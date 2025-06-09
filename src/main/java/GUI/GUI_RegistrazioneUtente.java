package GUI;

import Controller.UtenteController;

import javax.swing.*;
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

    public GUI_RegistrazioneUtente() {
        setContentPane(mainpanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setTitle("Registrazione Utente");


        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = nomebar.getText();
                String cognome = cognomebar.getText();
                String username = newuser.getText();
                String password = newpsw.getText();
                String repPassword = newreppsw.getText();

                if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty() || repPassword.isEmpty()) {

                    JOptionPane.showMessageDialog(GUI_RegistrazioneUtente.this,
                            "Compila tutti i campi!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);


                } else if (!password.equals(repPassword)) {

                    JOptionPane.showMessageDialog(GUI_RegistrazioneUtente.this,
                            "Le password non corrispondono!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);


                } else if (UtenteController.getInstance().registraUtenteGenerico(nome, cognome, username, password)) {
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
