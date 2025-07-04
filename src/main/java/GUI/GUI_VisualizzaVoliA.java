package GUI;

import controller.Controller;
import modello.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

public class GUI_VisualizzaVoliA extends JFrame {
    private JPanel mainpanel;
    private JButton indietroButton;
    private JButton voliButton;
    private JTable voliTable;
    private JPanel operazioniPanel;
    private JPanel tabellaPanel;
    private DefaultTableModel modelArrivi;
    private final Utente utente;

    public GUI_VisualizzaVoliA(Utente utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Visualizza Voli in Arrivo");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

        operazioniPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        tabellaPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        modelArrivi = new DefaultTableModel(
                new String[]{"Codice", "Compagnia", "Destinazione", "Data", "Orario", "Ritardo", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        voliTable.setModel(modelArrivi);
        modelArrivi.setRowCount(0);
        voliTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        modelArrivi.addRow(new Object[]{
                "Codice",
                "Compagnia",
                "Partenza",
                "Data",
                "Orario",
                "Ritardo",
                "Stato"
        });

        for (VoloArrivo volo : Controller.getInstance().getVoliArrivo()) {

            if (volo.getRitardo() > 0 && volo.getStato() == StatoVolo.PROGRAMMATO) {
                volo.setStato(StatoVolo.IN_RITARDO);
            }

            String ritardoFormattato = volo.getRitardo() > 0 ?
                    volo.getRitardo() + " min" : "In orario";

            modelArrivi.addRow(new Object[]{
                    volo.getCodice(),
                    volo.getCompagniaAerea(),
                    volo.getAeroportoOrigine(),
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    ritardoFormattato,
                    volo.getStato()
            });
        }

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                if (utente instanceof AmministratoreSistema) {
                    new GUI_HomeAmministratore((AmministratoreSistema) utente);
                } else {
                    new GUI_HomeUtente((UtenteGenerico) utente);
                }

            }
        });

        voliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_VisualizzaVoliP(utente);
                dispose();
            }
        });

    }

}
