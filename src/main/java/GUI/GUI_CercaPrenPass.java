package GUI;

import modello.UtenteGenerico;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI_CercaPrenPass extends JFrame {
    private JPanel mainpanel;
    private JButton cercaButton;
    private JButton annullaButton;
    private JButton cercaPerCodiceVoloButton;
    private JTextField nomeField;
    private JTextField cognomeField;
    private final UtenteGenerico utente;

    public GUI_CercaPrenPass(UtenteGenerico utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Cerca Prenotazione Passeggero");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        cercaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cercaPrenotazioni();
            }
        });

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_HomeUtente(utente).setVisible(true);
                dispose();
            }
        });

        cercaPerCodiceVoloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_CercaPrenVolo(utente);
                dispose();
            }
        });

    }

    private void cercaPrenotazioni() {
        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();

        if (nome.isEmpty() || cognome.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Inserire sia nome che cognome per la ricerca",
                    "Campi Mancanti",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new GUI_CercaPrenPassRisult(utente, nome, cognome);
        dispose();
    }

}
