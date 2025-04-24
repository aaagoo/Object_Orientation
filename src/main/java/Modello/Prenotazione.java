package Modello;

public class Prenotazione {
    private String Numero_Biglietto;
    private String Posto_Assegnato;
    private Stato_Prenotazione Stato;
    private String Nome_Passeggero;
    private String Cognome_Passeggero;
    private String Codice_Fiscale;
    private Volo Volo;

    public Prenotazione(String Numero_Biglietto, String Posto_Assegnato, Stato_Prenotazione Stato,
                        String Nome_Passeggero, String Cognome_Passeggero, String Codice_Fiscale,
                        Volo Volo) {
        this.Numero_Biglietto = Numero_Biglietto;
        this.Posto_Assegnato = Posto_Assegnato;
        this.Stato = Stato;
        this.Nome_Passeggero = Nome_Passeggero;
        this.Cognome_Passeggero = Cognome_Passeggero;
        this.Codice_Fiscale = Codice_Fiscale;
        this.Volo = Volo;
    }

    public String getNumero_Biglietto() {
        return Numero_Biglietto;
    }
    public void setNumero_Biglietto(String Numero_Biglietto) {
        this.Numero_Biglietto = Numero_Biglietto;
    }

    public String getPosto_Assegnato() {
        return Posto_Assegnato;
    }
    public void setPosto_Assegnato(String Posto_Assegnato) {
        this.Posto_Assegnato = Posto_Assegnato;
    }

    public Stato_Prenotazione getStato() {
        return Stato;
    }
    public void setStato(Stato_Prenotazione Stato) {
        this.Stato = Stato;
    }

    public String getNome_Passeggero() {
        return Nome_Passeggero;
    }
    public void setNome_Passeggero(String Nome_Passeggero) {
        this.Nome_Passeggero = Nome_Passeggero;
    }

    public String getCognome_Passeggero() {
        return Cognome_Passeggero;
    }
    public void setCognome_Passeggero(String Cognome_Passeggero) {
        this.Cognome_Passeggero = Cognome_Passeggero;
    }

    public String getCodice_Fiscale() {
        return Codice_Fiscale;
    }
    public void setCodice_Fiscale(String Codice_Fiscale) {
        this.Codice_Fiscale = Codice_Fiscale;
    }

    public Volo getVolo() {
        return Volo;
    }
    public void setVolo(Volo Volo) {
        this.Volo = Volo;
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "Numero_Biglietto='" + Numero_Biglietto + '\'' +
                ", Posto_Assegnato='" + Posto_Assegnato + '\'' +
                ", Stato=" + Stato +
                ", Nome_Passeggero='" + Nome_Passeggero + '\'' +
                ", Cognome_Passeggero='" + Cognome_Passeggero + '\'' +
                ", Codice_Fiscale='" + Codice_Fiscale + '\'' +
                ", Volo=" + Volo +
                '}';
    }
}