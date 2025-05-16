package Modello;.

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;


public abstract class Volo {
    private String Codice;
    private String Compagnia_Aerea;
    private String Aeroporto_Origine;
    private String Aeroporto_Destinazione;
    private LocalDate Data;
    private LocalTime Orario;
    private long Ritardo;
    private Stato_Volo Stato;

    public Volo(String Codice, String Compagnia_Aerea, String Aeroporto_Origine,
                String Aeroporto_Destinazione, LocalDate Data, LocalTime Orario, Duration ritardo, Stato_Volo stato) {
        this.Codice = Codice;
        this.Compagnia_Aerea = Compagnia_Aerea;
        this.Aeroporto_Origine = Aeroporto_Origine;
        this.Aeroporto_Destinazione = Aeroporto_Destinazione;
        this.Data = Data;
        this.Orario = Orario;
        this.Ritardo = 0;
        this.Stato = Stato_Volo.Programmato;
    }

    public String getCodice() {
        return Codice;
    }

    public void setCodice(String Codice) {
        this.Codice = Codice;
    }

    public String getCompagnia_Aerea() {
        return Compagnia_Aerea;
    }

    public void setCompagnia_Aerea(String Compagnia_Aerea) {
        this.Compagnia_Aerea = Compagnia_Aerea;
    }

    public String getAeroporto_Origine() {
        return Aeroporto_Origine;
    }

    public void setAeroporto_Origine(String Aeroporto_Origine) {
        this.Aeroporto_Origine = Aeroporto_Origine;
    }

    public String getAeroporto_Destinazione() {
        return Aeroporto_Destinazione;
    }

    public void setAeroporto_Destinazione(String Aeroporto_Destinazione) {
        this.Aeroporto_Destinazione = Aeroporto_Destinazione;
    }

    public LocalDate getData() {
        return Data;
    }

    public void setData(LocalDate Data) {
        this.Data = Data;
    }

    public LocalTime getOrario() {
        return Orario;
    }

    public void setOrario(LocalTime Orario) {
        this.Orario = Orario;
    }

    public long getRitardo() {
        return Ritardo;
    }

    public void setRitardo(long Ritardo) {
        this.Ritardo = Ritardo;
    }

    public Stato_Volo getStato() {
        return Stato;
    }

    public void setStato(Stato_Volo Stato) {
        this.Stato = Stato;
    }

    @Override
    public String toString() {
        return "Volo{" +
                "Codice='" + Codice + '\'' +
                ", Compagnia_Aerea='" + Compagnia_Aerea + '\'' +
                ", Aeroporto_Origine='" + Aeroporto_Origine + '\'' +
                ", Aeroporto_Destinazione='" + Aeroporto_Destinazione + '\'' +
                ", Data=" + Data +
                ", Orario=" + Orario +
                ", Ritardo=" + Ritardo +
                ", Stato=" + Stato +
                '}';
    }
}