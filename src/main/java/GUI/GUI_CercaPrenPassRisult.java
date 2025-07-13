
package GUI;

import modello.Prenotazione;
import controller.Controller;
import modello.UtenteGenerico;
import modello.Volo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GUI_CercaPrenPassRisult extends JFrame {
    private JPanel mainpanel;
    private JTable tabellaVoli;
    private JButton indietroButton;
    private JPanel tabellaPanel;
    private JPanel buttonsPanel;
    private DefaultTableModel modelTabella;
    private final UtenteGenerico utente;

    public GUI_CercaPrenPassRisult(UtenteGenerico utente, String nome, String cognome) {
        this.utente = utente;
        initializeComponents();
        setupUI();
        caricaRisultati(nome, cognome);
        setupListeners();
    }

    private void initializeComponents() {
        setContentPane(mainpanel);
        setTitle("Risultati Ricerca Prenotazioni");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    private void setupUI() {
        mainpanel.setBorder(null);

        tabellaPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        buttonsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

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
    }

    private void setupListeners() {
        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void caricaRisultati(String nome, String cognome) {
        List<Prenotazione> prenotazioni = new ArrayList<>();

        try {
            prenotazioni = Controller.getInstance().cercaPrenotazioniPerPasseggero(nome, cognome);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Errore durante la ricerca delle prenotazioni: " + e.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (prenotazioni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessuna prenotazione trovata per il passeggero specificato",
                    "Nessun Risultato",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        modelTabella.setRowCount(0);
        for (Prenotazione p : prenotazioni) {
            Volo volo = p.getVolo();
            modelTabella.addRow(new Object[]{
                    p.getNumeroBiglietto(),
                    volo.getCodice(),
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    p.getPostoAssegnato(),
                    p.getStato()
            });
        }
    }
}