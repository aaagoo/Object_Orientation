package Modello;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_RegistrazioneAmministratore extends JFrame {
    private JPanel mainpanel;
    private JButton annullaButton;
    private JButton confermaButton;
    private JTextField newuser;
    private JTextField newpsw;
    private JTextField newreppsw;

    public GUI_RegistrazioneAmministratore () {
        setContentPane(mainpanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setTitle("Registrazione Amministratore");


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


                } else if (UtenteController.getInstance().registraAmministratore(username, password)) {
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
