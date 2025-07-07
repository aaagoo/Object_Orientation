package GUI;

import controller.Controller;
import modello.AmministratoreSistema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GUI_ModificaAmministratori extends JFrame{
    private JPanel tabellaPanel;
    private JTable amministratoriTable;
    private JPanel operationsPanel;
    private JTextField passwordField;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField nomeutenteField;
    private JTextField rippasswordField;
    private JPanel buttonsPanel;
    private JButton confermaButton;
    private JButton annullaButton;
    private JButton eliminaButton;
    private JPanel mainpanel;
    private DefaultTableModel modelAmministratori;

    public GUI_ModificaAmministratori(AmministratoreSistema amministratore) {
        setContentPane(mainpanel);
        setTitle("modifica Account Amministratori");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        operationsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        buttonsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        tabellaPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        modelAmministratori = new DefaultTableModel(
                new String[]{"ID", "Nome Utente", "Password"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        amministratoriTable.setModel(modelAmministratori);
        aggiornaTabelle();

        amministratoriTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        amministratoriTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int riga = amministratoriTable.rowAtPoint(e.getPoint());

                if (riga > 0) {
                    String nomeutente = (String) amministratoriTable.getValueAt(amministratoriTable.getSelectedRow(), 1);
                    String password = (String) amministratoriTable.getValueAt(amministratoriTable.getSelectedRow(), 2);
                    nomeutenteField.setText(nomeutente);
                    passwordField.setText(password);
                    rippasswordField.setText(password);
                }
            }
        });

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_GestisciAccount(amministratore);
                dispose();
            }
        });
    }
    private void aggiornaTabelle() {
        try {
            modelAmministratori.setRowCount(0);
            modelAmministratori.addRow(new Object[]{
                    "ID",
                    "Nome Utente",
                    "Password"
            });

            List<Map<String, Object>> amministratori = Controller.getInstance().getTuttiAmministratori();
            for (Map<String, Object> amministratore : amministratori) {
                modelAmministratori.addRow(new Object[]{
                        amministratore.get("id"),
                        amministratore.get("nomeutente"),
                        amministratore.get("password")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Errore durante il caricamento dei dati: " + e.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
