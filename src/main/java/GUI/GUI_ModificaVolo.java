package GUI;

import controller.Controller;
import modello.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GUI_ModificaVolo extends JFrame {


    private JPanel tabellaPanel;
    private JTable prenotazioniTable;
    private JPanel mainpanel;
    private JPanel operationsPanel;
    private JPanel codicePanel;
    private JTextField codiceVoloField;
    private JPanel ritardoPanel;
    private JTextField nuovocodiceField;
    private JPanel statoPanel;
    private JPanel buttonsPanel;
    private JButton confermaButton;
    private JButton annullaButton;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JPanel prenotazioniPanel;
    private JTable partenzeTable;
    private JPanel partenzePanel;
    private DefaultTableModel modelTabella;
    private DefaultTableModel modelPartenze;

    public GUI_ModificaVolo(UtenteGenerico utente) {
        setContentPane(mainpanel);
        setTitle("Modifica Volo");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        operationsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        buttonsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        tabellaPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        prenotazioniPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(240, 240, 240)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        partenzePanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(240, 240, 240)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));


        modelTabella = new DefaultTableModel(
                new String[]{"Nome", "Cognome", "Numero Biglietto", "Posto", "Volo", "Data", "Orario", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        prenotazioniTable.setModel(modelTabella);
        prenotazioniTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        caricaRisultati(utente.getNome(), utente.getCognome());

        modelPartenze = new DefaultTableModel(
                new String[]{"Codice", "Data", "Orario", "Ritardo", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        partenzeTable.setModel(modelPartenze);
        modelPartenze.setRowCount(0);

        modelPartenze.addRow(new Object[]{
                "Codice",
                "Data",
                "Orario",
                "Ritardo",
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
                    dataFormattata,
                    oraFormattata,
                    ritardoFormattato,
                    volo.getStato()
            });
        }


        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_HomeUtente(utente);
                dispose();
            }
        });


        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void caricaRisultati(String nome, String cognome) {
        List<Prenotazione> prenotazioni = Controller.getInstance()
                .cercaPrenotazioniPerCreatore(nome, cognome);

        modelTabella.addRow(new Object[]{
                "N. Biglietto",
                "Nome",
                "Cognome",
                "Volo",
                "Posto",
                "Data",
                "Orario",
                "Stato"
        });

        for (Prenotazione p : prenotazioni) {
            Volo volo = p.getVolo();
            modelTabella.addRow(new Object[]{
                    p.getNumeroBiglietto(),
                    p.getNomePasseggero(),
                    p.getCognomePasseggero(),
                    volo.getCodice(),
                    p.getPostoAssegnato(),
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    p.getStato()
            });
        }
    }
}
