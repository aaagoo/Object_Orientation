package gui;

import modello.*;
import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class GUI_GestisciPrenotazioni extends JFrame {


    private JPanel mainpanel;
    private JPanel tabellaPanel;
    private JTable prenotazioniTable;
    private JPanel operationsPanel;
    private JPanel codicePanel;
    private JTextField prenotazioneField;
    private JPanel statoPanel;
    private JComboBox statoComboBox;
    private JPanel buttonsPanel;
    private JButton confermaButton;
    private JButton annullaButton;
    private AmministratoreSistema utente;
    private DefaultTableModel modelPrenotazioni;


    public GUI_GestisciPrenotazioni(AmministratoreSistema utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Gestisci Prenotazioni");
        setSize(1000, 900);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        modelPrenotazioni = new DefaultTableModel(
                new String[]{"Numero Biglietto", "Nome Passeggero", "Cognome Passeggero",
                        "Codice Volo", "Posto", "Stato", "Username"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        prenotazioniTable.setModel(modelPrenotazioni);
        prenotazioniTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        aggiornaTabella();

        statoComboBox.setModel(new DefaultComboBoxModel<>(StatoPrenotazione.values()));

        prenotazioniTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (prenotazioniTable.getSelectedRow() > 0) {
                    String codiceVolo = (String) prenotazioniTable.getValueAt(prenotazioniTable.getSelectedRow(), 0);
                    prenotazioneField.setText(codiceVolo);
                }
            }
        });

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_HomeAmministratore(utente);
                dispose();
            }
        });

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String numeroBiglietto = prenotazioneField.getText().trim();
                    if (numeroBiglietto.isEmpty()) {
                        JOptionPane.showMessageDialog(GUI_GestisciPrenotazioni.this,
                                "Selezionare una prenotazione dalla tabella",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    List<Prenotazione> prenotazioni = Controller.getInstance().cercaPrenotazioniPerCodice(numeroBiglietto);
                    if (prenotazioni.isEmpty()) {
                        JOptionPane.showMessageDialog(GUI_GestisciPrenotazioni.this,
                                "Prenotazione non trovata",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Prenotazione prenotazione = prenotazioni.get(0);
                    StatoPrenotazione nuovoStato = (StatoPrenotazione) statoComboBox.getSelectedItem();
                    prenotazione.setStato(nuovoStato);

                    Controller.getInstance().aggiornaStatoPrenotazione(numeroBiglietto, nuovoStato);

                    aggiornaTabella();

                    prenotazioneField.setText("");

                    JOptionPane.showMessageDialog(GUI_GestisciPrenotazioni.this,
                            "Stato della prenotazione aggiornato con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(GUI_GestisciPrenotazioni.this,
                            "Errore durante l'aggiornamento della prenotazione: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GUI_GestisciPrenotazioni.this,
                            "Errore: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

    private void aggiornaTabella() {
        modelPrenotazioni.setRowCount(0);

        modelPrenotazioni.addRow(new Object[]{
                "Numero Biglietto",
                "Nome Passeggero",
                "Cog. Passeggero",
                "Codice Volo",
                "Posto",
                "Stato",
                "Username"
        });

        try {
            for (Prenotazione prenotazione : Controller.getInstance().getTuttePrenotazioni()) {
                modelPrenotazioni.addRow(new Object[]{
                        prenotazione.getNumeroBiglietto(),
                        prenotazione.getNomePasseggero(),
                        prenotazione.getCognomePasseggero(),
                        prenotazione.getVolo().getCodice(),
                        prenotazione.getPostoAssegnato(),
                        prenotazione.getStato(),
                        prenotazione.getUsernamePrenotazione()
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
