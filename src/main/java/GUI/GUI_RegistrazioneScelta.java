package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_RegistrazioneScelta extends JFrame {
    private JPanel mainpanel;
    private JButton utenteButton;
    private JButton amministratoreButton;
    private JButton annulllaButton;


    public GUI_RegistrazioneScelta() {
        setContentPane(mainpanel);
        setTitle("Registrazione");
        setSize(400,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

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
