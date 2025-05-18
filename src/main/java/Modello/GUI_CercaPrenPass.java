package Modello;

import javax.swing.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class GUI_CercaPrenPass extends JFrame {
    private JPanel mainpanel;
    private JButton cercaButton;
    private JButton annullaButton;
    private JButton cercaPerCodiceVoloButton;
    private JTextField nomeField;
    private JTextField cognomeField;
    private final Utente_Generico utente;

    public GUI_CercaPrenPass(Utente_Generico utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Cerca Prenotazione Passeggero");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Listener per il pulsante Cerca
        cercaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cercaPrenotazioni();
            }
        });

        // Listener per il pulsante Annulla
        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_HomeUtente(utente).setVisible(true);
            }
        });

        // Listener per il pulsante Cerca per Codice Volo
        cercaPerCodiceVoloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_CercaPrenVolo(utente);
            }
        });

        setVisible(true);
    }

    private void cercaPrenotazioni() {
        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();

        if (nome.isEmpty() || cognome.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Inserire sia nome che cognome per la ricerca",
                    "Campi Mancanti",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Prenotazione> prenotazioni = PrenotazioneController.getInstance()
                .cercaPrenotazioniPerPasseggero(nome, cognome);

        if (prenotazioni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessuna prenotazione trovata per il passeggero specificato",
                    "Ricerca Completata",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Creazione della finestra dei risultati
        JFrame risultatiFrame = new JFrame("Risultati Ricerca");
        risultatiFrame.setSize(800, 400);
        risultatiFrame.setLocationRelativeTo(this);

        // Creazione della tabella risultati
        DefaultTableModel modelRisultati = new DefaultTableModel(
                new String[]{"Numero Biglietto", "Volo", "Data", "Orario", "Posto", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabellaRisultati = new JTable(modelRisultati);
        JScrollPane scrollPane = new JScrollPane(tabellaRisultati);

        // Popolamento della tabella
        for (Prenotazione p : prenotazioni) {
            Volo volo = p.getVolo();
            modelRisultati.addRow(new Object[]{
                    p.getNumero_Biglietto(),
                    volo.getCodice(),
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    p.getPosto_Assegnato(),
                    p.getStato()
            });
        }

        // Aggiunta della tabella alla finestra
        risultatiFrame.add(scrollPane);

        // Aggiunta pulsante di chiusura
        JPanel buttonPanel = new JPanel();
        JButton chiudiButton = new JButton("Chiudi");
        chiudiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                risultatiFrame.dispose();
            }
        });
        buttonPanel.add(chiudiButton);
        risultatiFrame.add(buttonPanel, BorderLayout.SOUTH);

        risultatiFrame.setVisible(true);
    }


}
