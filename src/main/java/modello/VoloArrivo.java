package modello;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class VoloArrivo extends Volo {

    public VoloArrivo(String codice,
                      String compagniaAerea,
                      String aeroportoOrigine,
                      LocalDate data,
                      LocalTime orario,
                      Duration ritardo,
                      StatoVolo stato) {
        super(codice, compagniaAerea, aeroportoOrigine, "Napoli", data, orario, ritardo, stato, TipoVolo.ARRIVO);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}