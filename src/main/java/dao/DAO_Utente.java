package dao;

import modello.Utente;
import modello.UtenteGenerico;
import modello.AmministratoreSistema;

import java.sql.SQLException;



public interface DAO_Utente {

    void inizializzaTabelle() throws SQLException;

    boolean registraUtenteGenerico(UtenteGenerico utente) throws SQLException;

    boolean registraAmministratore(AmministratoreSistema admin) throws SQLException;

    Utente verificaCredenziali(String nomeutente, String password) throws SQLException;
}
