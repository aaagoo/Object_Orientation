package GUI;

import controller.Controller;
import modello.Prenotazione;
import modello.UtenteGenerico;
import modello.Volo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;


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
    private JButton eliminaButton;
    private JButton modificaAccountButton;
    private DefaultTableModel modelTabella;


    public GUI_AreaPersonale(UtenteGenerico utente) {
        setContentPane(mainpanel);
        setTitle("Area Personale");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

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

        caricaRisultati(utente.getNomeUtente());

        modificavoliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_ModificaPrenotazione(utente);
                dispose();
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_EliminaPrenotazione(utente);
                dispose();
            }
        });

        modificaAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_ModificaAccountPersonale(utente);
                dispose();
            }
        });
    }

    private void caricaRisultati(String usernamePrenotante) {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try {
            prenotazioni = Controller.getInstance().cercaPrenotazioniPerCreatore(usernamePrenotante);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Errore durante il caricamento delle prenotazioni: " + e.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        modelTabella.setRowCount(0);
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

