package implementazioneDAO;

import dao.DAO_Volo;
import database.ConnessioneDatabase;
import modello.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DAOimpl_Volo implements DAO_Volo {
    private static DAOimpl_Volo instance;

    private DAOimpl_Volo() {}

    public static synchronized DAOimpl_Volo getInstance() {
        if (instance == null) {
            instance = new DAOimpl_Volo();
        }
        return instance;
    }

    @Override
    public List<Volo> getTuttiVoli() throws SQLException {
        List<Volo> voli = new ArrayList<>();
        String query = "SELECT * FROM tutti_voli";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                voli.add(creaVoloDaResultSet(rs));
            }
        }
        return voli;
    }

    @Override
    public List<VoloArrivo> getVoliArrivo() throws SQLException {
        List<VoloArrivo> voliArrivo = new ArrayList<>();
        String query = "SELECT * FROM voli_in_arrivo";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                voliArrivo.add((VoloArrivo) creaVoloDaResultSet(rs));
            }
        }
        return voliArrivo;
    }

    @Override
    public List<VoloPartenza> getVoliPartenza() throws SQLException {
        List<VoloPartenza> voliPartenza = new ArrayList<>();
        String query = "SELECT * FROM voli_in_partenza";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                voliPartenza.add((VoloPartenza) creaVoloDaResultSet(rs));
            }
        }
        return voliPartenza;
    }

    @Override
    public Volo cercaPerCodice(String codice) throws SQLException {
        String query = "SELECT * FROM tutti_voli WHERE codice = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codice);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return creaVoloDaResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public VoloPartenza trovaVoloPartenza(String codice) throws SQLException {
        String query = "SELECT * FROM voli_in_partenza WHERE codice = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codice);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return (VoloPartenza) creaVoloDaResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public void aggiungiVolo(Volo volo) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             // Modifica qui: cambiato da "{CALL ...}" a "CALL ..."
             CallableStatement stmt = conn.prepareCall("CALL aggiungi_volo(?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, volo.getCodice());
            stmt.setString(2, volo.getCompagniaAerea());
            stmt.setString(3, volo.getAeroportoOrigine());
            stmt.setString(4, volo.getAeroportoDestinazione());

            // Convertiamo la data nel formato corretto per il database
            LocalDate data = volo.getData();
            java.sql.Date sqlDate = java.sql.Date.valueOf(data);
            stmt.setDate(5, sqlDate);

            // Convertiamo l'orario nel formato corretto per il database
            LocalTime orario = volo.getOrario();
            java.sql.Time sqlTime = java.sql.Time.valueOf(orario);
            stmt.setTime(6, sqlTime);

            stmt.setString(7, volo instanceof VoloPartenza ? "PARTENZA" : "ARRIVO");

            stmt.execute();

            // Se è un volo in partenza e ha un gate assegnato, lo assegniamo
            if (volo instanceof VoloPartenza) {
                VoloPartenza voloPartenza = (VoloPartenza) volo;
                if (voloPartenza.getGate() != null) {
                    try (CallableStatement stmtGate = conn.prepareCall("CALL assegna_gate(?, ?)")) {
                        stmtGate.setString(1, volo.getCodice());
                        stmtGate.setInt(2, voloPartenza.getGate().getNumeroGate());
                        stmtGate.execute();
                    }
                }
            }
        }
    }



    @Override
    public void modificaVolo(Volo volo) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall("CALL aggiorna_stato_volo(?, ?, ?)")) {

            stmt.setString(1, volo.getCodice());
            stmt.setObject(2, volo.getStato().toString(), Types.OTHER);
            stmt.setInt(3, (int) volo.getRitardo());

            stmt.execute();
        }
    }

    @Override
    public void assegnaGate(String codiceVolo, int numeroGate) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall("CALL assegna_gate(?, ?)")) {

            stmt.setString(1, codiceVolo);
            stmt.setInt(2, numeroGate);
            stmt.execute();
        }
    }

    @Override
    public void aggiornaStatoVolo(String codiceVolo, StatoVolo nuovoStato, long nuovoRitardo) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall("CALL aggiorna_stato_volo(?, ?::stato_volo, ?)")) {

            stmt.setString(1, codiceVolo);
            stmt.setString(2, nuovoStato.toString());
            stmt.setInt(3, (int) nuovoRitardo);

            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'aggiornamento dello stato del volo: " + e.getMessage());
        }
    }


    @Override
    public void eliminaVolo(String codice) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall("CALL elimina_volo(?)")) {

            stmt.setString(1, codice);
            stmt.execute();
        }
    }

    private Volo creaVoloDaResultSet(ResultSet rs) throws SQLException {
        String codice = rs.getString("codice");
        String compagniaAerea = rs.getString("compagnia_aerea");
        String aeroportoOrigine = rs.getString("aeroporto_origine");
        String aeroportoDestinazione = rs.getString("aeroporto_destinazione");
        LocalDate data = rs.getDate("data_partenza").toLocalDate();
        LocalTime orario = rs.getTime("orario").toLocalTime();
        long ritardo = rs.getInt("ritardo");
        StatoVolo stato = StatoVolo.valueOf(rs.getString("stato"));
        String tipoVolo = rs.getString("tipo_volo");

        if (tipoVolo.equals("PARTENZA")) {
            Integer numeroGate = rs.getObject("numero_gate", Integer.class);
            Gate gate = numeroGate != null ? new Gate(numeroGate) : null;
            return new VoloPartenza(codice, compagniaAerea, aeroportoDestinazione,
                    data, orario, java.time.Duration.ofMinutes(ritardo), stato, gate);
        } else {
            return new VoloArrivo(codice, compagniaAerea, aeroportoOrigine,
                    data, orario, java.time.Duration.ofMinutes(ritardo), stato);
        }
    }
}