package modello;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Volo {
    private static final List<Volo> voli = new ArrayList<>();

    private String codice;
    private String compagniaAerea;
    private String aeroportoOrigine;
    private String aeroportoDestinazione;
    private LocalDate data;
    private LocalTime orario;
    private long ritardo;
    private StatoVolo stato;

    protected Volo(String codice, String compagniaAerea, String aeroportoOrigine,
                   String aeroportoDestinazione, LocalDate data, LocalTime orario,
                   Duration ritardo, StatoVolo stato) {
        this.codice = codice;
        this.compagniaAerea = compagniaAerea;
        this.aeroportoOrigine = aeroportoOrigine;
        this.aeroportoDestinazione = aeroportoDestinazione;
        this.data = data;
        this.orario = orario;
        this.ritardo = 0;
        this.stato = StatoVolo.PROGRAMMATO;
    }

    // Metodi getter e setter esistenti
    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getCompagniaAerea() {
        return compagniaAerea;
    }

    public void setCompagniaAerea(String compagniaAerea) {
        this.compagniaAerea = compagniaAerea;
    }

    public String getAeroportoOrigine() {
        return aeroportoOrigine;
    }

    public void setAeroportoOrigine(String aeroportoOrigine) {
        this.aeroportoOrigine = aeroportoOrigine;
    }

    public String getAeroportoDestinazione() {
        return aeroportoDestinazione;
    }

    public void setAeroportoDestinazione(String aeroportoDestinazione) {
        this.aeroportoDestinazione = aeroportoDestinazione;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOrario() {
        return orario;
    }

    public void setOrario(LocalTime orario) {
        this.orario = orario;
    }

    public long getRitardo() {
        return ritardo;
    }

    public void setRitardo(long ritardo) {
        this.ritardo = ritardo;
    }

    public StatoVolo getStato() {
        return stato;
    }

    public void setStato(StatoVolo stato) {
        this.stato = stato;
    }

    // Nuovi metodi statici per la gestione dei voli
    public static void inizializzaDatiTest() {
        voli.add(new VoloArrivo(
                "BL2435",
                "EasyJet",
                "Madrid",
                LocalDate.now(),
                LocalTime.of(9, 45),
                Duration.ZERO,
                StatoVolo.PROGRAMMATO
        ));

        voli.add(new VoloArrivo(
                "AH8364",
                "Ryanair",
                "Barcellona",
                LocalDate.now(),
                LocalTime.of(10, 55),
                Duration.ZERO,
                StatoVolo.PROGRAMMATO
        ));

        VoloArrivo voloGS6859 = new VoloArrivo(
                "GS6859",
                "Turkish Airlines",
                "Istanbul",
                LocalDate.now(),
                LocalTime.of(20, 30),
                Duration.ofMinutes(15),
                StatoVolo.IN_RITARDO
        );
        voloGS6859.setRitardo(15);
        voli.add(voloGS6859);

        voli.add(new VoloArrivo(
                "GB9405",
                "British Airways",
                "Londra",
                LocalDate.now(),
                LocalTime.of(16, 5),
                Duration.ZERO,
                StatoVolo.PROGRAMMATO
        ));

        VoloArrivo voloAZ1234 = new VoloArrivo(
                "AZ1234",
                "Alitalia",
                "Roma",
                LocalDate.now(),
                LocalTime.of(12, 30),
                Duration.ofMinutes(35),
                StatoVolo.IN_RITARDO
        );
        voloAZ1234.setRitardo(35);
        voli.add(voloAZ1234);

        voli.add(new VoloPartenza(
                "AZ2468",
                "Alitalia",
                "Milano",
                LocalDate.now(),
                LocalTime.of(16, 15),
                Duration.ZERO,
                StatoVolo.PROGRAMMATO,
                new Gate(1)
        ));

        voli.add(new VoloPartenza(
                "FR1357",
                "Ryanair",
                "Parigi",
                LocalDate.now(),
                LocalTime.of(18, 30),
                Duration.ZERO,
                StatoVolo.PROGRAMMATO,
                new Gate(2)
        ));

        voli.add(new VoloPartenza(
                "EA0495",
                "EasyJet",
                "Berlino",
                LocalDate.now(),
                LocalTime.of(8, 25),
                Duration.ZERO,
                StatoVolo.PROGRAMMATO,
                new Gate(9)
        ));

        voli.add(new VoloPartenza(
                "BA7233",
                "Swiss Air",
                "Zurigo",
                LocalDate.now(),
                LocalTime.of(21, 35),
                Duration.ZERO,
                StatoVolo.PROGRAMMATO,
                null
        ));

        VoloPartenza voloBN9932 = new VoloPartenza(
                "BN9932",
                "Lufthansa",
                "Francoforte",
                LocalDate.now(),
                LocalTime.of(10, 15),
                Duration.ofMinutes(10),
                StatoVolo.IN_RITARDO,
                new Gate(5)
        );
        voloBN9932.setRitardo(10);
        voli.add(voloBN9932);

    }

    public static List<Volo> getTuttiVoli() {
        return new ArrayList<>(voli);
    }

    public static List<VoloArrivo> getVoliArrivo() {
        return voli.stream()
                .filter(v -> v instanceof VoloArrivo)
                .map(v -> (VoloArrivo) v)
                .collect(Collectors.toList());
    }

    public static List<VoloPartenza> getVoliPartenza() {
        return voli.stream()
                .filter(v -> v instanceof VoloPartenza)
                .map(v -> (VoloPartenza) v)
                .collect(Collectors.toList());
    }

    public static void aggiungiVolo(Volo volo) {
        voli.add(volo);
    }

    public static Volo cercaPerCodice(String codice) {
        return voli.stream()
                .filter(v -> v.getCodice().equals(codice))
                .findFirst()
                .orElse(null);
    }

    public static VoloPartenza trovaVoloPartenza(String codice) {
        return voli.stream()
                .filter(v -> v instanceof VoloPartenza && v.getCodice().equals(codice))
                .map(v -> (VoloPartenza) v)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "Volo{" +
                "Codice='" + codice + '\'' +
                ", Compagnia_Aerea='" + compagniaAerea + '\'' +
                ", Aeroporto_Origine='" + aeroportoOrigine + '\'' +
                ", Aeroporto_Destinazione='" + aeroportoDestinazione + '\'' +
                ", Data=" + data +
                ", Orario=" + orario +
                ", Ritardo=" + ritardo +
                ", Stato=" + stato +
                '}';
    }
}