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
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JLabel userpngLabel;
    private JPanel infoadminPanel;


    public GUI_HomeAmministratore(AmministratoreSistema amministratore){
        setContentPane(mainpanel);
        setTitle("Home Amministratore");
        setSize(1000,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

        infoadminPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        operationsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/aeroporto_loginamm.jpg"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(1000, 300, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);

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
    }


}
