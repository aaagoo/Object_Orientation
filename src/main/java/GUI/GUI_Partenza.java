package GUI;

import controller.Controller;
import modello.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class GUI_Partenza extends JFrame {
    private JPanel mainpanel;
    private JTextField codiceField;
    private JTextField compagniaField;
    private JTextField destinazioneField;
    private JTextField dataField;
    private JTextField orarioField;
    private JTextField ritardoField;
    private JButton annullaButton;
    private JButton confermaButton;
    private JPanel codicePanel;
    private JPanel ritardoPanel;
    private JPanel compagniaPanel;
    private JPanel destinazionePanel;
    private JPanel dataPanel;
    private JPanel orarioPanel;
    private JLabel imageLabel;
    private JPanel imagePanel;
    private JLabel textLabel;
    private JPanel operationsPanel;
    private JPanel intestazionePanel;
    private JPanel bottoniPanel;
    private JPanel textPanel;
    private JComboBox comboBox1;
    private JPanel statoPanel;
    private AmministratoreSistema utente;


    public GUI_Partenza(Utente utente) {
        this.utente = (AmministratoreSistema) utente;
        setContentPane(mainpanel);
        setTitle("Inserisci Volo");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        dataField.setToolTipText(null);
        orarioField.setToolTipText(null);
        ritardoField.setToolTipText(null);

        for (StatoVolo stato : StatoVolo.values()) {
            comboBox1.addItem(stato);
        }
        comboBox1.setSelectedItem(StatoVolo.PROGRAMMATO);

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/aeroporto_torre.jpg"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(350, 700, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_HomeAmministratore((AmministratoreSistema) utente);
            }
        });

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (codiceField.getText().trim().isEmpty() ||
                        compagniaField.getText().trim().isEmpty() ||
                        destinazioneField.getText().trim().isEmpty() ||
                        dataField.getText().trim().isEmpty() ||
                        orarioField.getText().trim().isEmpty() ||
                        ritardoField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(GUI_Partenza.this,
                            "Per favore, compila tutti i campi richiesti!",
                            "Campi Mancanti",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String codice = codiceField.getText();
                    String compagnia = compagniaField.getText();
                    String destinazione = destinazioneField.getText();
                    LocalDate data = LocalDate.parse(dataField.getText(),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalTime orario = LocalTime.parse(orarioField.getText(),
                            DateTimeFormatter.ofPattern("HH:mm"));
                    long ritardoMinuti = Long.parseLong(ritardoField.getText());

                    StatoVolo statoSelezionato = (StatoVolo) comboBox1.getSelectedItem();


                    VoloPartenza nuovoVolo = new VoloPartenza(
                            codice,
                            compagnia,
                            destinazione,
                            data,
                            orario,
                            Duration.ofMinutes(ritardoMinuti),
                            statoSelezionato,
                            null
                    );

                    nuovoVolo.setRitardo(ritardoMinuti);

                    Controller.getInstance().aggiungiVolo(nuovoVolo);

                    JOptionPane.showMessageDialog(GUI_Partenza.this,
                            "Volo inserito con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Aggiorna la tabella dei voli
                    if (utente instanceof AmministratoreSistema) {
                        new GUI_VisualizzaVoliP(utente);
                    } else {
                        JOptionPane.showMessageDialog(GUI_Partenza.this,
                                "Errore: Utente non autorizzato");
                    }
                    dispose();


                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(GUI_Partenza.this,
                            "Formato data/ora non valido.\nUtilizzare i formati:\nData: dd/MM/yyyy\nOra: HH:mm",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI_Partenza.this,
                            "Valore numerico non valido per ritardo.\nInserire numeri interi.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GUI_Partenza.this,
                            "Errore nell'inserimento dei dati: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}

