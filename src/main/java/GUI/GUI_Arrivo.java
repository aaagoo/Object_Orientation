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


public class GUI_Arrivo extends JFrame {
    private JPanel mainpanel;
    private JTextField codiceField;
    private JTextField compagniaField;
    private JTextField origineField;
    private JTextField dataField;
    private JTextField orarioField;
    private JTextField ritardoField;
    private JButton annullaButton;
    private JButton confermaButton;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JPanel textPanel;
    private JPanel bottoniPanel;
    private JPanel intestazionePanel;
    private JLabel textLabel;
    private JPanel operationsPanel;
    private JPanel codicePanel;
    private JPanel ritardoPanel;
    private JPanel compagniaPanel;
    private JPanel destinazionePanel;
    private JPanel dataPanel;
    private JPanel orarioPanel;
    private AmministratoreSistema utente;


    public GUI_Arrivo(Utente utente) {
        this.utente = (AmministratoreSistema) utente;
        setContentPane(mainpanel);
        setTitle("Inserisci Volo in Arrivo");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        dataField.setToolTipText(null);
        orarioField.setToolTipText(null);
        ritardoField.setToolTipText(null);

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/aeroporto_torre.jpg"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(350, 700, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);

        operationsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        bottoniPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));


        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (codiceField.getText().trim().isEmpty() ||
                        compagniaField.getText().trim().isEmpty() ||
                        origineField.getText().trim().isEmpty() ||
                        dataField.getText().trim().isEmpty() ||
                        orarioField.getText().trim().isEmpty() ||
                        ritardoField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(GUI_Arrivo.this,
                            "Per favore, compila tutti i campi richiesti!",
                            "Campi Mancanti",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String codice = codiceField.getText();
                    String compagnia = compagniaField.getText();
                    String origine = origineField.getText();
                    LocalDate data = LocalDate.parse(dataField.getText(),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalTime orario = LocalTime.parse(orarioField.getText(),
                            DateTimeFormatter.ofPattern("HH:mm"));
                    long ritardoMinuti = Long.parseLong(ritardoField.getText());


                    if (ritardoMinuti < 0) {
                        throw new IllegalArgumentException("Il ritardo non può essere negativo");
                    }

                    StatoVolo statoIniziale;
                    if (ritardoMinuti > 0) {
                        statoIniziale = StatoVolo.IN_RITARDO;
                    } else {
                        statoIniziale = StatoVolo.PROGRAMMATO;
                    }


                    VoloArrivo nuovoVolo = new VoloArrivo(
                            codice,
                            compagnia,
                            origine,
                            data,
                            orario,
                            Duration.ofMinutes(ritardoMinuti),
                            statoIniziale
                    );

                    nuovoVolo.setRitardo(ritardoMinuti);
                    Controller.getInstance().aggiungiVolo(nuovoVolo);


                    JOptionPane.showMessageDialog(GUI_Arrivo.this,
                            "Volo inserito con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                    if (utente instanceof AmministratoreSistema) {
                        new GUI_VisualizzaVoliA(utente);
                    } else {
                        JOptionPane.showMessageDialog(GUI_Arrivo.this,
                                "Errore: Utente non autorizzato");
                    }
                    dispose();

                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(GUI_Arrivo.this,
                            "Formato data/ora non valido.\nUtilizzare i formati:\nData: dd/MM/yyyy\nOra: HH:mm",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI_Arrivo.this,
                            "Valore del ritardo non valido.\nInserire un numero intero.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GUI_Arrivo.this,
                            "Errore nell'inserimento dei dati: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_HomeAmministratore((AmministratoreSistema) utente);
            }
        });

    }
}
