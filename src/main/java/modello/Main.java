package modello;

import GUI.GUI_Login;
import implementazioneDAO.DAOimpl_Utente;

import javax.swing.*;
import java.sql.SQLException;


public class Main {
    public static void main(String[] args) {
        try {
            DAOimpl_Utente.getInstance().inizializzaTabelle();
            new GUI_Login();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Errore durante l'inizializzazione del database: " + e.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}