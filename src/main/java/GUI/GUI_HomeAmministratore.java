package GUI;

import modello.AmministratoreSistema;

import javax.swing.*;
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


    public GUI_HomeAmministratore(AmministratoreSistema amministratore){
        setContentPane(mainpanel);
        setTitle("Home Amministratore");
        setSize(500,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        benvenuto.setText("Benvenuto " + amministratore.getNomeUtente() + " nel sistema!");

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
