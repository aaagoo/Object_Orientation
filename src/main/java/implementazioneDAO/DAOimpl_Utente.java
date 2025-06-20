package implementazioneDAO;

import dao.DAO_Utente;
import database.ConnessioneDatabase;
import modello.Utente;
import modello.UtenteGenerico;
import modello.AmministratoreSistema;

import java.sql.*;


public class DAOimpl_Utente implements DAO_Utente {

    private static DAOimpl_Utente instance;

    private DAOimpl_Utente() {
        try {
            inizializzaTabelle();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'inizializzazione delle tabelle: " + e.getMessage());
        }
    }

    public static synchronized DAOimpl_Utente getInstance() {
        if (instance == null) {
            instance = new DAOimpl_Utente();
        }
        return instance;
    }

    @Override
    public void inizializzaTabelle() throws SQLException {
        Connection conn = ConnessioneDatabase.getInstance().connection;

        String createAccountTable = """
                                           CREATE TABLE IF NOT EXISTS account (
                                               id SERIAL PRIMARY KEY,
                                               nomeutente VARCHAR(50) UNIQUE NOT NULL,
                                               password VARCHAR(100) NOT NULL,
                                               tipo VARCHAR(20) NOT NULL
                                           )""";

        String createUtenteTable = """
                                           CREATE TABLE IF NOT EXISTS utente (
                                               id INTEGER PRIMARY KEY REFERENCES account(id),
                                               nome VARCHAR(50) NOT NULL,
                                               cognome VARCHAR(50) NOT NULL
                                           )""";

        String createAmministratoreTable = """
                                           CREATE TABLE IF NOT EXISTS amministratore (
                                               id INTEGER PRIMARY KEY REFERENCES account(id)
                                           )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createAccountTable);
            stmt.execute(createUtenteTable);
            stmt.execute(createAmministratoreTable);
        }
    }

    @Override
    public boolean registraUtenteGenerico(UtenteGenerico utente) throws SQLException {
        Connection conn = ConnessioneDatabase.getInstance().connection;
        conn.setAutoCommit(false);

        try {
            int newId = inserisciAccount(conn, utente.getNomeUtente(), utente.getPassword(), "UTENTE");

            String sql = "INSERT INTO utente (id, nome, cognome) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newId);
                stmt.setString(2, utente.getNome());
                stmt.setString(3, utente.getCognome());
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public boolean registraAmministratore(AmministratoreSistema admin) throws SQLException {
        Connection conn = ConnessioneDatabase.getInstance().connection;
        conn.setAutoCommit(false);

        try {
            int newId = inserisciAccount(conn, admin.getNomeUtente(), admin.getPassword(), "ADMIN");

            String sql = "INSERT INTO amministratore (id) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private int inserisciAccount(Connection conn, String nomeutente, String password, String tipo)
            throws SQLException {
        String sql = "INSERT INTO account (nomeutente, password, tipo) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeutente);
            stmt.setString(2, password);
            stmt.setString(3, tipo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Errore nella generazione dell'ID");
        }

    }

    @Override
    public Utente verificaCredenziali(String nomeutente, String password) throws SQLException {
        String sql = "SELECT id, tipo FROM account WHERE nomeutente = ? AND password = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeutente);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String tipo = rs.getString("tipo");

                    if ("ADMIN".equals(tipo)) {
                        return new AmministratoreSistema(nomeutente, password);
                    } else {
                        return caricaDatiUtenteGenerico(conn, id, nomeutente, password);
                    }
                }
                return null; // Credenziali non valide
            }
        }
    }

    private UtenteGenerico caricaDatiUtenteGenerico(Connection conn, int id, String nomeutente, String password)
            throws SQLException {
        String sql = "SELECT nome, cognome FROM utente WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UtenteGenerico(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            nomeutente,
                            password
                    );
                }
                return null;
            }
        }
    }
}

