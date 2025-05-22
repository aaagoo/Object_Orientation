package Modello;

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

                    Utente utente = UtenteController.getInstance().login(username, password);
                    if (utente != null) {
                        dispose();

                        if (utente instanceof Amministratore_Del_Sistema) {

                            mostraInterfacciaAmministratore((Amministratore_Del_Sistema) utente);

                        } else if (utente instanceof Utente_Generico) {

                            mostraInterfacciaUtente((Utente_Generico) utente);
                        }

                    } else {
                        JOptionPane.showMessageDialog(GUI_Login.this,
                                "Credenziali non valide!",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

        }

    private void mostraInterfacciaAmministratore(Amministratore_Del_Sistema admin) {

        new GUI_HomeAmministratore(admin);
    }

    private void mostraInterfacciaUtente(Utente_Generico utente) {

        new GUI_HomeUtente(utente);
    }

}

