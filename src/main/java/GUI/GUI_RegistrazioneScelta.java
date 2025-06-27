package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_RegistrazioneScelta extends JFrame {
    private JPanel mainpanel;
    private JButton utenteButton;
    private JButton amministratoreButton;
    private JButton annulllaButton;
    private JPanel choicePanel;
    private JPanel textPanel;
    private JPanel annullaPanel;
    private JPanel bigPanel;
    private JPanel smallPanel;


    public GUI_RegistrazioneScelta() {
        setContentPane(mainpanel);
        setTitle("Registrazione");
        setSize(500,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);


        utenteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_RegistrazioneUtente();
            }
        });

        amministratoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_RegistrazioneAmministratore();
            }
        });

        annulllaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_Login();
            }
        });
    }
}
