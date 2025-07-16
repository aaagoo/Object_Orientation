package gui;

import modello.AmministratoreSistema;
import modello.Utente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_NuovoVolo extends JFrame {
    private JPanel mainpanel;
    private JButton partenzaButton;
    private JButton arrivoButton;
    private JButton annullaButton;
    private JPanel bigPanel;
    private JPanel smallPanel;
    private JPanel choicePanel;
    private JPanel annullaPanel;
    private JPanel textPanel;
    private Utente utente;


    public GUI_NuovoVolo(Utente utente) {
        this.utente = utente;
        setContentPane(mainpanel);
        setTitle("Nuovo Volo");
        setSize(500,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        mainpanel.setBorder(null);

        partenzaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_Partenza((AmministratoreSistema) utente);
                dispose();
            }
        });


        arrivoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_Arrivo((AmministratoreSistema) utente);
                dispose();
            }
        });


        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_HomeAmministratore((AmministratoreSistema) utente);
                dispose();
            }
        });
    }
}
