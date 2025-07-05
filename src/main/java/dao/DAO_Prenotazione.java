package dao;

import modello.Prenotazione;
import java.sql.SQLException;
import java.util.List;
import modello.UtenteGenerico;
import modello.Volo;

public interface DAO_Prenotazione {

    Prenotazione creaPrenotazione(String nomePasseggero, String cognomePasseggero,
                                  Volo volo, UtenteGenerico utente) throws SQLException;

// Metodi di ricerca
    List<Prenotazione> getTuttePrenotazioni();
    List<Prenotazione> cercaPerPasseggero(String nome, String cognome);
    List<Prenotazione> cercaPerCodiceVolo(String codiceVolo);
    List<Prenotazione> cercaPerCreatore(String usernamePrenotazione);
    List<Prenotazione> cercaPerCodice(String numeroBiglietto);

    // Metodi di gestione
    void aggiungiPrenotazione(Prenotazione prenotazione) throws SQLException;
    void modificaPrenotazione(Prenotazione prenotazione) throws SQLException;
    void eliminaPrenotazione(String numeroBiglietto) throws SQLException;
}