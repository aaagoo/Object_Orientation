package GUI;

import controller.Controller;
import modello.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.sql.SQLException;


public class GUI_PrenotaVolo extends JFrame {
    private JPanel mainpanel;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField CFField;
    private JTextField voloField;
    private JButton confermaButton;
    private JButton annullaButton;
    private JTable tabellaVoli;
    private JPanel buttonsPanel;
    private JPanel tabellaPanel;
    private JPanel operationsPanel;
    private DefaultTableModel modelVoli;
    private final UtenteGenerico utente;

    public GUI_PrenotaVolo(UtenteGenerico utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Prenota Volo");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        mainpanel.setBorder(null);

        modelVoli = new DefaultTableModel(
                new String[]{"Codice", "Compagnia", "Destinazione", "Data", "Orario"},
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

        tabellaVoli.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (tabellaVoli.getSelectedRow() > 0) {
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
        List<VoloPartenza> voli = Controller.getInstance().getVoliPartenza();

        modelVoli.addRow(new Object[]{
                "Codice",
                "Compagnia",
                "Destinazione",
                "Data",
                "Orario"
        });

        for (VoloPartenza volo : voli) {
            if (volo.getStato() == StatoVolo.PROGRAMMATO || volo.getStato() == StatoVolo.IN_RITARDO) {

                modelVoli.addRow(new Object[]{
                        volo.getCodice(),
                        volo.getCompagniaAerea(),
                        volo.getAeroportoDestinazione(),
                        volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                });
            }
        }
    }

    private boolean validaCampi() {
        if (nomeField.getText().trim().isEmpty() ||
                cognomeField.getText().trim().isEmpty() ||
                voloField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Tutti i campi sono obbligatori!",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Volo volo = Controller.getInstance().cercaVoloPerCodice(voloField.getText().trim());
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
            Volo volo = Controller.getInstance().cercaVoloPerCodice(codiceVolo);

            Prenotazione prenotazione = Controller.getInstance().creaPrenotazione(
                    nomeField.getText().trim(),
                    cognomeField.getText().trim(),
                    volo,
                    utente
            );

            JOptionPane.showMessageDialog(this,
                    "Prenotazione effettuata con successo!\nNumero biglietto: " +
                            prenotazione.getNumeroBiglietto() +
                            "\nPosto assegnato: " + prenotazione.getPostoAssegnato(),
                    "Prenotazione Confermata",
                    JOptionPane.INFORMATION_MESSAGE);

            new GUI_HomeUtente(utente);
            dispose();

        } catch (SQLException e) {
            String messaggio;
            if (e.getMessage().contains("unique_passeggero_volo")) {
                messaggio = "Non è possibile effettuare la prenotazione:\nIl passeggero ha già prenotato questo volo!";
            } else {
                messaggio = "Errore durante la prenotazione: " + e.getMessage();
            }

            JOptionPane.showMessageDialog(this,
                    messaggio,
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Errore durante la prenotazione: " + e.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
