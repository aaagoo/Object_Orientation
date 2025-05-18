package Modello;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GUI_CercaPrenVolo extends JFrame {
    private JPanel mainpanel;
    private JTextField codicevoloField;
    private JButton cercaButton;
    private JButton annullaButton;
    private JButton cercaPerDatiPasseggeroButton;
    private JTable tabellaVoli;
    private DefaultTableModel modelVoli;
    private final Utente_Generico utente;

    public GUI_CercaPrenVolo(Utente_Generico utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Cerca Prenotazioni per Volo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inizializzazione tabella voli
        modelVoli = new DefaultTableModel(
                new String[]{"Codice", "Compagnia", "Destinazione", "Data", "Orario", "Ritardo", "Gate", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaVoli.setModel(modelVoli);
        tabellaVoli.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaVoli.setAutoCreateRowSorter(true);

        // Caricamento voli in partenza
        caricaVoliPartenza();

        // Listener per la selezione nella tabella
        tabellaVoli.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabellaVoli.getSelectedRow() != -1) {
                String codiceVolo = (String) tabellaVoli.getValueAt(tabellaVoli.getSelectedRow(), 0);
                codicevoloField.setText(codiceVolo);
            }
        });

        cercaButton.addActionListener(e -> mostraPrenotazioni());

        annullaButton.addActionListener(e -> {
            dispose();
            new GUI_HomeUtente(utente).setVisible(true);
        });

        cercaPerDatiPasseggeroButton.addActionListener(e -> {
            dispose();
            new GUI_CercaPrenPass(utente).setVisible(true);
        });

        setVisible(true);
    }

    private void caricaVoliPartenza() {
        modelVoli.setRowCount(0);
        List<Volo_Partenza> voliPartenza = VoloController.getInstance().getVoliPartenza();

        for (Volo_Partenza volo : voliPartenza) {
            if (volo.getRitardo() > 0 && volo.getStato() == Stato_Volo.Programmato) {
                volo.setStato(Stato_Volo.In_Ritardo);
            }

            String dataFormattata = volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String oraFormattata = volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm"));
            String ritardoFormattato = volo.getRitardo() > 0 ? volo.getRitardo() + " min" : "In orario";
            String gate = volo.getGate() != null ? String.valueOf(volo.getGate().getNumero_Gate()) : "Non Assegnato";

            modelVoli.addRow(new Object[]{
                    volo.getCodice(),
                    volo.getCompagnia_Aerea(),
                    volo.getAeroporto_Destinazione(),
                    dataFormattata,
                    oraFormattata,
                    ritardoFormattato,
                    gate,
                    volo.getStato()
            });
        }
    }

    private void mostraPrenotazioni() {
        String codiceVolo = codicevoloField.getText().trim();

        if (codiceVolo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Inserire un codice volo!",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Prenotazione> prenotazioni = PrenotazioneController.getInstance()
                .cercaPrenotazioniPerCodiceVolo(codiceVolo);

        if (prenotazioni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessuna prenotazione trovata per il volo specificato",
                    "Informazione",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Creazione della finestra dei risultati
        JFrame risultatiFrame = new JFrame("Prenotazioni per il volo " + codiceVolo);
        risultatiFrame.setSize(800, 400);
        risultatiFrame.setLocationRelativeTo(this);

        // Creazione della tabella risultati
        DefaultTableModel modelRisultati = new DefaultTableModel(
                new String[]{"Numero Biglietto", "Nome", "Cognome", "CF", "Posto", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabellaRisultati = new JTable(modelRisultati);
        JScrollPane scrollPane = new JScrollPane(tabellaRisultati);

        // Popolamento della tabella risultati
        for (Prenotazione p : prenotazioni) {
            modelRisultati.addRow(new Object[]{
                    p.getNumero_Biglietto(),
                    p.getNome_Passeggero(),
                    p.getCognome_Passeggero(),
                    p.getCodice_Fiscale(),
                    p.getPosto_Assegnato(),
                    p.getStato()
            });
        }

        // Aggiunta della tabella alla finestra
        risultatiFrame.add(scrollPane);

        // Aggiunta pulsante di chiusura
        JPanel buttonPanel = new JPanel();
        JButton chiudiButton = new JButton("Chiudi");
        chiudiButton.addActionListener(e -> risultatiFrame.dispose());
        buttonPanel.add(chiudiButton);
        risultatiFrame.add(buttonPanel, BorderLayout.SOUTH);

        risultatiFrame.setVisible(true);
    }
}