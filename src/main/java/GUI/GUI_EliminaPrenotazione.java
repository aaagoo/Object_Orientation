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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GUI_EliminaPrenotazione extends JFrame {

    private JPanel tabellaPanel;
    private JTable voliTable;
    private JPanel mainpanel;
    private JPanel operationsPanel;
    private JTextField codiceprentazioneField;
    private JPanel buttonsPanel;
    private JButton indietroButton;
    private JButton confermaButton;
    private DefaultTableModel modelTabella;

    public GUI_EliminaPrenotazione(UtenteGenerico utente){
        setContentPane(mainpanel);
        setTitle("Area Personale");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        voliTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int riga = voliTable.rowAtPoint(e.getPoint());

                if (riga != 0) {
                    String codiceVolo = (String) voliTable.getValueAt(voliTable.getSelectedRow(), 0);
                    codiceprentazioneField.setText(codiceVolo);
                }
            }
        });

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_AreaPersonale(utente);
                dispose();
            }
        });


        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String codicePrenotazione = codiceprentazioneField.getText().trim();

                if (codicePrenotazione.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            GUI_EliminaPrenotazione.this,
                            "Inserire un codice di prenotazione!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    private void caricaRisultati(String usernamePrenotante) {
        List<Prenotazione> prenotazioni = Controller.getInstance()
                .cercaPrenotazioniPerCreatore(usernamePrenotante);

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
