package Controller;

import Modello.*;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.stream.Collectors;


public class VoloController {
    private static VoloController instance;
    private final List<Volo> voli;

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

        voli.add(new Volo_Arrivo(
                "BL2435",
                "EasyJet",
                "Madrid",
                LocalDate.now(),
                LocalTime.of(9, 45),
                Duration.ZERO,
                Stato_Volo.Programmato
        ));

        voli.add(new Volo_Arrivo(
                "AH8364",
                "Ryanair",
                "Barcellona",
                LocalDate.now(),
                LocalTime.of(10, 55),
                Duration.ZERO,
                Stato_Volo.Programmato
        ));

        Volo_Arrivo voloGS6859 = new Volo_Arrivo(
                "GS6859",
                "Turkish Airlines",
                "Istanbul",
                LocalDate.now(),
                LocalTime.of(20, 30),
                Duration.ofMinutes(15),
                Stato_Volo.In_Ritardo
        );
        voloGS6859.setRitardo(15);
        voli.add(voloGS6859);

        voli.add(new Volo_Arrivo(
                "GB9405",
                "British Airways",
                "Londra",
                LocalDate.now(),
                LocalTime.of(16, 5),
                Duration.ZERO,
                Stato_Volo.Programmato
        ));

        Volo_Arrivo voloAZ1234 = new Volo_Arrivo(
                "AZ1234",
                "Alitalia",
                "Roma",
                LocalDate.now(),
                LocalTime.of(12, 30),
                Duration.ofMinutes(35),
                Stato_Volo.In_Ritardo
        );
        voloAZ1234.setRitardo(35);
        voli.add(voloAZ1234);

        voli.add(new Volo_Arrivo(
                "FR5678",
                "Ryanair",
                "Bologna",
                LocalDate.now(),
                LocalTime.of(14, 45),
                Duration.ZERO,
                Stato_Volo.Programmato
        ));

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

        voli.add(new Volo_Partenza(
                "EA0495",
                "EasyJet",
                "Berlino",
                LocalDate.now(),
                LocalTime.of(8, 25),
                Duration.ZERO,
                Stato_Volo.Programmato,
                new Gate(9)
        ));

        voli.add(new Volo_Partenza(
                "BA7233",
                "Swiss Air",
                "Zurigo",
                LocalDate.now(),
                LocalTime.of(21, 35),
                Duration.ZERO,
                Stato_Volo.Programmato,
                null
        ));

        Volo_Partenza voloBN9932 = new Volo_Partenza(
                "BN9932",
                "Lufthansa",
                "Francoforte",
                LocalDate.now(),
                LocalTime.of(10, 15),
                Duration.ofMinutes(10),
                Stato_Volo.In_Ritardo,
                new Gate(5)
        );
        voloBN9932.setRitardo(10);
        voli.add(voloBN9932);

        voli.add(new Volo_Partenza(
                "AN2758",
                "Air France",
                "Parigi",
                LocalDate.now(),
                LocalTime.of(15, 20),
                Duration.ZERO,
                Stato_Volo.Programmato,
                new Gate(7)
        ));
    }

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

    public void aggiungiVolo(Volo volo) {
        voli.add(volo);
    }

    public Volo cercaVoloPerCodice(String codice) {
        return voli.stream()
                .filter(v -> v.getCodice().equals(codice))
                .findFirst()
                .orElse(null);
    }

    public Volo_Partenza trovaVoloPartenza(String codice) {
        return voli.stream()
                .filter(v -> v instanceof Volo_Partenza && v.getCodice().equals(codice))
                .map(v -> (Volo_Partenza) v)
                .findFirst()
                .orElse(null);
    }
}

