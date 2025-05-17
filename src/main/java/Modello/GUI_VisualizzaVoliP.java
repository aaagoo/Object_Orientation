package Modello;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;


public class GUI_VisualizzaVoliP extends JFrame {
    private JPanel mainpanel;
    private JTable tabellavoli;
    private JButton indietroButton;
    private JButton voliButton;
    private DefaultTableModel modelPartenze;
    private final Utente utente;

    public GUI_VisualizzaVoliP(Utente utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Visualizza Voli in Partenza");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


        modelPartenze = new DefaultTableModel(
                new String[]{"Codice", "Compagnia", "Destinazione", "Data", "Orario", "Ritardo", "Gate", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rende la tabella non editabile
            }
        };
        tabellavoli.setModel(modelPartenze);

        modelPartenze.setRowCount(0);

        for (Volo_Partenza volo : VoloController.getInstance().getVoliPartenza()) {

            if (volo.getRitardo() > 0 && volo.getStato() == Stato_Volo.Programmato) {
                volo.setStato(Stato_Volo.In_Ritardo);
            }

            String dataFormattata = volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String oraFormattata = volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm"));

            String ritardoFormattato;
            if (volo.getRitardo() > 0) {
                ritardoFormattato = volo.getRitardo() + " min";
            } else {
                ritardoFormattato = "In orario";
            }

            modelPartenze.addRow(new Object[]{
                    volo.getCodice(),
                    volo.getCompagnia_Aerea(),
                    volo.getAeroporto_Destinazione(),
                    dataFormattata,
                    oraFormattata,
                    ritardoFormattato,
                    volo.getGate(),
                    volo.getStato()
            });
        }


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (utente instanceof Amministratore_Del_Sistema) {
                    new GUI_HomeAmministratore((Amministratore_Del_Sistema) utente);
                } else {
                    new GUI_HomeUtente((Utente_Generico) utente);
                }
                dispose();
            }
        });

        voliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_VisualizzaVoliA(utente);
                dispose();
            }
        });
    }
}

