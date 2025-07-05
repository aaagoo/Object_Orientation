package dao;

import modello.Utente;
import modello.UtenteGenerico;
import modello.AmministratoreSistema;
import java.sql.SQLException;
import java.util.List;

public interface DAO_Utente {

    List<UtenteGenerico> getTuttiUtenti() throws SQLException;
    List<AmministratoreSistema> getTuttiAmministratori() throws SQLException;

    boolean registraUtenteGenerico(UtenteGenerico utente) throws SQLException;
    boolean registraAmministratore(AmministratoreSistema admin) throws SQLException;

    void modificaUtente(UtenteGenerico utente) throws SQLException;
    void eliminaUtente(String username) throws SQLException;

    Utente verificaCredenziali(String nomeutente, String password) throws SQLException;
}