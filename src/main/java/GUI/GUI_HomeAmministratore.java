package GUI;

import modello.AmministratoreSistema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_HomeAmministratore extends JFrame{
    private JPanel mainpanel;
    private JButton nuovoVoloButton;
    private JButton visualizzaVoliButton;
    private JButton assegnaGateButton;
    private JButton aggiornaVoloButton;
    private JLabel benvenuto;
    private JButton disconnettitiButton;
    private JPanel textPanel;
    private JPanel operationsPanel;
    private JPanel disconnectPanel;
    private JLabel imageLabel;
    private JLabel userpngLabel;
    private JPanel infoadminPanel;
    private JButton eliminaVoloButton;
    private JButton gestisciprenotazioniButton;
    private JButton gestisciaccountButton;


    public GUI_HomeAmministratore(AmministratoreSistema amministratore){
        setContentPane(mainpanel);
        setTitle("Home Amministratore");
        setSize(1000,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

        ImageIcon imageIcon2 = new ImageIcon(getClass().getClassLoader().getResource("images/user.png"));
        Image image2 = imageIcon2.getImage();
        Image newimg2 = image2.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        imageIcon2 = new ImageIcon(newimg2);
        userpngLabel.setIcon(imageIcon2);

        benvenuto.setText(amministratore.getNomeUtente());

        visualizzaVoliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_VisualizzaVoliP(amministratore);
                dispose();
            }
        });

        nuovoVoloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_NuovoVolo(amministratore);
                dispose();
            }
        });

        assegnaGateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_AssegnaGate(amministratore);
                dispose();
            }
        });

        aggiornaVoloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_AggiornaVolo(amministratore);
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

        eliminaVoloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_EliminaVolo(amministratore);
                dispose();
            }
        });

        gestisciprenotazioniButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_GestisciPrenotazioni(amministratore);
                dispose();
            }
        });

        gestisciaccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_GestisciAccount(amministratore);
                dispose();
            }
        });
    }


}
