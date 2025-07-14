package GUI;

import modello.Prenotazione;
import controller.Controller;
import modello.UtenteGenerico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class GUI_CercaPrenVoloRisult extends JFrame {
    private JPanel mainpanel;
    private JTable tabellaVoli;
    private JButton indietroButton;
    private JPanel buttonsPanel;
    private JPanel tabellaPanel;
    private DefaultTableModel modelTabella;
    private final UtenteGenerico utente;

    public GUI_CercaPrenVoloRisult(UtenteGenerico utente, String codiceVolo) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Risultati Ricerca Prenotazioni");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

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
                dispose();
            }
        });
    }

    private void caricaRisultati(String codiceVolo) {
        List<Prenotazione> prenotazioni = null;
        try {
            prenotazioni = Controller.getInstance()
                    .cercaPrenotazioniPerCodiceVolo(codiceVolo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (prenotazioni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessuna prenotazione trovata per il volo specificato",
                    "Nessun Risultato",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Prenotazione p : prenotazioni) {
            modelTabella.addRow(new Object[]{
                    p.getNumeroBiglietto(),
                    p.getNomePasseggero(),
                    p.getCognomePasseggero(),
                    p.getPostoAssegnato(),
                    p.getStato()
            });
        }
    }
}

