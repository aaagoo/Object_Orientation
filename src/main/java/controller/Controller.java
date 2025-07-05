package controller;

import dao.DAO_Volo;
import dao.DAO_Prenotazione;
import implementazioneDAO.DAOimpl_Utente;
import implementazioneDAO.DAOimpl_Volo;
import implementazioneDAO.DAOimpl_Prenotazione;
import modello.*;
import java.util.List;
import java.sql.SQLException;

public class Controller {
    private static Controller instance;
    private final DAO_Volo DAO_Volo;
    private final DAO_Prenotazione DAO_Prenotazione;


    private Controller() {
        this.DAO_Volo = DAOimpl_Volo.getInstance();
        this.DAO_Prenotazione = DAOimpl_Prenotazione.getInstance();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    //gestione degli utenti
    public boolean registraUtenteGenerico(String nome, String cognome, String username, String password) {
        return UtenteGenerico.registraNuovo(nome, cognome, username, password);
    }

    public boolean registraAmministratore(String username, String password) {
        return AmministratoreSistema.registraNuovo(username, password);
    }

    public Utente login(String nomeutente, String password) {
        try {
            return DAOimpl_Utente.getInstance().verificaCredenziali(nomeutente, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //gestione dei voli
    public List<Volo> getAllVoli() {
        return DAO_Volo.getTuttiVoli();
    }

    public List<VoloArrivo> getVoliArrivo() {
        return DAO_Volo.getVoliArrivo();
    }

    public List<VoloPartenza> getVoliPartenza() {
        return DAO_Volo.getVoliPartenza();
    }

    public void aggiungiVolo(Volo volo) {
        DAO_Volo.aggiungiVolo(volo);
    }

    public Volo cercaVoloPerCodice(String codice) {
        return DAO_Volo.cercaPerCodice(codice);
    }

    public VoloPartenza trovaVoloPartenza(String codice) {
        return DAO_Volo.trovaVoloPartenza(codice);
    }

    public void modificaVolo(Volo volo) {
        DAO_Volo.modificaVolo(volo);
    }

    public void eliminaVolo(String codice) {
        DAO_Volo.eliminaVolo(codice);
    }


    //gestione delle prenotazioni
    public List<Prenotazione> getTuttePrenotazioni() {
        return DAO_Prenotazione.getTuttePrenotazioni();
    }

    public Prenotazione creaPrenotazione(String nomePasseggero, String cognomePasseggero,
                                         Volo volo, UtenteGenerico utente) throws SQLException {
        return DAO_Prenotazione.creaPrenotazione(nomePasseggero, cognomePasseggero, volo, utente);
    }

    public List<Prenotazione> cercaPrenotazioniPerPasseggero(String nome, String cognome) {
        return DAO_Prenotazione.cercaPerPasseggero(nome, cognome);
    }

    public List<Prenotazione> cercaPrenotazioniPerCodiceVolo(String codiceVolo) {
        return DAO_Prenotazione.cercaPerCodiceVolo(codiceVolo);
    }

    public List<Prenotazione> cercaPrenotazioniPerCreatore(String usernamePrenotazione) {
        return DAO_Prenotazione.cercaPerCreatore(usernamePrenotazione);
    }

    public List<Prenotazione> cercaPrenotazioniPerCodice(String codicePrenotazione) {
        return DAO_Prenotazione.cercaPerCodice(codicePrenotazione);
    }

    public void modificaPrenotazione(Prenotazione prenotazione) throws SQLException {
        DAO_Prenotazione.modificaPrenotazione(prenotazione);
    }

    public void eliminaPrenotazione(String numeroBiglietto) throws SQLException {
        DAO_Prenotazione.eliminaPrenotazione(numeroBiglietto);
    }

}