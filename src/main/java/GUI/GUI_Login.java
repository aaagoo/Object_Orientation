package GUI;

import modello.AmministratoreSistema;
import modello.Utente;
import controller.Controller;
import modello.UtenteGenerico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class GUI_Login extends JFrame {
    private JPanel mainpanel;
    private JButton accediButton;
    private JButton registratiButton;
    private JTextField userField;
    private JPasswordField pswField;
    private JLabel title1;
    private JPanel textArea;
    private JPanel registerPanel;
    private JPanel pswPanel;
    private JPanel userPanel;
    private JPanel accediPanel;
    private JLabel text;
    private JLabel imageLabel;
    private JLabel userLabel;
    private JLabel pswLabel;
    private JPanel credentialsPanel;
    private JPanel imagePanel;
    private JPanel textPanel;


    public GUI_Login() {
        setContentPane(mainpanel);
        setTitle("Login");
        setSize(1000,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);


        credentialsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/aeroportoimg1.jpg"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);


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

        userField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    accediButton.doClick();
                }
            }
        });

        pswField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    accediButton.doClick();
                }
            }
        });

        accediButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    accediButton.doClick();
                }
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