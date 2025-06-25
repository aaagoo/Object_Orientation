package GUI;

import modello.UtenteGenerico;

import javax.swing.*;
import java.awt.*;
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
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JPanel textPanel;
    private JPanel operationsPanel;
    private JPanel disconnectPanel;
    private JLabel userpngLabel;
    private JPanel infoutentePanel;


    public GUI_HomeUtente(UtenteGenerico utente) {
        setContentPane(mainpanel);
        setTitle("Home Utente");
        setSize(1000,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

        operationsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        infoutentePanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/user.png"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        userpngLabel.setIcon(imageIcon);

        ImageIcon imageIcon2 = new ImageIcon(getClass().getClassLoader().getResource("images/aeroporto_login.png"));
        Image image2 = imageIcon2.getImage();
        Image newimg2 = image2.getScaledInstance(1000, 300, Image.SCALE_SMOOTH);
        imageIcon2 = new ImageIcon(newimg2);
        imageLabel.setIcon(imageIcon2);

        benvenuto.setText(utente.getNome() + " " + utente.getCognome());


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
