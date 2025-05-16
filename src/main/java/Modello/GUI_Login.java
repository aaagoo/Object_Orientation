package Modello;

import javax.swing.*;
import javax.swing.DefaultComboBoxModel;
import java.awt.*;

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


            registratiButton.addActionListener(e -> {

                setVisible(false);
                new GUI_Registrazione();
            });

            accediButton.addActionListener(e -> {
                String username = userfield.getText();
                String password = new String(pswfield.getPassword());

                Utente utente = UtenteController.getInstance().login(username, password);
                if (utente != null) {
                    setVisible(false);
                    // Apri l'interfaccia appropriata in base al tipo di utente
                    if (utente instanceof Amministratore_Del_Sistema) {
                        mostraInterfacciaAmministratore((Amministratore_Del_Sistema) utente);

                    } else if (utente instanceof Utente_Generico) {

                        mostraInterfacciaUtente((Utente_Generico) utente);
                    }

                } else {
                    JOptionPane.showMessageDialog(this,
                            "Credenziali non valide!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

        }

    private void mostraInterfacciaAmministratore(Amministratore_Del_Sistema admin) {

        JFrame adminFrame = new JFrame("Pannello Amministratore");
    }

    private void mostraInterfacciaUtente(Utente_Generico utente) {

        JFrame userFrame = new JFrame("Area Utente");
    }

}

