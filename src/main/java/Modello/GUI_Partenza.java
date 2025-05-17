package Modello;

import javax.swing.*;
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
    private JTextField gateField;
    private JButton annullaButton;
    private JButton confermaButton;
    private JComboBox statoComboBox;
    private Amministratore_Del_Sistema utente;


    public GUI_Partenza(Utente utente) {
        this.utente = (Amministratore_Del_Sistema) utente;
        setContentPane(mainpanel);
        setTitle("Inserisci Volo in Partenza");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Inizializza la combo box con gli stati del volo
        statoComboBox.setModel(new DefaultComboBoxModel<>(Stato_Volo.values()));

        // Imposta il valore predefinito del ritardo a 0
        ritardoField.setText("0");

        confermaButton.addActionListener(e -> {
            try {
                // Parsing dei dati inseriti
                String codice = codiceField.getText();
                String compagnia = compagniaField.getText();
                String destinazione = destinazioneField.getText();
                LocalDate data = LocalDate.parse(dataField.getText(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalTime orario = LocalTime.parse(orarioField.getText(),
                        DateTimeFormatter.ofPattern("HH:mm"));
                int numeroGate = Integer.parseInt(gateField.getText());
                long ritardoMinuti = Long.parseLong(ritardoField.getText());
                Stato_Volo stato = (Stato_Volo) statoComboBox.getSelectedItem();

                // Verifica che il ritardo non sia negativo
                if (ritardoMinuti < 0) {
                    throw new IllegalArgumentException("Il ritardo non può essere negativo");
                }

                // Creazione del nuovo volo con ritardo
                Volo_Partenza nuovoVolo = new Volo_Partenza(
                        codice,
                        compagnia,
                        destinazione,
                        data,
                        orario,
                        Duration.ofMinutes(ritardoMinuti),  // Converti i minuti in Duration
                        stato,
                        new Gate(numeroGate)
                );

                // Imposta esplicitamente il ritardo
                nuovoVolo.setRitardo(ritardoMinuti);

                // Se c'è un ritardo, aggiorna lo stato automaticamente
                if (ritardoMinuti > 0 && stato == Stato_Volo.Programmato) {
                    nuovoVolo.setStato(Stato_Volo.In_Ritardo);
                }

                // Aggiunta del volo al controller
                VoloController.getInstance().aggiungiVolo(nuovoVolo);

                // Mostra messaggio di successo
                JOptionPane.showMessageDialog(this,
                        "Volo inserito con successo!",
                        "Successo",
                        JOptionPane.INFORMATION_MESSAGE);

                // Aggiorna la tabella dei voli
                if (utente instanceof Amministratore_Del_Sistema) {
                    new GUI_VisualizzaVoliP(utente);
                } else {
                    JOptionPane.showMessageDialog(this, "Errore: Utente non autorizzato");
                }
                dispose();


            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Formato data/ora non valido.\nUtilizzare i formati:\nData: dd/MM/yyyy\nOra: HH:mm",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Valore numerico non valido per gate o ritardo.\nInserire numeri interi.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Errore nell'inserimento dei dati: " + ex.getMessage(),
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_HomeAmministratore((Amministratore_Del_Sistema) utente);
            }
        });
    }
}

