package Modello;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GUI_CercaPrenVolo extends JFrame {
    private JPanel mainpanel;
    private JTextField codicevoloField;
    private JButton cercaButton;
    private JButton annullaButton;
    private JButton cercaPerDatiPasseggeroButton;
    private JTable tabellaVoli;
    private DefaultTableModel modelVoli;
    private final Utente_Generico utente;

    public GUI_CercaPrenVolo(Utente_Generico utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Cerca Prenotazioni per Volo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

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
        tabellaVoli.setAutoCreateRowSorter(true);

        caricaVoliPartenza();

        tabellaVoli.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabellaVoli.getSelectedRow() != -1) {
                String codiceVolo = (String) tabellaVoli.getValueAt(tabellaVoli.getSelectedRow(), 0);
                codicevoloField.setText(codiceVolo);
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
                dispose();
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
        List<Volo_Partenza> voliPartenza = VoloController.getInstance().getVoliPartenza();

        for (Volo_Partenza volo : voliPartenza) {
            if (volo.getRitardo() > 0 && volo.getStato() == Stato_Volo.Programmato) {
                volo.setStato(Stato_Volo.In_Ritardo);
            }

            String dataFormattata = volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String oraFormattata = volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm"));
            String ritardoFormattato = volo.getRitardo() > 0 ? volo.getRitardo() + " min" : "In orario";
            String gate = volo.getGate() != null ? String.valueOf(volo.getGate().getNumero_Gate()) : "Non Assegnato";

            modelVoli.addRow(new Object[]{
                    volo.getCodice(),
                    volo.getCompagnia_Aerea(),
                    volo.getAeroporto_Destinazione(),
                    dataFormattata,
                    oraFormattata,
                    ritardoFormattato,
                    gate,
                    volo.getStato()
            });
        }
    }
}