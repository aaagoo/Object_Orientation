package dao;

import modello.Utente;
import modello.UtenteGenerico;
import modello.AmministratoreSistema;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DAO_Utente {

    List<Map<String, Object>> getTuttiUtenti() throws SQLException;
    List<Map<String, Object>> getTuttiAmministratori() throws SQLException;

    boolean registraUtenteGenerico(UtenteGenerico utente) throws SQLException;
    boolean registraAmministratore(AmministratoreSistema admin) throws SQLException;

    void modificaUtente(String nomeutente, String nuovaPassword, String nuovoNome, String nuovoCognome) throws SQLException;
    void modificaAdmin(String nomeutente, String nuovaPassword) throws SQLException;

    void eliminaUtente(String username) throws SQLException;
    void eliminaAdmin(String username) throws SQLException;

    Utente verificaCredenziali(String nomeutente, String password) throws SQLException;
}