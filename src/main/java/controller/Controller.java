package controller;

import implementazioneDAO.DAOimpl_Utente;
import modello.*;
import java.util.List;
import java.sql.SQLException;

public class Controller {
    private static Controller instance;

    private Controller() {
        Volo.inizializzaDatiTest();
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
        return Volo.getTuttiVoli();
    }

    public List<VoloArrivo> getVoliArrivo() {
        return Volo.getVoliArrivo();
    }

    public List<VoloPartenza> getVoliPartenza() {
        return Volo.getVoliPartenza();
    }

    public void aggiungiVolo(Volo volo) {
        Volo.aggiungiVolo(volo);
    }

    public Volo cercaVoloPerCodice(String codice) {
        return Volo.cercaPerCodice(codice);
    }

    public VoloPartenza trovaVoloPartenza(String codice) {
        return Volo.trovaVoloPartenza(codice);
    }

    //gestione delle prenotazioni
    public Prenotazione creaPrenotazione(String nomePasseggero, String cognomePasseggero,
                                         String codiceFiscale, Volo volo) {
        return Prenotazione.creaPrenotazione(nomePasseggero, cognomePasseggero, codiceFiscale, volo);
    }

    public List<Prenotazione> cercaPrenotazioniPerPasseggero(String nome, String cognome) {
        return Prenotazione.cercaPerPasseggero(nome, cognome);
    }

    public List<Prenotazione> cercaPrenotazioniPerCodiceVolo(String codiceVolo) {
        return Prenotazione.cercaPerCodiceVolo(codiceVolo);
    }
}