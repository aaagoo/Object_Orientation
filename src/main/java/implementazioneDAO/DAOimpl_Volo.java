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
    public List<Volo> getTuttiVoli() {
        List<Volo> voli = new ArrayList<>();
        String query = "SELECT v.*, vp.numero_gate FROM volo v " +
                "LEFT JOIN volo_partenza vp ON v.codice = vp.codice";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                voli.add(creaVoloDaResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voli;
    }

    @Override
    public List<VoloArrivo> getVoliArrivo() {
        List<VoloArrivo> voliArrivo = new ArrayList<>();
        String query = "SELECT v.* FROM volo v " +
                "INNER JOIN volo_arrivo va ON v.codice = va.codice " +
                "WHERE v.tipo_volo = 'ARRIVO'";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                voliArrivo.add((VoloArrivo) creaVoloDaResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voliArrivo;
    }

    @Override
    public List<VoloPartenza> getVoliPartenza() {
        List<VoloPartenza> voliPartenza = new ArrayList<>();
        String query = "SELECT v.*, vp.numero_gate FROM volo v " +
                "INNER JOIN volo_partenza vp ON v.codice = vp.codice " +
                "WHERE v.tipo_volo = 'PARTENZA'";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                voliPartenza.add((VoloPartenza) creaVoloDaResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voliPartenza;
    }

    @Override
    public Volo cercaPerCodice(String codice) {
        String query = "SELECT v.*, vp.numero_gate FROM volo v " +
                "LEFT JOIN volo_partenza vp ON v.codice = vp.codice " +
                "WHERE v.codice = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codice);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return creaVoloDaResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public VoloPartenza trovaVoloPartenza(String codice) {
        String query = "SELECT v.*, vp.numero_gate FROM volo v " +
                "INNER JOIN volo_partenza vp ON v.codice = vp.codice " +
                "WHERE v.codice = ? AND v.tipo_volo = 'PARTENZA'";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codice);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return (VoloPartenza) creaVoloDaResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void aggiungiVolo(Volo volo) {
        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            String queryVolo = "INSERT INTO volo (codice, compagnia_aerea, aeroporto_origine, " +
                    "aeroporto_destinazione, data, orario, ritardo, stato, tipo_volo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(queryVolo)) {
                stmt.setString(1, volo.getCodice());
                stmt.setString(2, volo.getCompagniaAerea());
                stmt.setString(3, volo.getAeroportoOrigine());
                stmt.setString(4, volo.getAeroportoDestinazione());
                stmt.setDate(5, Date.valueOf(volo.getData()));
                stmt.setTime(6, Time.valueOf(volo.getOrario()));
                stmt.setLong(7, volo.getRitardo());
                stmt.setObject(8, volo.getStato().toString(), Types.OTHER);
                stmt.setString(9, volo instanceof VoloPartenza ? "PARTENZA" : "ARRIVO");
                stmt.executeUpdate();
            }

            if (volo instanceof VoloPartenza) {
                String queryPartenza = "INSERT INTO volo_partenza (codice, numero_gate) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(queryPartenza)) {
                    stmt.setString(1, volo.getCodice());
                    Gate gate = ((VoloPartenza) volo).getGate();
                    stmt.setObject(2, gate != null ? gate.getNumeroGate() : null);
                    stmt.executeUpdate();
                }
            } else {
                String queryArrivo = "INSERT INTO volo_arrivo (codice) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(queryArrivo)) {
                    stmt.setString(1, volo.getCodice());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }

    @Override
    public void modificaVolo(Volo volo) {
        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            String queryVolo = "UPDATE volo SET compagnia_aerea = ?, aeroporto_origine = ?, " +
                    "aeroporto_destinazione = ?, data = ?, orario = ?, ritardo = ?, " +
                    "stato = ? WHERE codice = ?";

            try (PreparedStatement stmt = conn.prepareStatement(queryVolo)) {
                stmt.setString(1, volo.getCompagniaAerea());
                stmt.setString(2, volo.getAeroportoOrigine());
                stmt.setString(3, volo.getAeroportoDestinazione());
                stmt.setDate(4, Date.valueOf(volo.getData()));
                stmt.setTime(5, Time.valueOf(volo.getOrario()));
                stmt.setLong(6, volo.getRitardo());
                stmt.setString(7, volo.getStato().toString());
                stmt.setString(8, volo.getCodice());
                stmt.executeUpdate();
            }

            if (volo instanceof VoloPartenza) {
                String queryPartenza = "UPDATE volo_partenza SET numero_gate = ? WHERE codice = ?";
                try (PreparedStatement stmt = conn.prepareStatement(queryPartenza)) {
                    Gate gate = ((VoloPartenza) volo).getGate();
                    stmt.setObject(1, gate != null ? gate.getNumeroGate() : null);
                    stmt.setString(2, volo.getCodice());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaVolo(String codice) {
        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            // Prima elimina dalle tabelle figlie
            String queryDeletePartenza = "DELETE FROM volo_partenza WHERE codice = ?";
            String queryDeleteArrivo = "DELETE FROM volo_arrivo WHERE codice = ?";
            String queryDeleteVolo = "DELETE FROM volo WHERE codice = ?";

            try (PreparedStatement stmtPartenza = conn.prepareStatement(queryDeletePartenza);
                 PreparedStatement stmtArrivo = conn.prepareStatement(queryDeleteArrivo);
                 PreparedStatement stmtVolo = conn.prepareStatement(queryDeleteVolo)) {

                stmtPartenza.setString(1, codice);
                stmtPartenza.executeUpdate();

                stmtArrivo.setString(1, codice);
                stmtArrivo.executeUpdate();

                stmtVolo.setString(1, codice);
                stmtVolo.executeUpdate();

                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }

    private Volo creaVoloDaResultSet(ResultSet rs) throws SQLException {
        String codice = rs.getString("codice");
        String compagniaAerea = rs.getString("compagnia_aerea");
        String aeroportoOrigine = rs.getString("aeroporto_origine");
        String aeroportoDestinazione = rs.getString("aeroporto_destinazione");
        LocalDate data = rs.getDate("data").toLocalDate();
        LocalTime orario = rs.getTime("orario").toLocalTime();
        StatoVolo stato = StatoVolo.valueOf(rs.getString("stato"));
        String tipoVolo = rs.getString("tipo_volo");

        if (tipoVolo.equals("PARTENZA")) {
            Integer numeroGate = rs.getObject("numero_gate", Integer.class);
            Gate gate = numeroGate != null ? new Gate(numeroGate) : null;
            return new VoloPartenza(codice, compagniaAerea, aeroportoDestinazione,
                    data, orario, java.time.Duration.ZERO, stato, gate);
        } else {
            return new VoloArrivo(codice, compagniaAerea, aeroportoOrigine,
                    data, orario, java.time.Duration.ZERO, stato);
        }
    }
}