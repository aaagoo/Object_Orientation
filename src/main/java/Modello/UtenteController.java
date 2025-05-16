package Modello;.

import java.util.Map;
import java.util.HashMap;

public class UtenteController {
    private static UtenteController instance;
    private Map<String, Utente> utentiRegistrati;

    private UtenteController() {
        utentiRegistrati = new HashMap<>();

        utentiRegistrati.put("admin", new Amministratore_Del_Sistema("admin", "admin"));
        utentiRegistrati.put("utente", new Utente_Generico("Mario", "Rossi", "utente", "password"));
    }

    public static UtenteController getInstance() {
        if (instance == null) {
            instance = new UtenteController();
        }
        return instance;
    }

    public boolean registraUtente(String nome, String cognome, String username, String password, String ruolo) {
        if (utentiRegistrati.containsKey(username)) {
            return false;
        }

        Utente nuovoUtente;
        switch (ruolo) {
            case "Amministratore":
                nuovoUtente = new Amministratore_Del_Sistema(username, password);
                break;
            case "Utente Generico":
                nuovoUtente = new Utente_Generico(nome, cognome, username, password);
                break;
            default:
                return false;
        }

        utentiRegistrati.put(username, nuovoUtente);
        return true;
    }

    public Utente login(String username, String password) {
        Utente utente = utentiRegistrati.get(username);
        if (utente != null && utente.getPassword().equals(password)) {
            return utente;
        }
        return null;
    }
}

