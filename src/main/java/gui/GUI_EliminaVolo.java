package gui;

import controller.Controller;
import modello.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class GUI_EliminaVolo extends JFrame{
    private JPanel mainpanel;
    private JPanel tabellaPanel;
    private JTable tabellaVoli;
    private JPanel operationsPanel;
    private JPanel codicePanel;
    private JTextField codiceVoloField;
    private JPanel buttonsPanel;
    private JButton eliminaButton;
    private JButton annullaButton;
    private AmministratoreSistema utente;
    private DefaultTableModel modelVoli;


    public GUI_EliminaVolo(AmministratoreSistema utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Elimina Volo");
        setSize(1000, 900);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        modelVoli = new DefaultTableModel(
                new String[]{"Codice", "Compagnia", "Destinazione", "Data", "Orario", "Ritardo", "Gate", "Stato"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaVoli.setModel(modelVoli);
        tabellaVoli.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        aggiornaTabella();

        tabellaVoli.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (tabellaVoli.getSelectedRow() > 0) {
                    String codiceVolo = (String) tabellaVoli.getValueAt(tabellaVoli.getSelectedRow(), 0);
                    codiceVoloField.setText(codiceVolo);
                }
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String codiceVolo = codiceVoloField.getText();

                if (codiceVolo == null || codiceVolo.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Per favore, inserisci un codice volo valido.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Volo voloTrovato = Controller.getInstance().cercaVoloPerCodice(codiceVolo);
                if (voloTrovato == null) {
                    JOptionPane.showMessageDialog(null,
                            "Il codice volo inserito non esiste nel sistema.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<Prenotazione> prenotazioniAssociate = null;
                try {
                    prenotazioniAssociate = Controller.getInstance().cercaPrenotazioniPerCodiceVolo(codiceVolo);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if (!prenotazioniAssociate.isEmpty()) {
                    int scelta = JOptionPane.showConfirmDialog(null,
                            "Esistono " + prenotazioniAssociate.size() + " prenotazioni associate a questo volo.\n" +
                                    "Vuoi procedere con l'eliminazione del volo e di tutte le prenotazioni associate?",
                            "Conferma eliminazione",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (scelta != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                try {
                    Controller.getInstance().eliminaVolo(codiceVolo);
                    JOptionPane.showMessageDialog(null,
                            "Volo eliminato con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                    aggiornaTabella();
                    codiceVoloField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Errore durante l'eliminazione del volo: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_HomeAmministratore(utente);
                dispose();
            }
        });
    }

    private void aggiornaTabella() {
        modelVoli.setRowCount(0);

        modelVoli.addRow(new Object[]{
                "Codice",
                "Tipo",
                "Compagnia",
                "Orig/Dest",
                "Data",
                "Orario",
                "Ritardo",
                "Gate",
                "Stato"
        });

        for (Volo volo : Controller.getInstance().getAllVoli()) {

            if (volo.getRitardo() > 0 && volo.getStato() == StatoVolo.PROGRAMMATO) {
                volo.setStato(StatoVolo.IN_RITARDO);
            }

            String tipo = volo instanceof VoloArrivo ? "Arrivo" : "Partenza";
            String origine_destinazione = volo instanceof VoloArrivo ?
                    (volo).getAeroportoOrigine() :
                    (volo).getAeroportoDestinazione();
            String gate = volo instanceof VoloPartenza ?
                    (((VoloPartenza) volo).getGate() != null ?
                            String.valueOf(((VoloPartenza) volo).getGate().getNumeroGate()) :
                            "Non assegnato") :
                    "N/A";

            modelVoli.addRow(new Object[]{
                    volo.getCodice(),
                    tipo,
                    volo.getCompagniaAerea(),
                    origine_destinazione,
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    volo.getRitardo() > 0 ? volo.getRitardo() + " min" : "In orario",
                    gate,
                    volo.getStato()
            });
        }
    }
}
