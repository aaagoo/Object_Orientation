package Modello;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.stream.Collectors;


public class VoloController {
    private static VoloController instance;
    private List<Volo> voli;

    private VoloController() {
        voli = new ArrayList<>();
        initializeTestData();
    }

    public static VoloController getInstance() {
        if (instance == null) {
            instance = new VoloController();
        }
        return instance;
    }

    private void initializeTestData() {
        // Voli in arrivo
        voli.add(new Volo_Arrivo(
                "AZ1234",
                "Alitalia",
                "Roma",
                LocalDate.now(),
                LocalTime.of(10, 30),
                Duration.ZERO,
                Stato_Volo.Programmato
        ));

        voli.add(new Volo_Arrivo(
                "FR5678",
                "Ryanair",
                "Londra",
                LocalDate.now(),
                LocalTime.of(14, 45),
                Duration.ZERO,
                Stato_Volo.Programmato
        ));

        // Voli in partenza
        voli.add(new Volo_Partenza(
                "AZ2468",
                "Alitalia",
                "Milano",
                LocalDate.now(),
                LocalTime.of(16, 15),
                Duration.ZERO,
                Stato_Volo.Programmato,
                new Gate(1)
        ));

        voli.add(new Volo_Partenza(
                "FR1357",
                "Ryanair",
                "Parigi",
                LocalDate.now(),
                LocalTime.of(18, 30),
                Duration.ZERO,
                Stato_Volo.Programmato,
                new Gate(2)
        ));
    }

    // Metodi per ottenere i voli
    public List<Volo> getAllVoli() {
        return new ArrayList<>(voli);
    }

    public List<Volo_Arrivo> getVoliArrivo() {
        return voli.stream()
                .filter(v -> v instanceof Volo_Arrivo)
                .map(v -> (Volo_Arrivo) v)
                .collect(Collectors.toList());
    }

    public List<Volo_Partenza> getVoliPartenza() {
        return voli.stream()
                .filter(v -> v instanceof Volo_Partenza)
                .map(v -> (Volo_Partenza) v)
                .collect(Collectors.toList());
    }

    // Nuovi metodi di gestione
    public void aggiungiVolo(Volo volo) {
        voli.add(volo);
    }

    public void rimuoviVolo(String codice) {
        voli.removeIf(v -> v.getCodice().equals(codice));
    }

    public Volo cercaVoloPerCodice(String codice) {
        return voli.stream()
                .filter(v -> v.getCodice().equals(codice))
                .findFirst()
                .orElse(null);
    }

    public void aggiornaStatoVolo(String codice, Stato_Volo nuovoStato) {
        voli.stream()
                .filter(v -> v.getCodice().equals(codice))
                .findFirst()
                .ifPresent(v -> v.setStato(nuovoStato));
    }

    public void aggiornaRitardoVolo(String codice, long ritardoMinuti) {
        voli.stream()
                .filter(v -> v.getCodice().equals(codice))
                .findFirst()
                .ifPresent(v -> v.setRitardo(ritardoMinuti));
    }

    public void aggiornaGateVolo(String codice, int nuovoNumeroGate) {
        voli.stream()
                .filter(v -> v instanceof Volo_Partenza && v.getCodice().equals(codice))
                .map(v -> (Volo_Partenza) v)
                .findFirst()
                .ifPresent(v -> v.setGate(new Gate(nuovoNumeroGate)));
    }

    public List<Volo> cercaVoliPerStato(Stato_Volo stato) {
        return voli.stream()
                .filter(v -> v.getStato() == stato)
                .collect(Collectors.toList());
    }

    public List<Volo_Partenza> cercaVoliPerGate(int numeroGate) {
        return getVoliPartenza().stream()
                .filter(v -> v.getGate().getNumero_Gate() == numeroGate)
                .collect(Collectors.toList());
    }
}

