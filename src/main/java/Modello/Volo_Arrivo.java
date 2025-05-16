package Modello;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class Volo_Arrivo extends Volo {

    public Volo_Arrivo(String Codice,
                       String Compagnia_Aerea,
                       String Aeroporto_Origine,
                       LocalDate Data,
                       LocalTime Orario,
                       Duration Ritardo,
                       Stato_Volo Stato) {
        super(Codice, Compagnia_Aerea, Aeroporto_Origine, "Napoli", Data, Orario, Ritardo, Stato);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}