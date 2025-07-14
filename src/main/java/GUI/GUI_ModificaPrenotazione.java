package GUI;

import controller.Controller;
import modello.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.sql.SQLException;


public class GUI_ModificaPrenotazione extends JFrame {


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

    public GUI_ModificaPrenotazione(UtenteGenerico utente) {
        setContentPane(mainpanel);
        setTitle("Modifica Prenotazioni");
        setSize(1000, 950);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        prenotazioniTable.setModel(modelTabella);
        prenotazioniTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        caricaRisultati(utente.getNomeUtente());

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
        partenzeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
                new GUI_AreaPersonale(utente);
                dispose();
            }
        });


        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (codiceVoloField.getText().trim().isEmpty() ||
                        nuovocodiceField.getText().trim().isEmpty() ||
                        nomeField.getText().trim().isEmpty() ||
                        cognomeField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(GUI_ModificaPrenotazione.this,
                            "Per favore, compila tutti i campi richiesti!",
                            "Campi Mancanti",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String vecchioCodice = codiceVoloField.getText().trim();
                String nuovoCodice = nuovocodiceField.getText().trim();
                String nomePasseggero = nomeField.getText().trim();
                String cognomePasseggero = cognomeField.getText().trim();

                // Verifica che il nuovo volo esista
                try {
                    List<Prenotazione> prenotazioni = Controller.getInstance()
                            .cercaPrenotazioniPerCodiceVolo(vecchioCodice);

                    if (prenotazioni.isEmpty()) {
                        JOptionPane.showMessageDialog(GUI_ModificaPrenotazione.this,
                                "Prenotazione non trovata!",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Chiama il nuovo metodo del Controller per modificare la prenotazione
                    Controller.getInstance().modificaPrenotazione(
                            utente.getNomeUtente(),
                            vecchioCodice,
                            nuovoCodice,
                            nomePasseggero,
                            cognomePasseggero
                    );

                    JOptionPane.showMessageDialog(GUI_ModificaPrenotazione.this,
                            "Prenotazione modificata con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Aggiorna la tabella delle prenotazioni
                    modelTabella.setRowCount(0);
                    caricaRisultati(utente.getNomeUtente());

                    // Torna all'area personale
                    new GUI_AreaPersonale(utente);
                    dispose();

                } catch (SQLException ex) {
                    String messaggio;
                    if (ex.getMessage().contains("unique_passeggero_volo")) {
                        messaggio = "Il passeggero ha già una prenotazione per questo volo!";
                    } else if (ex.getMessage().contains("DECOLLATO") ||
                            ex.getMessage().contains("CANCELLATO")) {
                        messaggio = "Non è possibile modificare una prenotazione per un volo già partito o cancellato!";
                    } else {
                        messaggio = "Errore durante la modifica della prenotazione: " + ex.getMessage();
                    }

                    JOptionPane.showMessageDialog(GUI_ModificaPrenotazione.this,
                            messaggio,
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        prenotazioniTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int riga = prenotazioniTable.rowAtPoint(e.getPoint());

                if (riga > 0) {
                    String codiceVolo = (String) prenotazioniTable.getValueAt(prenotazioniTable.getSelectedRow(), 3);
                    String nomePasseggero = (String) prenotazioniTable.getValueAt(prenotazioniTable.getSelectedRow(), 1);
                    String cognomePasseggero = (String) prenotazioniTable.getValueAt(prenotazioniTable.getSelectedRow(), 2);
                    codiceVoloField.setText(codiceVolo);
                    nuovocodiceField.setText(codiceVolo);
                    nomeField.setText(nomePasseggero);
                    cognomeField.setText(cognomePasseggero);
                }
            }
        });

        partenzeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (partenzeTable.getSelectedRow() > 0) {
                    String codiceVolo = (String) partenzeTable.getValueAt(partenzeTable.getSelectedRow(), 0);
                    nuovocodiceField.setText(codiceVolo);
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
