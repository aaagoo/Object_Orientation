package implementazioneDAO;

import dao.DAO_Prenotazione;
import database.ConnessioneDatabase;
import modello.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DAOimpl_Prenotazione implements DAO_Prenotazione {
    private static DAOimpl_Prenotazione instance;
    private final DAOimpl_Volo daoVolo;
    private static final String PRENOTAZIONE_BASE_QUERY = "SELECT * FROM prenotazione";

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
    public List<Prenotazione> getTuttePrenotazioni() {
        return eseguiQuery(PRENOTAZIONE_BASE_QUERY, null);
    }

    @Override
    public List<Prenotazione> cercaPerPasseggero(String nome, String cognome) {
        String query = PRENOTAZIONE_BASE_QUERY + " WHERE nome_passeggero = ? AND cognome_passeggero = ?";
        return eseguiQuery(query, stmt -> {
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
        });
    }

    @Override
    public List<Prenotazione> cercaPerCodiceVolo(String codiceVolo) {
        String query = PRENOTAZIONE_BASE_QUERY + " WHERE codice_volo = ?";
        return eseguiQuery(query, stmt -> stmt.setString(1, codiceVolo));
    }

    @Override
    public List<Prenotazione> cercaPerCreatore(String usernamePrenotazione) {
        String query = PRENOTAZIONE_BASE_QUERY + " WHERE username_prenotazione = ?";
        return eseguiQuery(query, stmt -> stmt.setString(1, usernamePrenotazione));
    }

    @Override
    public List<Prenotazione> cercaPerCodice(String numeroBiglietto) {
        String query = PRENOTAZIONE_BASE_QUERY + " WHERE numero_biglietto = ?";
        return eseguiQuery(query, stmt -> stmt.setString(1, numeroBiglietto));
    }

    @Override
    public void aggiungiPrenotazione(Prenotazione prenotazione) throws SQLException {
        String query = """
                INSERT INTO prenotazione (numero_biglietto, posto_assegnato, stato,
                nome_passeggero, cognome_passeggero, codice_volo, username_prenotazione)
                VALUES (?, ?, ?::stato_prenotazione, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {
            impostaParametriPrenotazione(stmt, prenotazione);
            stmt.executeUpdate();
        }
    }

    @Override
    public void modificaPrenotazione(Prenotazione prenotazione) throws SQLException {
        String query = """
                UPDATE prenotazione 
                SET posto_assegnato = ?, stato = ?::stato_prenotazione, nome_passeggero = ?,
                    cognome_passeggero = ?, codice_volo = ?, username_prenotazione = ?
                WHERE numero_biglietto = ?
                """;

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {
            impostaParametriPrenotazione(stmt, prenotazione);
            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminaPrenotazione(String numeroBiglietto) throws SQLException {
        String query = "DELETE FROM prenotazione WHERE numero_biglietto = ?";
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, numeroBiglietto);
            stmt.executeUpdate();
        }
    }

    @Override
    public Prenotazione creaPrenotazione(String nomePasseggero, String cognomePasseggero,
                                         Volo volo, UtenteGenerico utente) throws SQLException {
        Prenotazione prenotazione = new Prenotazione(
                generaNumeroBiglietto(),
                generaPostoCasuale(),
                StatoPrenotazione.CONFERMATA,
                nomePasseggero,
                cognomePasseggero,
                volo,
                utente.getNomeUtente()
        );

        aggiungiPrenotazione(prenotazione);
        return prenotazione;
    }

    private List<Prenotazione> eseguiQuery(String query, PreparedStatementSetter setter) {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (setter != null) {
                setter.setParameters(stmt);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                prenotazioni.add(creaPrenotazioneDaResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prenotazioni;
    }

    private void impostaParametriPrenotazione(PreparedStatement stmt, Prenotazione prenotazione)
            throws SQLException {
        stmt.setString(1, prenotazione.getNumeroBiglietto());
        stmt.setString(2, prenotazione.getPostoAssegnato());
        stmt.setObject(3, prenotazione.getStato().toString());
        stmt.setString(4, prenotazione.getNomePasseggero());
        stmt.setString(5, prenotazione.getCognomePasseggero());
        stmt.setString(6, prenotazione.getVolo().getCodice());
        stmt.setString(7, prenotazione.getUsernamePrenotazione());
    }

    private String generaNumeroBiglietto() throws SQLException {
        String query = """
                SELECT COALESCE(MAX(CAST(SUBSTRING(numero_biglietto, 4) AS INTEGER)), 0) + 1
                FROM prenotazione WHERE numero_biglietto LIKE 'PRE%'
                """;

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("PRE%06d", rs.getInt(1));
            }
            return "PRE000001";
        }
    }

    private String generaPostoCasuale() {
        Random random = new Random();
        int fila = random.nextInt(30) + 1;
        char lettera = (char) ('A' + random.nextInt(6));
        return fila + String.valueOf(lettera);
    }

    private Prenotazione creaPrenotazioneDaResultSet(ResultSet rs) throws SQLException {
        return new Prenotazione(
                rs.getString("numero_biglietto"),
                rs.getString("posto_assegnato"),
                StatoPrenotazione.valueOf(rs.getString("stato")),
                rs.getString("nome_passeggero"),
                rs.getString("cognome_passeggero"),
                daoVolo.cercaPerCodice(rs.getString("codice_volo")),
                rs.getString("username_prenotazione")
        );
    }

    @FunctionalInterface
    private interface PreparedStatementSetter {
        void setParameters(PreparedStatement stmt) throws SQLException;
    }
}