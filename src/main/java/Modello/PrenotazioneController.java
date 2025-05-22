package Modello;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;


public class PrenotazioneController {
    private static PrenotazioneController instance;
    private final Map<String, Prenotazione> prenotazioni;
    private int numeroPrenotazioneCorrente;

    private PrenotazioneController() {
        this.prenotazioni = new HashMap<>();
        this.numeroPrenotazioneCorrente = 1;
    }

    public static PrenotazioneController getInstance() {
        if (instance == null) {
            instance = new PrenotazioneController();
        }
        return instance;
    }

    public Prenotazione creaPrenotazione(String nomePasseggero, String cognomePasseggero,
                                         String codiceFiscale, Volo volo) {

        String numeroBiglietto = generaNumeroBiglietto();
        String postoAssegnato = generaPostoCasuale();

        Prenotazione prenotazione = new Prenotazione(
                numeroBiglietto,
                postoAssegnato,
                Stato_Prenotazione.Confermata,
                nomePasseggero,
                cognomePasseggero,
                codiceFiscale,
                volo
        );

        prenotazioni.put(numeroBiglietto, prenotazione);
        return prenotazione;
    }

    private String generaNumeroBiglietto() {
        String numeroBiglietto = String.format("PRE%06d", numeroPrenotazioneCorrente);
        numeroPrenotazioneCorrente++;
        return numeroBiglietto;
    }

    private String generaPostoCasuale() {
        // Implementazione semplificata - da migliorare con gestione posti reale
        Random random = new Random();
        int fila = random.nextInt(30) + 1;
        char lettera = (char) ('A' + random.nextInt(6));
        return fila + String.valueOf(lettera);
    }

    public List<Prenotazione> cercaPrenotazioniPerPasseggero(String nome, String cognome) {
        return prenotazioni.values().stream()
                .filter(p -> p.getNome_Passeggero().equalsIgnoreCase(nome) &&
                        p.getCognome_Passeggero().equalsIgnoreCase(cognome))
                .collect(Collectors.toList());
    }

    public List<Prenotazione> cercaPrenotazioniPerCodiceVolo(String codiceVolo) {
        return prenotazioni.values().stream()
                .filter(p -> p.getVolo().getCodice().equals(codiceVolo))
                .collect(Collectors.toList());
    }

}

