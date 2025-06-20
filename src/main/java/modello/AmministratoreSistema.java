package modello;

import java.util.List;

public class AmministratoreSistema extends Utente {
    public AmministratoreSistema(String nomeUtente, String password) {
        super(nomeUtente, password);
    }

    public static boolean registraNuovo(String username, String password) {
        AmministratoreSistema nuovoAdmin = new AmministratoreSistema(username, password);
        return nuovoAdmin.registra();
    }

    public List<Volo> visualizzaVoli() {
        return null;
    }
}
