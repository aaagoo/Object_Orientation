package GUI;

import modello.AmministratoreSistema;
import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

public class GUI_GestisciAccount extends JFrame {

    private JPanel mainpanel;
    private JPanel tablePanel;
    private JPanel utentiPanel;
    private JTable utentiTable;
    private JPanel amministratoriPanel;
    private JTable amministratoriTable;
    private JPanel operationsPanel;
    private JButton modificaUtentiButton;
    private JButton modificaAmministratoriButton;
    private JButton indietroButton;
    private DefaultTableModel modelUtenti;
    private DefaultTableModel modelAmministratori;

    public GUI_GestisciAccount(AmministratoreSistema amministratore) {
        setContentPane(mainpanel);
        setTitle("Gestione Account");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        utentiPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        amministratoriPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        operationsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        modelUtenti = new DefaultTableModel(
                new String[]{"ID", "Nome Utente", "Password", "Nome", "Cognome"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        utentiTable.setModel(modelUtenti);

        utentiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        amministratoriTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_HomeAmministratore(amministratore);
                dispose();
            }
        });

        modificaUtentiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_ModificaUtenti(amministratore);
                dispose();
            }
        });

        modificaAmministratoriButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_ModificaAmministratori(amministratore);
                dispose();
            }
        });
    }

    private void aggiornaTabelle() {
        try {
            modelUtenti.setRowCount(0);
            modelUtenti.addRow(new Object[]{
                    "ID",
                    "Nome Utente",
                    "Password",
                    "Nome",
                    "Cognome"
            });

            List<Map<String, Object>> utenti = Controller.getInstance().getTuttiUtenti();
            for (Map<String, Object> utente : utenti) {
                modelUtenti.addRow(new Object[]{
                        utente.get("id"),
                        utente.get("nomeutente"),
                        utente.get("password"),
                        utente.get("nome"),
                        utente.get("cognome")
                });
            }

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
