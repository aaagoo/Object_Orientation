package modello;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class VoloPartenza extends Volo {
    private Gate gate;

    public VoloPartenza(String codice,
                        String compagniaAerea,
                        String aeroportoDestinazione,
                        LocalDate data,
                        LocalTime orario,
                        Duration ritardo,
                        StatoVolo stato,
                        Gate gate) {
        super(codice, compagniaAerea, "Napoli", aeroportoDestinazione, data, orario, ritardo, stato);
        this.gate = gate;
    }

    public Gate getGate() {
        return gate;
    }

    public void setGate(Gate gate) {
        this.gate = gate;
    }

    @Override
    public String toString() {
        return super.toString() + "Gate: " + gate;
    }
}

