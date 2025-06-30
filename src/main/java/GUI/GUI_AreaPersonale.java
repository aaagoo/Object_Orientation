package GUI;

import controller.Controller;
import modello.Prenotazione;
import modello.UtenteGenerico;
import modello.Volo;
import modello.VoloPartenza;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class GUI_AreaPersonale extends JFrame {
    private JPanel mainpanel;
    private JPanel leftPanel;
    private JLabel imageLabel;
    private JButton modificavoliButton;
    private JTable voliTable;
    private JPanel rightPanel;
    private JPanel tablePanel;
    private JPanel buttonPanel;
    private JButton indietroButton;
    private JPanel infoPanel;
    private JPanel imagePanel;
    private JPanel datiouterPanel;
    private JLabel nomeLabel;
    private JLabel cognomeLabel;
    private JLabel usernameLabel;
    private JPanel datiPanel;
    private DefaultTableModel modelTabella;


    public GUI_AreaPersonale(UtenteGenerico utente) {
        setContentPane(mainpanel);
        setTitle("Area Personale");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        datiPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(200, 225, 255), new Color(240, 240, 240)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));


        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/user.png"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(125, 125, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);

        nomeLabel.setText(utente.getNome());
        cognomeLabel.setText(utente.getCognome());
        usernameLabel.setText(utente.getNomeUtente());

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_HomeUtente(utente);
                dispose();
            }
        });

        modelTabella = new DefaultTableModel(
                new String[]{"Nome", "Cognome", "Numero Biglietto", "Posto", "Volo", "Data", "Orario", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        voliTable.setModel(modelTabella);
        voliTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        caricaRisultati(utente.getNome(), utente.getCognome());

        modificavoliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_ModificaVolo(utente);
                dispose();
            }
        });
    }

    private void caricaRisultati(String nome, String cognome) {
        List<Prenotazione> prenotazioni = Controller.getInstance()
                .cercaPrenotazioniPerCreatore(nome, cognome);

        modelTabella.addRow(new Object[]{
                "N.Biglietto",
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
