package dao;

import modello.Volo;
import modello.VoloArrivo;
import modello.VoloPartenza;
import java.util.List;


public interface DAO_Volo {

    // Metodi per ottenere i voli
    List<Volo> getTuttiVoli();
    List<VoloArrivo> getVoliArrivo();
    List<VoloPartenza> getVoliPartenza();

    // Metodi per cercare voli specifici
    Volo cercaPerCodice(String codice);
    VoloPartenza trovaVoloPartenza(String codice);

    // Metodi per gestire i voli
    void aggiungiVolo(Volo volo);
    void modificaVolo(Volo volo);
    void eliminaVolo(String codice);


}
