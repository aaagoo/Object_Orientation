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
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class GUI_AssegnaGate extends JFrame {
    private JPanel mainpanel;
    private JTextField codiceVoloField;
    private JTextField gateField;
    private JButton annullaButton;
    private JButton confermaButton;
    private JTable tabellaVoli;
    private JPanel buttonsPanel;
    private JPanel operationsPanel;
    private JPanel codicePanel;
    private JPanel ritardoPanel;
    private JPanel tabellaPanel;
    private DefaultTableModel modelVoli;
    private AmministratoreSistema utente;


    public GUI_AssegnaGate(AmministratoreSistema utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Assegna Gate");
        setSize(1000, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

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

        aggiornaTabella();

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String codiceVolo = codiceVoloField.getText();
                    int numeroGate = Integer.parseInt(gateField.getText());

                    VoloPartenza volo = Controller.getInstance().trovaVoloPartenza(codiceVolo);
                    if (volo == null) {
                        JOptionPane.showMessageDialog(GUI_AssegnaGate.this,
                                "Volo non trovato",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Controller.getInstance().assegnaGate(codiceVolo, numeroGate);

                    aggiornaTabella();

                    codiceVoloField.setText("");
                    gateField.setText("");

                    JOptionPane.showMessageDialog(GUI_AssegnaGate.this,
                            "Gate assegnato con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI_AssegnaGate.this,
                            "Numero gate non valido",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(GUI_AssegnaGate.this,
                            ex.getMessage(),
                            "Errore Database",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_HomeAmministratore(utente);
            }
        });

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
    }

    private void aggiornaTabella() {
        modelVoli.setRowCount(0);

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

        for (VoloPartenza volo : Controller.getInstance().getVoliPartenza()) {

            if (volo.getRitardo() > 0 && volo.getStato() == StatoVolo.PROGRAMMATO) {
                volo.setStato(StatoVolo.IN_RITARDO);
            }

            modelVoli.addRow(new Object[]{
                    volo.getCodice(),
                    volo.getCompagniaAerea(),
                    volo.getAeroportoDestinazione(),
                    volo.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    volo.getOrario().format(DateTimeFormatter.ofPattern("HH:mm")),
                    volo.getRitardo() > 0 ? volo.getRitardo() + " min" : "In orario",
                    volo.getGate() != null ? volo.getGate().getNumeroGate() : "Non assegnato",
                    volo.getStato()
            });
        }
    }
}
