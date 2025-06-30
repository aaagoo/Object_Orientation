package GUI;

import modello.StatoVolo;
import modello.UtenteGenerico;
import controller.Controller;
import modello.VoloPartenza;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GUI_CercaPrenVolo extends JFrame {
    private JPanel mainpanel;
    private JTextField codicevoloField;
    private JButton cercaButton;
    private JButton annullaButton;
    private JButton cercaPerDatiPasseggeroButton;
    private JTable tabellaVoli;
    private JPanel tabellaPanel;
    private JPanel buttonsPanel;
    private JPanel operationsPanel;
    private DefaultTableModel modelVoli;
    private final UtenteGenerico utente;

    public GUI_CercaPrenVolo(UtenteGenerico utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Cerca Prenotazioni per Volo");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

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

        modelVoli.setRowCount(0);

        caricaVoliPartenza();

        tabellaVoli.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int riga = tabellaVoli.rowAtPoint(e.getPoint());

                if (riga != 0) {
                    String codiceVolo = (String) tabellaVoli.getValueAt(tabellaVoli.getSelectedRow(), 0);
                    codicevoloField.setText(codiceVolo);
                }
            }
        });

        cercaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (codicevoloField.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            GUI_CercaPrenVolo.this,
                            "Inserire un codice volo!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                new GUI_CercaPrenVoloRisult(utente, codicevoloField.getText());

            }
        });

        cercaPerDatiPasseggeroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_CercaPrenPass(utente);
                dispose();
            }
        });

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_HomeUtente(utente);
                dispose();
            }
        });
    }

    private void caricaVoliPartenza() {
        modelVoli.setRowCount(0);
        List<VoloPartenza> voliPartenza = Controller.getInstance().getVoliPartenza();

        modelVoli.addRow(new Object[]{
                "Codice",
                "Compagnia",
                "Destinazione",
                "Data",
                "Orario",
                "Ritardo",
                "Gate",
                "Stato"
        });

        for (VoloPartenza volo : voliPartenza) {
            if (volo.getRitardo() > 0 && volo.getStato() == StatoVolo.PROGRAMMATO) {
                volo.setStato(StatoVolo.IN_RITARDO);
            }

            String dataFormattata = volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String oraFormattata = volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm"));
            String ritardoFormattato = volo.getRitardo() > 0 ? volo.getRitardo() + " min" : "In orario";
            String gate = volo.getGate() != null ? String.valueOf(volo.getGate().getNumeroGate()) : "Non Assegnato";

            modelVoli.addRow(new Object[]{
                    volo.getCodice(),
                    volo.getCompagniaAerea(),
                    volo.getAeroportoDestinazione(),
                    dataFormattata,
                    oraFormattata,
                    ritardoFormattato,
                    gate,
                    volo.getStato()
            });
        }
    }
}