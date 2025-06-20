package modello;

import java.util.Map;
import java.util.HashMap;

import implementazioneDAO.DAOimpl_Utente;

import java.sql.SQLException;

public abstract class Utente {
    private static final Map<String, Utente> utentiRegistrati = new HashMap<>();
    private String nomeUtente;
    private String password;


    protected Utente(String nomeUtente, String password) {
        this.nomeUtente = nomeUtente;
        this.password = password;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected boolean registra() {
        try {
            if (this instanceof UtenteGenerico) {
                boolean success = DAOimpl_Utente.getInstance().registraUtenteGenerico((UtenteGenerico) this);
                if (success) {
                    utentiRegistrati.put(this.getNomeUtente(), this);
                }
                return success;
            } else if (this instanceof AmministratoreSistema) {
                boolean success = DAOimpl_Utente.getInstance().registraAmministratore((AmministratoreSistema) this);
                if (success) {
                    utentiRegistrati.put(this.getNomeUtente(), this);
                }
                return success;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Utente autenticaUtente(String username, String password) {
        Utente utente = utentiRegistrati.get(username);
        if (utente != null && utente.getPassword().equals(password)) {
            return utente;
        }
        return null;
    }
}