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


public class GUI_ModificaUtenti extends JFrame{

    private JPanel mainpanel;
    private JPanel tabellaPanel;
    private JTable utentiTable;
    private JPanel operationsPanel;
    private JTextField passwordField;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JPanel buttonsPanel;
    private JButton confermaButton;
    private JButton annullaButton;
    private JTextField nomeutenteField;
    private JTextField rippasswordField;
    private JButton eliminaButton;
    private DefaultTableModel modelUtenti;

    public GUI_ModificaUtenti(AmministratoreSistema amministratore) {
        setContentPane(mainpanel);
        setTitle("Modifica Account Utenti");
        setSize(1000, 800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

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
        aggiornaTabelle();

        utentiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        utentiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int riga = utentiTable.rowAtPoint(e.getPoint());

                if (riga > 0) {
                    String nomeutente = (String) utentiTable.getValueAt(utentiTable.getSelectedRow(), 1);
                    String nome = (String) utentiTable.getValueAt(utentiTable.getSelectedRow(), 3);
                    String cognome = (String) utentiTable.getValueAt(utentiTable.getSelectedRow(), 4);
                    String password = (String) utentiTable.getValueAt(utentiTable.getSelectedRow(), 2);
                    nomeField.setText(nome);
                    cognomeField.setText(cognome);
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
                String nome = nomeField.getText().trim();
                String cognome = cognomeField.getText().trim();
                String username = nomeutenteField.getText().trim();
                String password = passwordField.getText();
                String confermaPassword = rippasswordField.getText();

                if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() ||
                        password.isEmpty() || confermaPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(GUI_ModificaUtenti.this,
                            "Tutti i campi sono obbligatori",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confermaPassword)) {
                    JOptionPane.showMessageDialog(GUI_ModificaUtenti.this,
                            "Le password non coincidono",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Chiama il controller per modificare l'utente
                    Controller.getInstance().modificaUtente(username, nome, cognome, password);

                    // Aggiorna la tabella
                    aggiornaTabelle();

                    // Pulisci i campi
                    nomeField.setText("");
                    cognomeField.setText("");
                    nomeutenteField.setText("");
                    passwordField.setText("");
                    rippasswordField.setText("");

                    JOptionPane.showMessageDialog(GUI_ModificaUtenti.this,
                            "Utente modificato con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(GUI_ModificaUtenti.this,
                            "Errore durante la modifica dell'utente: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Controlliamo che sia stato selezionato un utente
                String username = nomeutenteField.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(GUI_ModificaUtenti.this,
                            "Seleziona un utente da eliminare",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Chiediamo conferma prima di eliminare
                int conferma = JOptionPane.showConfirmDialog(GUI_ModificaUtenti.this,
                        "Sei sicuro di voler eliminare l'utente " + username + "?",
                        "Conferma eliminazione",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        // Elimina l'utente
                        Controller.getInstance().eliminaUtente(username);

                        // Aggiorna la tabella
                        aggiornaTabelle();

                        // Pulisci i campi
                        nomeField.setText("");
                        cognomeField.setText("");
                        nomeutenteField.setText("");
                        passwordField.setText("");
                        rippasswordField.setText("");

                        JOptionPane.showMessageDialog(GUI_ModificaUtenti.this,
                                "Utente eliminato con successo",
                                "Successo",
                                JOptionPane.INFORMATION_MESSAGE);

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(GUI_ModificaUtenti.this,
                                "Errore durante l'eliminazione dell'utente: " + ex.getMessage(),
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void aggiornaTabelle() {
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
    }
}
