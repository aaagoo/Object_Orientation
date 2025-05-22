package Modello;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GUI_CercaPrenPassRisult extends JFrame {
    private JPanel mainpanel;
    private JTable tabellaVoli;
    private JButton indietroButton;
    private DefaultTableModel modelTabella;
    private final Utente_Generico utente;

    public GUI_CercaPrenPassRisult(Utente_Generico utente, String nome, String cognome) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Risultati Ricerca Prenotazioni");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        modelTabella = new DefaultTableModel(
                new String[]{"Numero Biglietto", "Volo", "Data", "Orario", "Posto", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaVoli.setModel(modelTabella);
        tabellaVoli.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        caricaRisultati(nome, cognome);

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_CercaPrenPass(utente);
                dispose();
            }
        });
    }

    private void caricaRisultati(String nome, String cognome) {
        List<Prenotazione> prenotazioni = PrenotazioneController.getInstance()
                .cercaPrenotazioniPerPasseggero(nome, cognome);

        if (prenotazioni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessuna prenotazione trovata per il passeggero specificato",
                    "Nessun Risultato",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Prenotazione p : prenotazioni) {
            Volo volo = p.getVolo();
            modelTabella.addRow(new Object[]{
                    p.getNumero_Biglietto(),
                    volo.getCodice(),
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    p.getPosto_Assegnato(),
                    p.getStato()
            });
        }
    }
}