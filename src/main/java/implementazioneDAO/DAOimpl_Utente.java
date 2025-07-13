package implementazioneDAO;

import dao.DAO_Utente;
import database.ConnessioneDatabase;
import modello.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DAOimpl_Utente implements DAO_Utente {
    private static DAOimpl_Utente instance;

    private DAOimpl_Utente() {
    }

    public static synchronized DAOimpl_Utente getInstance() {
        if (instance == null) {
            instance = new DAOimpl_Utente();
        }
        return instance;
    }

    @Override
    public List<Map<String, Object>> getTuttiUtenti() throws SQLException {
        List<Map<String, Object>> utenti = new ArrayList<>();
        String sql = "SELECT * FROM tutti_utenti";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> utente = new HashMap<>();
                utente.put("nomeutente", rs.getString("nomeutente"));
                utente.put("password", rs.getString("password"));
                utente.put("nome", rs.getString("nome"));
                utente.put("cognome", rs.getString("cognome"));
                utenti.add(utente);
            }
        }
        return utenti;
    }

    @Override
    public List<Map<String, Object>> getTuttiAmministratori() throws SQLException {
        List<Map<String, Object>> amministratori = new ArrayList<>();
        String sql = "SELECT * FROM tutti_admin";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> admin = new HashMap<>();
                admin.put("nomeutente", rs.getString("nomeutente"));
                admin.put("password", rs.getString("password"));
                amministratori.add(admin);
            }
        }
        return amministratori;
    }

    @Override
    public boolean registraUtenteGenerico(UtenteGenerico utente) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            String sql = "CALL crea_account_utente(?, ?, ?, ?)";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, utente.getNomeUtente());
                stmt.setString(2, utente.getPassword());
                stmt.setString(3, utente.getNome());
                stmt.setString(4, utente.getCognome());
                stmt.execute();
                return true;
            }
        }
    }

    @Override
    public boolean registraAmministratore(AmministratoreSistema admin) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            String sql = "CALL crea_account_admin(?, ?)";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, admin.getNomeUtente());
                stmt.setString(2, admin.getPassword());
                stmt.execute();
                return true;
            }
        }
    }

    @Override
    public void modificaUtente(UtenteGenerico utente) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            conn.setAutoCommit(false);
            try {
                String sqlUtente = "UPDATE utente u SET nome = ?, cognome = ? " +
                        "FROM account a WHERE a.id = u.id AND a.nomeutente = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUtente)) {
                    stmt.setString(1, utente.getNome());
                    stmt.setString(2, utente.getCognome());
                    stmt.setString(3, utente.getNomeUtente());
                    stmt.executeUpdate();
                }

                String sqlAccount = "UPDATE account SET password = ? WHERE nomeutente = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlAccount)) {
                    stmt.setString(1, utente.getPassword());
                    stmt.setString(2, utente.getNomeUtente());
                    stmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public void eliminaUtente(String username) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            String sql = "CALL elimina_utente(?)";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, username);
                stmt.execute();
            }
        }
    }

    @Override
    public void eliminaAdmin(String username) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            String sql = "CALL elimina_admin(?)";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, username);
                stmt.execute();
            }
        }
    }

    @Override
    public Utente verificaCredenziali(String nomeutente, String password) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            // Prima verifichiamo se è un amministratore
            String sqlAdmin = "SELECT verifica_admin(?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlAdmin)) {
                stmt.setString(1, nomeutente);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getBoolean(1)) {
                    return new AmministratoreSistema(nomeutente, password);
                }
            }

            // Se non è un amministratore, verifichiamo se è un utente generico
            String sqlUtente = "SELECT verifica_utente(?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUtente)) {
                stmt.setString(1, nomeutente);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getBoolean(1)) {
                    // Se è un utente valido, recuperiamo i suoi dati dalla vista
                    String sqlDati = "SELECT nome, cognome FROM tutti_utenti WHERE nomeutente = ?";
                    try (PreparedStatement stmtDati = conn.prepareStatement(sqlDati)) {
                        stmtDati.setString(1, nomeutente);
                        ResultSet rsDati = stmtDati.executeQuery();

                        if (rsDati.next()) {
                            return new UtenteGenerico(
                                    rsDati.getString("nome"),
                                    rsDati.getString("cognome"),
                                    nomeutente,
                                    password
                            );
                        }
                    }
                }
            }
            // Se non è né amministratore né utente, ritorniamo null
            return null;
        }
    }
}