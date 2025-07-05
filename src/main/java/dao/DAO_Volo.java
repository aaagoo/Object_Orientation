package dao;

import modello.Volo;
import modello.VoloArrivo;
import modello.VoloPartenza;
import java.util.List;


public interface DAO_Volo {

    List<Volo> getTuttiVoli();
    List<VoloArrivo> getVoliArrivo();
    List<VoloPartenza> getVoliPartenza();

    Volo cercaPerCodice(String codice);
    VoloPartenza trovaVoloPartenza(String codice);

    void aggiungiVolo(Volo volo);
    void modificaVolo(Volo volo);
    void eliminaVolo(String codice);


}
