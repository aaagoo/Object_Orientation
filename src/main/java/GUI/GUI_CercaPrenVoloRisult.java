package GUI;

import Modello.Prenotazione;
import Controller.PrenotazioneController;
import Modello.Utente_Generico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GUI_CercaPrenVoloRisult extends JFrame {
    private JPanel mainpanel;
    private JTable tabellaVoli;
    private JButton indietroButton;
    private DefaultTableModel modelTabella;
    private final Utente_Generico utente;

    public GUI_CercaPrenVoloRisult(Utente_Generico utente, String codiceVolo) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Risultati Ricerca Prenotazioni");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        modelTabella = new DefaultTableModel(
                new String[]{"Numero Biglietto", "Nome", "Cognome", "CF", "Posto", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaVoli.setModel(modelTabella);
        tabellaVoli.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        caricaRisultati(codiceVolo);

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_CercaPrenVolo(utente);
                dispose();
            }
        });
    }

    private void caricaRisultati(String codiceVolo) {
        List<Prenotazione> prenotazioni = PrenotazioneController.getInstance()
                .cercaPrenotazioniPerCodiceVolo(codiceVolo);

        if (prenotazioni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessuna prenotazione trovata per il volo specificato",
                    "Nessun Risultato",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Prenotazione p : prenotazioni) {
            modelTabella.addRow(new Object[]{
                    p.getNumero_Biglietto(),
                    p.getNome_Passeggero(),
                    p.getCognome_Passeggero(),
                    p.getCodice_Fiscale(),
                    p.getPosto_Assegnato(),
                    p.getStato()
            });
        }
    }
}

