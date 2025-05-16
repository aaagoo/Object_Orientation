package Modello;.

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class Volo_Partenza extends Volo {
    private Gate Gate;

    public Volo_Partenza(String Codice,
                         String Compagnia_Aerea,
                         String Aeroporto_Destinazione,
                         LocalDate Data,
                         LocalTime Orario,
                         Duration Ritardo,
                         Stato_Volo Stato,
                         Gate Gate) {
        super(Codice, Compagnia_Aerea, "Napoli", Aeroporto_Destinazione, Data, Orario, Ritardo, Stato);
        this.Gate = Gate;
    }

    public Gate getGate() {
        return Gate;
    }

    public void setGate(Gate Gate) {
        this.Gate = Gate;
    }

    @Override
    public String toString() {
        return super.toString() + "Gate: " + Gate;
    }
}

