package gui;

import controller.Controller;
import modello.AmministratoreSistema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

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

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeutente = nomeutenteField.getText();
                String nuovaPassword = passwordField.getText();
                String confermaPassword = rippasswordField.getText();

                if (nomeutente.isEmpty() || nuovaPassword.isEmpty() || confermaPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Tutti i campi sono obbligatori!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!nuovaPassword.equals(confermaPassword)) {
                    JOptionPane.showMessageDialog(null,
                            "Le password non coincidono!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    Controller.getInstance().modificaAdmin(nomeutente, nuovaPassword);
                    JOptionPane.showMessageDialog(null,
                            "Password modificata con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);
                    aggiornaTabelle();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Errore durante la modifica della password: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeutente = nomeutenteField.getText();

                if (nomeutente.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Seleziona un amministratore dalla tabella!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                    try {
                        Controller.getInstance().eliminaAdmin(nomeutente);

                        aggiornaTabelle();
                        nomeutenteField.setText("");
                        passwordField.setText("");
                        rippasswordField.setText("");

                        JOptionPane.showMessageDialog(null,
                                "Amministratore eliminato con successo!",
                                "Successo",
                                JOptionPane.INFORMATION_MESSAGE);

                        if (nomeutente.equals(amministratore.getNomeUtente())) {

                            JOptionPane.showMessageDialog(null,
                                    "Hai eliminato il tuo account.\n" +
                                            "Sarai reindirizzato alla pagina di login.",
                                    "Avviso",
                                    JOptionPane.INFORMATION_MESSAGE);

                            new GUI_Login();
                            dispose();
                        }

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Errore durante l'eliminazione dell'amministratore: " + ex.getMessage(),
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    }
            }
        });
    }
    private void aggiornaTabelle() {
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
    }
}
