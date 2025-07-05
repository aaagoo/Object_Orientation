package modello;

public class Prenotazione {

    private String numeroBiglietto;
    private String postoAssegnato;
    private StatoPrenotazione stato;
    private String nomePasseggero;
    private String cognomePasseggero;
    private Volo volo;
    private String usernamePrenotazione;

    public Prenotazione(String numeroBiglietto, String postoAssegnato, StatoPrenotazione stato,
                        String nomePasseggero, String cognomePasseggero,
                        Volo volo, String usernamePrenotazione) {
        this.numeroBiglietto = numeroBiglietto;
        this.postoAssegnato = postoAssegnato;
        this.stato = stato;
        this.nomePasseggero = nomePasseggero;
        this.cognomePasseggero = cognomePasseggero;
        this.volo = volo;
        this.usernamePrenotazione = usernamePrenotazione;
    }

    public String getNumeroBiglietto() {
        return numeroBiglietto;
    }

    public void setNumeroBiglietto(String numeroBiglietto) {
        this.numeroBiglietto = numeroBiglietto;
    }

    public String getPostoAssegnato() {
        return postoAssegnato;
    }

    public void setPostoAssegnato(String postoAssegnato) {
        this.postoAssegnato = postoAssegnato;
    }

    public StatoPrenotazione getStato() {
        return stato;
    }

    public void setStato(StatoPrenotazione stato) {
        this.stato = stato;
    }

    public String getNomePasseggero() {
        return nomePasseggero;
    }

    public void setNomePasseggero(String nomePasseggero) {
        this.nomePasseggero = nomePasseggero;
    }

    public String getCognomePasseggero() {
        return cognomePasseggero;
    }

    public void setCognomePasseggero(String cognomePasseggero) {
        this.cognomePasseggero = cognomePasseggero;
    }

    public Volo getVolo() {
        return volo;
    }

    public void setVolo(Volo volo) {
        this.volo = volo;
    }

    public String getUsernamePrenotazione() {
        return usernamePrenotazione;
    }

    public void setUsernamePrenotazione(String usernamePrenotazione) {
        this.usernamePrenotazione = usernamePrenotazione;
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "Numero_Biglietto='" + numeroBiglietto + '\'' +
                ", Posto_Assegnato='" + postoAssegnato + '\'' +
                ", Stato=" + stato +
                ", Nome_Passeggero='" + nomePasseggero + '\'' +
                ", Cognome_Passeggero='" + cognomePasseggero + '\'' +
                ", Volo=" + volo +
                ", Username_Prenotazione='" + usernamePrenotazione + '\'' +
                '}';
    }
}