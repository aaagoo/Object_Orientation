package GUI;

import modello.UtenteGenerico;
import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class GUI_ModificaAccountPersonale extends JFrame {


    private JPanel mainpanel;
    private JLabel imageLabel;
    private JPanel operationsPanel;
    private JTextField passwordField;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField nomeutenteField;
    private JTextField rippasswordField;
    private JPanel buttonsPanel;
    private JButton confermaButton;
    private JButton annullaButton;
    private JButton eliminaButton;
    private JPanel imagePanel;
    private JLabel nomeutenteLabel;

    public GUI_ModificaAccountPersonale(UtenteGenerico utente) {
        setContentPane(mainpanel);
        setTitle("Modifica Account Personale");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/user.png"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(125, 125, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);

        nomeutenteLabel.setText(utente.getNomeUtente());
        nomeField.setText(utente.getNome());
        cognomeField.setText(utente.getCognome());
        passwordField.setText(utente.getPassword());
        rippasswordField.setText(utente.getPassword());

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_AreaPersonale(utente);
                dispose();
            }
        });

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = nomeField.getText();
                String cognome = cognomeField.getText();
                String password = passwordField.getText();
                String confermaPassword = rippasswordField.getText();

                if (nome.isEmpty() || cognome.isEmpty() || password.isEmpty() || confermaPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Tutti i campi sono obbligatori!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confermaPassword)) {
                    JOptionPane.showMessageDialog(null,
                            "Le password non coincidono!",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    Controller.getInstance().modificaUtente(utente.getNomeUtente(), nome, cognome, password);
                    JOptionPane.showMessageDialog(null,
                            "Dati modificati con successo!",
                            "Successo",
                            JOptionPane.INFORMATION_MESSAGE);

                    utente.setNome(nome);
                    utente.setCognome(cognome);
                    utente.setPassword(password);

                    new GUI_AreaPersonale(utente);
                    dispose();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Errore durante la modifica dei dati: " + ex.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    try {
                        Controller.getInstance().eliminaUtente(utente.getNomeUtente());
                        JOptionPane.showMessageDialog(null,
                                "Account eliminato con successo!",
                                "Successo",
                                JOptionPane.INFORMATION_MESSAGE);

                        JOptionPane.showMessageDialog(null,
                                "Hai eliminato il tuo account.\n" +
                                        "Sarai reindirizzato alla pagina di login.",
                                "Avviso",
                                JOptionPane.INFORMATION_MESSAGE);

                        new GUI_Login();
                        dispose();

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Errore durante l'eliminazione dell'account: " + ex.getMessage(),
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    }
            }
        });
    }
}
