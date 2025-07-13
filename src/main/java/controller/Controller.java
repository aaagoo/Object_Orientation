
package controller;

import dao.DAO_Utente;
import dao.DAO_Volo;
import dao.DAO_Prenotazione;
import implementazioneDAO.DAOimpl_Utente;
import implementazioneDAO.DAOimpl_Volo;
import implementazioneDAO.DAOimpl_Prenotazione;
import modello.*;
import java.util.List;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;

public class Controller {
    private static Controller instance;
    private final DAO_Volo DAO_Volo;
    private final DAO_Prenotazione DAO_Prenotazione;
    private final DAO_Utente DAO_Utente;

    private Controller() {
        this.DAO_Volo = DAOimpl_Volo.getInstance();
        this.DAO_Prenotazione = DAOimpl_Prenotazione.getInstance();
        this.DAO_Utente = DAOimpl_Utente.getInstance();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    // Gestione degli utenti
    public boolean registraUtenteGenerico(String nome, String cognome, String username, String password) {
        try {
            return DAO_Utente.registraUtenteGenerico(new UtenteGenerico(nome, cognome, username, password));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registraAmministratore(String username, String password) {
        try {
            return DAO_Utente.registraAmministratore(new AmministratoreSistema(username, password));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utente login(String nomeutente, String password) {
        try {
            return DAO_Utente.verificaCredenziali(nomeutente, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> getTuttiUtenti() {
        try {
            return DAO_Utente.getTuttiUtenti();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getTuttiAmministratori() {
        try {
            return DAO_Utente.getTuttiAmministratori();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void modificaUtente(String vecchioUsername, String nuovoNome, String nuovoCognome,
                               String nuovoUsername, String nuovaPassword) throws SQLException {
        UtenteGenerico utente = new UtenteGenerico(nuovoNome, nuovoCognome, vecchioUsername, nuovaPassword);
        DAO_Utente.modificaUtente(utente);
    }

    public void eliminaUtente(String username) throws SQLException {
        DAO_Utente.eliminaUtente(username);
    }

    // Gestione dei voli
    public List<Volo> getAllVoli() {
        try {
            return DAO_Volo.getTuttiVoli();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<VoloArrivo> getVoliArrivo() {
        try {
            return DAO_Volo.getVoliArrivo();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<VoloPartenza> getVoliPartenza() {
        try {
            return DAO_Volo.getVoliPartenza();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void aggiungiVolo(Volo volo) throws SQLException {
        DAO_Volo.aggiungiVolo(volo);
    }

    public Volo cercaVoloPerCodice(String codice) {
        try {
            return DAO_Volo.cercaPerCodice(codice);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public VoloPartenza trovaVoloPartenza(String codice) {
        try {
            return DAO_Volo.trovaVoloPartenza(codice);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void modificaVolo(Volo volo) throws SQLException {
        DAO_Volo.modificaVolo(volo);
    }

    public void eliminaVolo(String codice) throws SQLException {
        DAO_Volo.eliminaVolo(codice);
    }

    // Gestione delle prenotazioni
    public List<Prenotazione> getTuttePrenotazioni() throws SQLException {
        return DAO_Prenotazione.getTuttePrenotazioni();
    }

    public Prenotazione creaPrenotazione(String nomePasseggero, String cognomePasseggero,
                                         Volo volo, UtenteGenerico utente) throws SQLException {
        return DAO_Prenotazione.creaPrenotazione(nomePasseggero, cognomePasseggero, volo, utente);
    }

    public List<Prenotazione> cercaPrenotazioniPerPasseggero(String nome, String cognome)
            throws SQLException {
        return DAO_Prenotazione.cercaPerPasseggero(nome, cognome);
    }

    public List<Prenotazione> cercaPrenotazioniPerCodiceVolo(String codiceVolo)
            throws SQLException {
        return DAO_Prenotazione.cercaPerCodiceVolo(codiceVolo);
    }

    public List<Prenotazione> cercaPrenotazioniPerCreatore(String usernamePrenotazione)
            throws SQLException {
        return DAO_Prenotazione.cercaPerCreatore(usernamePrenotazione);
    }

    public List<Prenotazione> cercaPrenotazioniPerCodice(String numeroBiglietto)
            throws SQLException {
        return DAO_Prenotazione.cercaPerCodice(numeroBiglietto);
    }

    public void aggiornaStatoPrenotazione(String numeroBiglietto, StatoPrenotazione nuovoStato)
            throws SQLException {
        DAO_Prenotazione.aggiornaStatoPrenotazione(numeroBiglietto, nuovoStato);
    }

    public void eliminaPrenotazione(String numeroBiglietto) throws SQLException {
        DAO_Prenotazione.eliminaPrenotazione(numeroBiglietto);
    }

    public void modificaPrenotazione(String nomeutente, String codiceVoloVecchio,
                                     String codiceVoloNuovo, String nuovoNome,
                                     String nuovoCognome) throws SQLException {
        DAO_Prenotazione.modificaPrenotazione(nomeutente, codiceVoloVecchio,
                codiceVoloNuovo, nuovoNome, nuovoCognome);
    }
}