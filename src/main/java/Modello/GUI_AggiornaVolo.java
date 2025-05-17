package Modello;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;


public class GUI_AggiornaVolo extends JFrame {
    private JPanel mainpanel;
    private JTextField codiceVoloField;
    private JTextField ritardoField;
    private JComboBox<Stato_Volo> statoComboBox;
    private JButton confermaButton;
    private JButton annullaButton;
    private JTable tabellaVoli;
    private DefaultTableModel modelVoli;
    private Amministratore_Del_Sistema utente;

    public GUI_AggiornaVolo(Amministratore_Del_Sistema utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Aggiorna Volo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


        // Inizializza la tabella dei voli
        modelVoli = new DefaultTableModel(
                new String[]{"Codice", "Tipo", "Compagnia", "Origine/Destinazione",
                        "Data", "Orario", "Ritardo", "Gate", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rende la tabella non editabile
            }
        };

        tabellaVoli.setModel(modelVoli);

        // Inizializza la combo box degli stati
        statoComboBox.setModel(new DefaultComboBoxModel<>(Stato_Volo.values()));

        // Carica i voli nella tabella
        aggiornaTabella();

        // Listener per la selezione nella tabella
        tabellaVoli.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabellaVoli.getSelectedRow() != -1) {
                int row = tabellaVoli.getSelectedRow();
                codiceVoloField.setText((String) tabellaVoli.getValueAt(row, 0));
                ritardoField.setText(tabellaVoli.getValueAt(row, 6).toString().replace(" min", ""));
                statoComboBox.setSelectedItem(Stato_Volo.valueOf(tabellaVoli.getValueAt(row, 8).toString()));
            }
        });

        confermaButton.addActionListener(e -> {
            try {
                String codiceVolo = codiceVoloField.getText().trim();
                if (codiceVolo.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Selezionare un volo dalla tabella",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Trova il volo
                Volo volo = VoloController.getInstance().cercaVoloPerCodice(codiceVolo);
                if (volo == null) {
                    JOptionPane.showMessageDialog(this,
                            "Volo non trovato",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Aggiorna ritardo
                long nuovoRitardo = Long.parseLong(ritardoField.getText().trim());
                if (nuovoRitardo < 0) {
                    throw new IllegalArgumentException("Il ritardo non può essere negativo");
                }
                volo.setRitardo(nuovoRitardo);

                // Aggiorna stato
                Stato_Volo nuovoStato = (Stato_Volo) statoComboBox.getSelectedItem();
                volo.setStato(nuovoStato);

                // Se c'è ritardo, aggiorna automaticamente lo stato
                if (nuovoRitardo > 0 && nuovoStato == Stato_Volo.Programmato) {
                    volo.setStato(Stato_Volo.In_Ritardo);
                }

                // Aggiorna la tabella
                aggiornaTabella();

                // Pulisci i campi
                codiceVoloField.setText("");
                ritardoField.setText("0");
                statoComboBox.setSelectedItem(Stato_Volo.Programmato);

                JOptionPane.showMessageDialog(this,
                        "Volo aggiornato con successo!",
                        "Successo",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Valore del ritardo non valido.\nInserire un numero intero.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Errore nell'aggiornamento: " + ex.getMessage(),
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        annullaButton.addActionListener(e -> {
            dispose();
            new GUI_HomeAmministratore(utente);
        });

    }

    private void aggiornaTabella() {
        modelVoli.setRowCount(0);

        // Aggiungi tutti i voli
        for (Volo volo : VoloController.getInstance().getAllVoli()) {

            if (volo.getRitardo() > 0 && volo.getStato() == Stato_Volo.Programmato) {
                volo.setStato(Stato_Volo.In_Ritardo);
            }

            String tipo = volo instanceof Volo_Arrivo ? "Arrivo" : "Partenza";
            String origine_destinazione = volo instanceof Volo_Arrivo ?
                    ((Volo_Arrivo) volo).getAeroporto_Origine() :
                    ((Volo_Partenza) volo).getAeroporto_Destinazione();
            String gate = volo instanceof Volo_Partenza ?
                    (((Volo_Partenza) volo).getGate() != null ?
                            String.valueOf(((Volo_Partenza) volo).getGate().getNumero_Gate()) :
                            "Non assegnato") :
                    "N/A";

            modelVoli.addRow(new Object[]{
                    volo.getCodice(),
                    tipo,
                    volo.getCompagnia_Aerea(),
                    origine_destinazione,
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    volo.getRitardo() > 0 ? volo.getRitardo() + " min" : "In orario",
                    gate,
                    volo.getStato()
            });
        }
    }
}

