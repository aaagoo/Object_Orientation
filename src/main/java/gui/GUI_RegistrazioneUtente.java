package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI_RegistrazioneUtente extends JFrame{
    private JPanel mainpanel;
    private JButton annullaButton;
    private JButton confermaButton;
    private JTextField newuser;
    private JTextField newpsw;
    private JTextField newreppsw;
    private JTextField nomebar;
    private JTextField cognomebar;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JPanel credentialsPanel;
    private JPanel titlePanel;

    public GUI_RegistrazioneUtente() {
        setContentPane(mainpanel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        setTitle("Registrazione Utente");

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/airport_logo.png"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = nomebar.getText();
                String cognome = cognomebar.getText();
                String nomeutente = newuser.getText();
                String password = newpsw.getText();
                String repPassword = newreppsw.getText();

                if (nome.isEmpty() || cognome.isEmpty() || nomeutente.isEmpty() || password.isEmpty() || repPassword.isEmpty()) {

                    JOptionPane.showMessageDialog(GUI_RegistrazioneUtente.this,
                            "Compila tutti i campi!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);


                } else if (!password.equals(repPassword)) {

                    JOptionPane.showMessageDialog(GUI_RegistrazioneUtente.this,
                            "Le password non corrispondono!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);


                } else if (Controller.getInstance().registraUtenteGenerico(nome, cognome, nomeutente, password)) {
                    JOptionPane.showMessageDialog(GUI_RegistrazioneUtente.this,
                            "Registrazione avvenuta con successo!");
                    dispose();

                    new GUI_Login();

                } else {
                    JOptionPane.showMessageDialog(GUI_RegistrazioneUtente.this,
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
