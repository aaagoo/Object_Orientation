package GUI;

import modello.AmministratoreSistema;
import modello.Utente;
import controller.Controller;
import modello.UtenteGenerico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_Login extends JFrame {
    private JPanel mainpanel;
    private JButton accediButton;
    private JButton registratiButton;
    private JTextField userField;
    private JPasswordField pswField;
    private JPanel panel;
    private JLabel title1;
    private JPanel textArea;
    private JPanel registerPanel;
    private JPanel pswPanel;
    private JPanel userPanel;
    private JPanel accediPanel;
    private JLabel text;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JLabel userLabel;
    private JLabel pswLabel;
    private JPanel credentialsPanel;


    public GUI_Login() {
        setContentPane(mainpanel);
        setTitle("Login");
        setSize(1000,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        panel.setBackground(new Color(215, 225, 250));
        textArea.setBackground(new Color(215, 225, 250));
        userPanel.setBackground(new Color(215, 225, 250));
        pswPanel.setBackground(new Color(215, 225, 250));
        accediPanel.setBackground(new Color(215, 225, 250));
        registerPanel.setBackground(new Color(215, 225, 250));
        accediButton.setBackground(new Color(255, 225, 200));
        registratiButton.setBackground(new Color(255, 225, 200));
        userField.setBackground(new Color(200, 225, 255));
        pswField.setBackground(new Color(200, 225, 255));
        credentialsPanel.setBackground(new Color(250, 250, 250));

        title1.setFont(new Font("Inter", Font.BOLD , 18));
        text.setFont(new Font("Inter", Font.PLAIN , 16));
        accediButton.setFont(new Font("Inter", Font.BOLD , 13));
        registratiButton.setFont(new Font("Inter", Font.BOLD , 13));
        userLabel.setFont(new Font("Inter", Font.PLAIN , 14));
        pswLabel.setFont(new Font("Inter", Font.PLAIN , 14));
        userField.setFont(new Font("Inter", Font.PLAIN , 14));
        pswField.setFont(new Font("Inter", Font.PLAIN , 14));


        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/aeroportoimg1.jpg"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(493, 461, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);
        imagePanel.setBackground(new Color(215, 225, 250));




        registratiButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_RegistrazioneScelta();

                }
            });

            accediButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = userField.getText();
                    String password = new String(pswField.getPassword());

                    if (username.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(GUI_Login.this,
                                "Inserire username e password",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Utente utente = Controller.getInstance().login(username, password);

                    if (utente == null) {
                        JOptionPane.showMessageDialog(GUI_Login.this,
                                "Credenziali non valide",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (utente instanceof AmministratoreSistema) {
                        mostraInterfacciaAmministratore((AmministratoreSistema) utente);
                    } else if (utente instanceof UtenteGenerico) {
                        mostraInterfacciaUtente((UtenteGenerico) utente);
                    }

                    dispose();
                }
            });
        }

    private void mostraInterfacciaAmministratore(AmministratoreSistema admin) {

        new GUI_HomeAmministratore(admin);
    }

    private void mostraInterfacciaUtente(UtenteGenerico utente) {

        new GUI_HomeUtente(utente);
    }

}

