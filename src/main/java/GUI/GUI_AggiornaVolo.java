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


public class GUI_AggiornaVolo extends JFrame {
    private JPanel mainpanel;
    private JTextField codiceVoloField;
    private JTextField ritardoField;
    private JComboBox<StatoVolo> statoComboBox;
    private JButton confermaButton;
    private JButton annullaButton;
    private JTable tabellaVoli;
    private JPanel codicePanel;
    private JPanel ritardoPanel;
    private JPanel statoPanel;
    private JPanel operationsPanel;
    private JPanel buttonsPanel;
    private JPanel tabellaPanel;
    private DefaultTableModel modelVoli;
    private AmministratoreSistema utente;

    public GUI_AggiornaVolo(AmministratoreSistema utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Aggiorna Volo");
        setSize(1000, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

        modelVoli = new DefaultTableModel(
                new String[]{"Codice", "Tipo", "Compagnia", "Orig/Dest",
                        "Data", "Orario", "Ritardo", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaVoli.setModel(modelVoli);
        tabellaVoli.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        statoComboBox.setModel(new DefaultComboBoxModel<>(StatoVolo.values()));

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

                    Volo volo = Controller.getInstance().cercaVoloPerCodice(codiceVolo);
                    if (volo == null) {
                        JOptionPane.showMessageDialog(GUI_AggiornaVolo.this,
                                "Volo non trovato",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    long nuovoRitardo = Long.parseLong(ritardoField.getText().trim());

                    StatoVolo nuovoStato = (StatoVolo) statoComboBox.getSelectedItem();

                    Controller.getInstance().aggiornaStatoVolo(codiceVolo, nuovoStato, nuovoRitardo);

                    aggiornaTabella();

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

                codiceVoloField.setText("");
                ritardoField.setText("0");
                statoComboBox.setSelectedItem(StatoVolo.PROGRAMMATO);
            }
        });


        tabellaVoli.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                String codiceVolo = (String) tabellaVoli.getValueAt(tabellaVoli.getSelectedRow(), 0);
                codiceVoloField.setText(codiceVolo);
            }
        });
    }

    private void aggiornaTabella() {
        modelVoli.setRowCount(0);

        modelVoli.addRow(new Object[]{
                "Codice",
                "Tipo",
                "Compagnia",
                "Orig/Dest",
                "Data",
                "Orario",
                "Ritardo",
                "Stato"
        });


        for (Volo volo : Controller.getInstance().getAllVoli()) {

            String tipo = volo instanceof VoloArrivo ? "Arrivo" : "Partenza";
            String origine_destinazione = volo instanceof VoloArrivo ?
                    (volo).getAeroportoOrigine() :
                    (volo).getAeroportoDestinazione();
            String gate = volo instanceof VoloPartenza ?
                    (((VoloPartenza) volo).getGate() != null ?
                            String.valueOf(((VoloPartenza) volo).getGate().getNumeroGate()) :
                            "Non assegnato") :
                    "N/A";

            modelVoli.addRow(new Object[]{
                    volo.getCodice(),
                    tipo,
                    volo.getCompagniaAerea(),
                    origine_destinazione,
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    volo.getRitardo() > 0 ? volo.getRitardo() + " min" : "In orario",
                    volo.getStato()
            });
        }
    }
}

