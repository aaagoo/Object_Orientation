package Modello;

import java.util.List;


public class Utente_Generico extends Utente {
    private String Nome;
    private String Cognome;

    public Utente_Generico(String Nome, String Cognome, String Nome_Utente, String Password) {
        super(Nome_Utente, Password);
        this.Nome = Nome;
        this.Cognome = Cognome;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String Nome) {
        this.Nome = Nome;
    }

    public String getCognome() {
        return Cognome;
    }

    public void setCognome(String Cognome) {
        this.Cognome = Cognome;
    }

    public List<Volo> Visualizza_Voli() {

        return null;
    }

}