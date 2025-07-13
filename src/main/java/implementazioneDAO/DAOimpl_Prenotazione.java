package implementazioneDAO;

import dao.DAO_Prenotazione;
import database.ConnessioneDatabase;
import modello.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOimpl_Prenotazione implements DAO_Prenotazione {
    private static DAOimpl_Prenotazione instance;
    private final DAOimpl_Volo daoVolo;

    private DAOimpl_Prenotazione() {
        this.daoVolo = DAOimpl_Volo.getInstance();
    }

    public static synchronized DAOimpl_Prenotazione getInstance() {
        if (instance == null) {
            instance = new DAOimpl_Prenotazione();
        }
        return instance;
    }

    @Override
    public Prenotazione creaPrenotazione(String nomePasseggero, String cognomePasseggero,
                                         Volo volo, UtenteGenerico utente) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall("CALL crea_prenotazione(?, ?, ?, ?)")) {

            stmt.setString(1, utente.getNomeUtente());
            stmt.setString(2, nomePasseggero);
            stmt.setString(3, cognomePasseggero);
            stmt.setString(4, volo.getCodice());

            stmt.execute();

            // Recupera la prenotazione appena creata
            return cercaUltimaPrenotazioneUtente(utente.getNomeUtente());
        }
    }

    @Override
    public List<Prenotazione> getTuttePrenotazioni() throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM prenotazioni")) {

            return creaListaPrenotazioni(rs);
        }
    }

    @Override
    public List<Prenotazione> cercaPerPasseggero(String nome, String cognome) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM cerca_prenotazioni_passeggero(?, ?)")) {

            stmt.setString(1, nome);
            stmt.setString(2, cognome);

            try (ResultSet rs = stmt.executeQuery()) {
                return creaListaPrenotazioni(rs);
            }
        }
    }

    @Override
    public List<Prenotazione> cercaPerCodiceVolo(String codiceVolo) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM cerca_prenotazioni_volo(?)")) {

            stmt.setString(1, codiceVolo);

            try (ResultSet rs = stmt.executeQuery()) {
                return creaListaPrenotazioni(rs);
            }
        }
    }

    @Override
    public List<Prenotazione> cercaPerCreatore(String usernamePrenotazione) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM i_miei_voli(?)")) {

            stmt.setString(1, usernamePrenotazione);

            try (ResultSet rs = stmt.executeQuery()) {
                return creaListaPrenotazioni(rs);
            }
        }
    }

    @Override
    public List<Prenotazione> cercaPerCodice(String numeroBiglietto) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM prenotazioni WHERE numero_biglietto = ?")) {

            stmt.setString(1, numeroBiglietto);

            try (ResultSet rs = stmt.executeQuery()) {
                return creaListaPrenotazioni(rs);
            }
        }
    }

    @Override
    public void aggiornaStatoPrenotazione(String numeroBiglietto, StatoPrenotazione nuovoStato)
            throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall(
                     "{CALL aggiorna_stato_prenotazione(?, ?::stato_prenotazione)}")) {

            stmt.setString(1, numeroBiglietto);
            stmt.setString(2, nuovoStato.toString());
            stmt.execute();
        }
    }

    @Override
    public void eliminaPrenotazione(String numeroBiglietto) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall("{CALL elimina_prenotazione(?)}")) {

            stmt.setString(1, numeroBiglietto);
            stmt.execute();
        }
    }

    private List<Prenotazione> creaListaPrenotazioni(ResultSet rs) throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        while (rs.next()) {
            prenotazioni.add(creaPrenotazioneDaResultSet(rs));
        }
        return prenotazioni;
    }

    private Prenotazione creaPrenotazioneDaResultSet(ResultSet rs) throws SQLException {
        return new Prenotazione(
                rs.getString("numero_biglietto"),
                rs.getString("posto_assegnato"),
                StatoPrenotazione.valueOf(rs.getString("stato_prenotazione")),
                rs.getString("nome_passeggero"),
                rs.getString("cognome_passeggero"),
                daoVolo.cercaPerCodice(rs.getString("codice_volo")),
                rs.getString("username_prenotazione")
        );
    }

    private Prenotazione cercaUltimaPrenotazioneUtente(String username) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM prenotazioni WHERE username_prenotazione = ? " +
                             "ORDER BY numero_biglietto DESC LIMIT 1")) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return creaPrenotazioneDaResultSet(rs);
                }
                throw new SQLException("Nessuna prenotazione trovata per l'utente " + username);
            }
        }
    }

    @Override
    public void modificaPrenotazione(String nomeutente, String codiceVoloVecchio,
                                     String codiceVoloNuovo, String nuovoNome,
                                     String nuovoCognome) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall("{CALL modifica_prenotazione(?, ?, ?, ?, ?)}")) {

            stmt.setString(1, nomeutente);
            stmt.setString(2, codiceVoloVecchio);
            stmt.setString(3, codiceVoloNuovo);
            stmt.setString(4, nuovoNome);
            stmt.setString(5, nuovoCognome);

            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException("Errore durante la modifica della prenotazione: " + e.getMessage());
        }
    }

}