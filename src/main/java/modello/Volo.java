package modello;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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