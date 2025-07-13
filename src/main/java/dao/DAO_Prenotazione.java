package dao;

import modello.Prenotazione;
import java.sql.SQLException;
import java.util.List;
import modello.UtenteGenerico;
import modello.Volo;
import modello.StatoPrenotazione;

public interface DAO_Prenotazione {
    Prenotazione creaPrenotazione(String nomePasseggero, String cognomePasseggero,
                                  Volo volo, UtenteGenerico utente) throws SQLException;

    List<Prenotazione> getTuttePrenotazioni() throws SQLException;
    List<Prenotazione> cercaPerPasseggero(String nome, String cognome) throws SQLException;
    List<Prenotazione> cercaPerCodiceVolo(String codiceVolo) throws SQLException;
    List<Prenotazione> cercaPerCreatore(String usernamePrenotazione) throws SQLException;
    List<Prenotazione> cercaPerCodice(String numeroBiglietto) throws SQLException;

    void aggiornaStatoPrenotazione(String numeroBiglietto, StatoPrenotazione nuovoStato) throws SQLException;
    void eliminaPrenotazione(String numeroBiglietto) throws SQLException;
    void modificaPrenotazione(String nomeutente, String codiceVoloVecchio, String codiceVoloNuovo,
                              String nuovoNome, String nuovoCognome) throws SQLException;
}