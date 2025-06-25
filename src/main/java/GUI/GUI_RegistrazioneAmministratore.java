package GUI;

import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_RegistrazioneAmministratore extends JFrame {
    private JPanel mainpanel;
    private JButton annullaButton;
    private JButton confermaButton;
    private JTextField newuser;
    private JTextField newpsw;
    private JTextField newreppsw;
    private JPanel textPanel;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JPanel credentialsPanel;

    public GUI_RegistrazioneAmministratore () {
        setContentPane(mainpanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        setTitle("Registrazione Amministratore");

        credentialsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(240, 240, 240), new Color(215, 225, 250)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/airport_logo.png"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);


        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = newuser.getText();
                String password = newpsw.getText();
                String repPassword = newreppsw.getText();

                if (username.isEmpty() || password.isEmpty() || repPassword.isEmpty()) {

                    JOptionPane.showMessageDialog(GUI_RegistrazioneAmministratore.this,
                            "Compila tutti i campi!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);


                } else if (!password.equals(repPassword)) {

                    JOptionPane.showMessageDialog(GUI_RegistrazioneAmministratore.this,
                            "Le password non corrispondono!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);


                } else if (Controller.getInstance().registraAmministratore(username, password)) {
                    JOptionPane.showMessageDialog(GUI_RegistrazioneAmministratore.this,
                            "Registrazione avvenuta con successo!");
                    dispose();

                    new GUI_Login();

                } else {
                    JOptionPane.showMessageDialog(GUI_RegistrazioneAmministratore.this,
                            "Username già in uso!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }


        });
        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI_Login();
            }
        });
    }
}
