package Modello;

import javax.swing.*;
import javax.swing.DefaultComboBoxModel;
import java.awt.*;


public class GUI_Registrazione extends JFrame{
    private JPanel mainpanel;
    private JButton annullaButton;
    private JButton confermaButton;
    private JTextField newuser;
    private JTextField newpsw;
    private JTextField newreppsw;
    private JComboBox sceltaruolo;
    private JTextField nomebar;
    private JTextField cognomebar;

    public GUI_Registrazione() {
        setContentPane(mainpanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setVisible(true);

        sceltaruolo.setModel(new DefaultComboBoxModel<>(
                new String[]{"Utente Generico", "Amministratore"}));

        confermaButton.addActionListener(e -> {
            String nome = nomebar.getText();
            String cognome = cognomebar.getText();
            String username = newuser.getText();
            String password = newpsw.getText();
            String repPassword = newreppsw.getText();
            String ruolo = (String) sceltaruolo.getSelectedItem();

            if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty() || repPassword.isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Compila tutti i campi!",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;


            } else if (!password.equals(repPassword)) {

                JOptionPane.showMessageDialog(this,
                        "Le password non corrispondono!",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;


            } else if (UtenteController.getInstance().registraUtente(nome, cognome, username, password, ruolo)) {
                JOptionPane.showMessageDialog(this,
                        "Registrazione avvenuta con successo!");
                dispose();

                new GUI_Login();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Username già in uso!",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            }

        });


        annullaButton.addActionListener(e -> {
            dispose();
            new GUI_Login();
        });
    }
}
