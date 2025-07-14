package modello;

import java.util.Map;
import java.util.HashMap;

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

}