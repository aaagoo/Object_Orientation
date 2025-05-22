package Modello;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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


        modelVoli = new DefaultTableModel(
                new String[]{"Codice", "Tipo", "Compagnia", "Origine/Destinazione",
                        "Data", "Orario", "Ritardo", "Gate", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaVoli.setModel(modelVoli);

        statoComboBox.setModel(new DefaultComboBoxModel<>(Stato_Volo.values()));

        aggiornaTabella();

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
                    String codiceVolo = codiceVoloField.getText().trim();
                    if (codiceVolo.isEmpty()) {
                        JOptionPane.showMessageDialog(GUI_AggiornaVolo.this,
                                "Selezionare un volo dalla tabella",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Volo volo = VoloController.getInstance().cercaVoloPerCodice(codiceVolo);
                    if (volo == null) {
                        JOptionPane.showMessageDialog(GUI_AggiornaVolo.this,
                                "Volo non trovato",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    long nuovoRitardo = Long.parseLong(ritardoField.getText().trim());
                    if (nuovoRitardo < 0) {
                        throw new IllegalArgumentException("Il ritardo non può essere negativo");
                    }
                    volo.setRitardo(nuovoRitardo);

                    Stato_Volo nuovoStato = (Stato_Volo) statoComboBox.getSelectedItem();
                    volo.setStato(nuovoStato);

                    if (nuovoRitardo > 0 && nuovoStato == Stato_Volo.Programmato) {
                        volo.setStato(Stato_Volo.In_Ritardo);
                    }

                    aggiornaTabella();

                    codiceVoloField.setText("");
                    ritardoField.setText("0");
                    statoComboBox.setSelectedItem(Stato_Volo.Programmato);

                    JOptionPane.showMessageDialog(GUI_AggiornaVolo.this,
                            "Volo aggiornato con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI_AggiornaVolo.this,
                            "Valore del ritardo non valido.\nInserire un numero intero.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GUI_AggiornaVolo.this,
                            "Errore nell'aggiornamento: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void aggiornaTabella() {
        modelVoli.setRowCount(0);

        for (Volo volo : VoloController.getInstance().getAllVoli()) {

            if (volo.getRitardo() > 0 && volo.getStato() == Stato_Volo.Programmato) {
                volo.setStato(Stato_Volo.In_Ritardo);
            }

            String tipo = volo instanceof Volo_Arrivo ? "Arrivo" : "Partenza";
            String origine_destinazione = volo instanceof Volo_Arrivo ?
                    (volo).getAeroporto_Origine() :
                    (volo).getAeroporto_Destinazione();
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

