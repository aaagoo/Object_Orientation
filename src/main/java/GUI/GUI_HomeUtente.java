package GUI;

import controller.Controller;
import modello.StatoVolo;
import modello.UtenteGenerico;
import modello.VoloArrivo;
import modello.VoloPartenza;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;


public class GUI_HomeUtente extends JFrame{
    private JPanel mainpanel;
    private JButton visualizzaVoliButton;
    private JButton prenotaVoloButton;
    private JButton cercaPrenotazioneVoloButton;
    private JButton cercaPrenotazionePasseggeroButton;
    private JLabel benvenuto;
    private JButton disconnettitiButton;
    private JLabel imageLabel;
    private JPanel textPanel;
    private JPanel operationsPanel;
    private JPanel disconnectPanel;
    private JLabel userpngLabel;
    private JPanel infoutentePanel;
    private JButton areaPersonaleButton;
    private JTable partenzeTable;
    private JTable arriviTable;
    private JPanel partenzePanel;
    private JPanel topPanel;
    private JPanel tablePanel;
    private JPanel arriviPanel;
    private DefaultTableModel modelPartenze;
    private DefaultTableModel modelArrivi;


    public GUI_HomeUtente(UtenteGenerico utente) {
        setContentPane(mainpanel);
        setTitle("Home Utente");
        setSize(1200,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/user.png"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        userpngLabel.setIcon(imageIcon);


        benvenuto.setText(utente.getNome() + " " + utente.getCognome());

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

        partenzeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        arriviTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

        modelArrivi = new DefaultTableModel(
                new String[]{"Codice", "Data", "Orario", "Ritardo", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        arriviTable.setModel(modelArrivi);
        modelArrivi.setRowCount(0);

        modelArrivi.addRow(new Object[]{
                "Codice",
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
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    ritardoFormattato,
                    volo.getStato()
            });
        }

        visualizzaVoliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_VisualizzaVoliP(utente);
                dispose();
            }
        });


        prenotaVoloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_PrenotaVolo(utente);
                dispose();
            }
        });


        cercaPrenotazioneVoloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_CercaPrenVolo(utente);
                dispose();
            }
        });


        cercaPrenotazionePasseggeroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_CercaPrenPass(utente);
                dispose();
            }
        });


        disconnettitiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_Login();
                dispose();
            }
        });


        areaPersonaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_AreaPersonale(utente);
                dispose();
            }
        });
    }
}
