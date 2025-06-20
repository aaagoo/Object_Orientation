
package modello;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Prenotazione {
    private static final Map<String, Prenotazione> prenotazioni = new HashMap<>();
    private static int numeroPrenotazioneCorrente = 1;

    private String numeroBiglietto;
    private String postoAssegnato;
    private StatoPrenotazione stato;
    private String nomePasseggero;
    private String cognomePasseggero;
    private String codiceFiscale;
    private Volo volo;

    public Prenotazione(String numeroBiglietto, String postoAssegnato, StatoPrenotazione stato,
                        String nomePasseggero, String cognomePasseggero, String codiceFiscale,
                        Volo volo) {
        this.numeroBiglietto = numeroBiglietto;
        this.postoAssegnato = postoAssegnato;
        this.stato = stato;
        this.nomePasseggero = nomePasseggero;
        this.cognomePasseggero = cognomePasseggero;
        this.codiceFiscale = codiceFiscale;
        this.volo = volo;
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

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public Volo getVolo() {
        return volo;
    }

    public void setVolo(Volo volo) {
        this.volo = volo;
    }

    public static Prenotazione creaPrenotazione(String nomePasseggero, String cognomePasseggero,
                                                String codiceFiscale, Volo volo) {
        String numeroBiglietto = generaNumeroBiglietto();
        String postoAssegnato = generaPostoCasuale();

        Prenotazione prenotazione = new Prenotazione(
                numeroBiglietto,
                postoAssegnato,
                StatoPrenotazione.CONFERMATA,
                nomePasseggero,
                cognomePasseggero,
                codiceFiscale,
                volo
        );

        prenotazioni.put(numeroBiglietto, prenotazione);
        return prenotazione;
    }

    private static String generaNumeroBiglietto() {
        String numeroBiglietto = String.format("PRE%06d", numeroPrenotazioneCorrente);
        numeroPrenotazioneCorrente++;
        return numeroBiglietto;
    }

    private static String generaPostoCasuale() {
        Random random = new Random();
        int fila = random.nextInt(30) + 1;
        char lettera = (char) ('A' + random.nextInt(6));
        return fila + String.valueOf(lettera);
    }

    public static List<Prenotazione> cercaPerPasseggero(String nome, String cognome) {
        return prenotazioni.values().stream()
                .filter(p -> p.getNomePasseggero().equalsIgnoreCase(nome) &&
                        p.getCognomePasseggero().equalsIgnoreCase(cognome))
                .collect(Collectors.toList());
    }

    public static List<Prenotazione> cercaPerCodiceVolo(String codiceVolo) {
        return prenotazioni.values().stream()
                .filter(p -> p.getVolo().getCodice().equals(codiceVolo))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "Numero_Biglietto='" + numeroBiglietto + '\'' +
                ", Posto_Assegnato='" + postoAssegnato + '\'' +
                ", Stato=" + stato +
                ", Nome_Passeggero='" + nomePasseggero + '\'' +
                ", Cognome_Passeggero='" + cognomePasseggero + '\'' +
                ", Codice_Fiscale='" + codiceFiscale + '\'' +
                ", Volo=" + volo +
                '}';
    }
}