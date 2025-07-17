package gui;

import controller.Controller;
import modello.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;


public class GUI_VisualizzaVoliP extends JFrame {
    private JPanel mainpanel;
    private JTable voliTable;
    private JButton indietroButton;
    private JButton voliButton;
    private JPanel operazioniPanel;
    private JPanel tabellaPanel;
    private DefaultTableModel modelPartenze;
    private final Utente utente;

    public GUI_VisualizzaVoliP(Utente utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Visualizza Voli in Partenza");
        setSize(1000, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

        modelPartenze = new DefaultTableModel(
                new String[]{"Codice", "Compagnia", "Destinazione", "Data", "Orario", "Ritardo", "Gate", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        voliTable.setModel(modelPartenze);
        modelPartenze.setRowCount(0);

        voliTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        modelPartenze.addRow(new Object[]{
                "Codice",
                "Compagnia",
                "Destinazione",
                "Data",
                "Orario",
                "Ritardo",
                "Gate",
                "Stato"
        });

        for (VoloPartenza volo : Controller.getInstance().getVoliPartenza()) {

            if (volo.getRitardo() > 0 && volo.getStato() == StatoVolo.PROGRAMMATO) {
                volo.setStato(StatoVolo.IN_RITARDO);
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
                    volo.getCompagniaAerea(),
                    volo.getAeroportoDestinazione(),
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

                if (utente instanceof AmministratoreSistema) {
                    new GUI_HomeAmministratore((AmministratoreSistema) utente);
                } else {
                    new GUI_HomeUtente((UtenteGenerico) utente);
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

