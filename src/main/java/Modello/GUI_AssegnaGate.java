package Modello;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

public class GUI_AssegnaGate extends JFrame {
    private JPanel mainpanel;
    private JTextField codiceVoloField;
    private JTextField gateField;
    private JButton annullaButton;
    private JButton confermaButton;
    private JTable tabellaVoli;
    private DefaultTableModel modelVoli;
    private Amministratore_Del_Sistema utente;


    public GUI_AssegnaGate(Amministratore_Del_Sistema utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Assegna Gate");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Inizializza la tabella dei voli
        modelVoli = new DefaultTableModel(
                new String[]{"Codice", "Compagnia", "Destinazione", "Data", "Orario", "Gate", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rende la tabella non editabile
            }
        };
        tabellaVoli.setModel(modelVoli);

        // Carica i voli senza gate assegnato
        aggiornaTabella();


        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String codiceVolo = codiceVoloField.getText();
                    int numeroGate = Integer.parseInt(gateField.getText());

                    // Trova il volo
                    Volo_Partenza volo = VoloController.getInstance().trovaVoloPartenza(codiceVolo);
                    if (volo == null) {
                        JOptionPane.showMessageDialog(GUI_AssegnaGate.this,
                                "Volo non trovato",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Assegna il gate
                    volo.setGate(new Gate(numeroGate));

                    // Aggiorna la tabella
                    aggiornaTabella();

                    // Pulisci i campi
                    codiceVoloField.setText("");
                    gateField.setText("");

                    JOptionPane.showMessageDialog(GUI_AssegnaGate.this,
                            "Gate assegnato con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI_AssegnaGate.this,
                            "Numero gate non valido",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_HomeAmministratore(utente);
            }
        });
    }

    private void aggiornaTabella() {
        modelVoli.setRowCount(0);
        for (Volo_Partenza volo : VoloController.getInstance().getVoliPartenza()) {

            if (volo.getRitardo() > 0 && volo.getStato() == Stato_Volo.Programmato) {
                volo.setStato(Stato_Volo.In_Ritardo);
            }

            modelVoli.addRow(new Object[]{
                    volo.getCodice(),
                    volo.getCompagnia_Aerea(),
                    volo.getAeroporto_Destinazione(),
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    volo.getGate() != null ? volo.getGate().getNumero_Gate() : "Non assegnato",
                    volo.getStato()
            });
        }
    }
}
