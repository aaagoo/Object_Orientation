package GUI;

import Controller.VoloController;
import Modello.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

public class GUI_VisualizzaVoliA extends JFrame {
    private JPanel mainpanel;
    private JButton indietroButton;
    private JButton voliButton;
    private JTable tabellavoli;
    private DefaultTableModel modelArrivi;
    private final Utente utente;

    public GUI_VisualizzaVoliA(Utente utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Visualizza Voli in Arrivo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


        modelArrivi = new DefaultTableModel(
                new String[]{"Codice", "Compagnia", "Destinazione", "Data", "Orario", "Ritardo", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellavoli.setModel(modelArrivi);
        modelArrivi.setRowCount(0);

        for (Volo_Arrivo volo : VoloController.getInstance().getVoliArrivo()) {

            if (volo.getRitardo() > 0 && volo.getStato() == Stato_Volo.Programmato) {
                volo.setStato(Stato_Volo.In_Ritardo);
            }

            String ritardoFormattato = volo.getRitardo() > 0 ?
                    volo.getRitardo() + " min" : "In orario";

            modelArrivi.addRow(new Object[]{
                    volo.getCodice(),
                    volo.getCompagnia_Aerea(),
                    volo.getAeroporto_Origine(),
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
                if (utente instanceof Amministratore_Del_Sistema) {
                    new GUI_HomeAmministratore((Amministratore_Del_Sistema) utente);
                } else {
                    new GUI_HomeUtente((Utente_Generico) utente);
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
