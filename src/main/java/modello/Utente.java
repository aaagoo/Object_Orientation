package modello;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class Utente {
    private static final Map<String, Utente> utentiRegistrati = new HashMap<>();
    private String nomeUtente;
    private String password;

    static {
        new AmministratoreSistema("admin", "admin").registra();
        new UtenteGenerico("Ciro", "Esposito", "utente", "utente").registra();
    }

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
        if (utentiRegistrati.containsKey(nomeUtente)) {
            return false;
        }
        utentiRegistrati.put(nomeUtente, this);
        return true;
    }

    public static Utente autenticaUtente(String username, String password) {
        Utente utente = utentiRegistrati.get(username);
        if (utente != null && utente.getPassword().equals(password)) {
            return utente;
        }
        return null;
    }

    public abstract List<Volo> visualizzaVoli();
}