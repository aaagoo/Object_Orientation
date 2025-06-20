package GUI;

import modello.AmministratoreSistema;
import modello.Utente;
import controller.Controller;
import modello.UtenteGenerico;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_Login extends JFrame {
    private JPanel mainpanel;
    private JButton accediButton;
    private JButton registratiButton;
    private JTextField userfield;
    private JPasswordField pswfield;


    public GUI_Login() {
        setContentPane(mainpanel);
        setTitle("Login");
        setSize(400,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


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
                    String username = userfield.getText();
                    String password = new String(pswfield.getPassword());

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

