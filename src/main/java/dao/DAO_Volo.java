package dao;

import modello.Volo;
import modello.VoloArrivo;
import modello.VoloPartenza;
import java.sql.SQLException;
import java.util.List;
import modello.StatoVolo;


public interface DAO_Volo {

    List<Volo> getTuttiVoli() throws SQLException;
    List<VoloArrivo> getVoliArrivo() throws SQLException;
    List<VoloPartenza> getVoliPartenza() throws SQLException;

    Volo cercaPerCodice(String codice) throws SQLException;
    VoloPartenza trovaVoloPartenza(String codice) throws SQLException;

    void aggiungiVolo(Volo volo) throws SQLException;
    void modificaVolo(Volo volo) throws SQLException;
    void assegnaGate(String codiceVolo, int numeroGate) throws SQLException;
    void aggiornaStatoVolo(String codiceVolo, StatoVolo nuovoStato, long nuovoRitardo) throws SQLException;
void eliminaVolo(String codice) throws SQLException;
}
