package gui;

import controller.Controller;
import modello.Prenotazione;
import modello.UtenteGenerico;
import modello.Volo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.sql.SQLException;

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

                if (voliTable.getSelectedRow() > 0) {
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

                try {
                    Controller.getInstance().eliminaPrenotazione(codicePrenotazione);

                    modelTabella.setRowCount(0);
                    caricaRisultati(utente.getNomeUtente());

                    codiceprentazioneField.setText("");

                    JOptionPane.showMessageDialog(
                            GUI_EliminaPrenotazione.this,
                            "Prenotazione eliminata con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(
                            GUI_EliminaPrenotazione.this,
                            "Errore durante l'eliminazione della prenotazione: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    private void caricaRisultati(String usernamePrenotante) {
        List<Prenotazione> prenotazioni = null;
        try {
            prenotazioni = Controller.getInstance()
                    .cercaPrenotazioniPerCreatore(usernamePrenotante);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
