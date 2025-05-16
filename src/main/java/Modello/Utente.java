package Modello;

import java.util.List;


public abstract class Utente {
    private String Nome_Utente;
    private String Password;

    public Utente(String Nome_Utente, String Password) {
        this.Nome_Utente = Nome_Utente;
        this.Password = Password;
    }

    public String getNome_Utente() {
        return Nome_Utente;
    }
    public void setNome_Utente(String Nome_Utente) {
        this.Nome_Utente = Nome_Utente;
    }

    public String getPassword() {
        return Password;
    }
    public void setPassword(String Password) {
        this.Password = Password;
    }

    public abstract List<Volo> Visualizza_Voli();
}