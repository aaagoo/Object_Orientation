package Modello;

import java.util.List;

public class Utente_Generico extends Utente {
    private String Nome;
    private String Cognome;

    public Utente_Generico(String Nome_Utente, String Password, String Nome, String Cognome) {
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

    public List<Prenotazione> Visualizza_Voli() {

        return null;
    }
    public List<Prenotazione> CercaPrenotazionePerNome(String Nome_Passeggero) {

        return null;
    }
    public List<Prenotazione> CercaPrenotazionePerCodiceVolo(String Codice_Volo) {

        return null;
    }

    public void Prenota_Volo(Prenotazione prenotazione) {

    }
}