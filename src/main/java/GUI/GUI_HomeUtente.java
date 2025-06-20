package GUI;

import modello.UtenteGenerico;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI_HomeUtente extends JFrame{
    private JPanel mainpanel;
    private JButton visualizzaVoliButton;
    private JButton prenotaVoloButton;
    private JButton cercaPrenotazioneVoloButton;
    private JButton cercaPrenotazionePasseggeroButton;
    private JLabel benvenuto;
    private JButton disconnettitiButton;


    public GUI_HomeUtente(UtenteGenerico utente) {
        setContentPane(mainpanel);
        setTitle("Home Utente");
        setSize(500,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        benvenuto.setText("Benvenuto " + utente.getNome() + " " + utente.getCognome() + " nel sistema!");

        visualizzaVoliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_VisualizzaVoliP(utente);
                dispose();
            }
        });


        prenotaVoloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_PrenotaVolo(utente);
                dispose();
            }
        });


        cercaPrenotazioneVoloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_CercaPrenVolo(utente);
                dispose();
            }
        });


        cercaPrenotazionePasseggeroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_CercaPrenPass(utente);
                dispose();
            }
        });


        disconnettitiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_Login();
                dispose();
            }
        });
    }

}
