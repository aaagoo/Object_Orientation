package GUI;

import Controller.PrenotazioneController;
import Controller.VoloController;
import Modello.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;


public class GUI_PrenotaVolo extends JFrame {
    private JPanel mainpanel;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField CFField;
    private JTextField voloField;
    private JButton confermaButton;
    private JButton annullaButton;
    private JTable tabellaVoli;
    private DefaultTableModel modelVoli;
    private final Utente_Generico utente;

    public GUI_PrenotaVolo(Utente_Generico utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Prenota Volo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        modelVoli = new DefaultTableModel(
                new String[]{"Codice", "Compagnia", "Destinazione", "Data", "Orario", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaVoli.setModel(modelVoli);
        tabellaVoli.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        caricaVoliDisponibili();

        tabellaVoli.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tabellaVoli.getSelectedRow() != -1) {
                    String codiceVolo = (String) tabellaVoli.getValueAt(tabellaVoli.getSelectedRow(), 0);
                    voloField.setText(codiceVolo);
                }
            }
        });

        nomeField.setText(utente.getNome());
        cognomeField.setText(utente.getCognome());

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validaCampi()) {
                    effettuaPrenotazione();
                }
            }
        });

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_HomeUtente(utente);
                dispose();
            }
        });

    }

    private void caricaVoliDisponibili() {
        modelVoli.setRowCount(0);
        List<Volo_Partenza> voli = VoloController.getInstance().getVoliPartenza();

        for (Volo_Partenza volo : voli) {
            if (volo.getStato() == Stato_Volo.Programmato) {
                modelVoli.addRow(new Object[]{
                        volo.getCodice(),
                        volo.getCompagnia_Aerea(),
                        volo.getAeroporto_Destinazione(),
                        volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                        volo.getStato()
                });
            }
        }
    }

    private boolean validaCampi() {
        if (nomeField.getText().trim().isEmpty() ||
                cognomeField.getText().trim().isEmpty() ||
                CFField.getText().trim().isEmpty() ||
                voloField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Tutti i campi sono obbligatori!",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (CFField.getText().trim().length() != 16) {
            JOptionPane.showMessageDialog(this,
                    "Il codice fiscale deve essere di 16 caratteri!",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Volo volo = VoloController.getInstance().cercaVoloPerCodice(voloField.getText().trim());
        if (volo == null) {
            JOptionPane.showMessageDialog(this,
                    "Il codice volo inserito non è valido!",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void effettuaPrenotazione() {
        try {
            String codiceVolo = voloField.getText().trim();
            Volo volo = VoloController.getInstance().cercaVoloPerCodice(codiceVolo);

            Prenotazione prenotazione = PrenotazioneController.getInstance().creaPrenotazione(
                    nomeField.getText().trim(),
                    cognomeField.getText().trim(),
                    CFField.getText().trim(),
                    volo
            );

            JOptionPane.showMessageDialog(this,
                    "Prenotazione effettuata con successo!\nNumero biglietto: " +
                            prenotazione.getNumero_Biglietto() +
                            "\nPosto assegnato: " + prenotazione.getPosto_Assegnato(),
                    "Prenotazione Confermata",
                    JOptionPane.INFORMATION_MESSAGE);

            new GUI_HomeUtente(utente);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Errore durante la prenotazione: " + e.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
