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
    private static final String BASE_QUERY = "SELECT * FROM public.account";

    public static synchronized DAOimpl_Utente getInstance() {
        if (instance == null) {
            instance = new DAOimpl_Utente();
        }
        return instance;
    }

    @Override
    public List<Map<String, Object>> getTuttiUtenti() throws SQLException {
        List<Map<String, Object>> utenti = new ArrayList<>();
        String sql = "SELECT a.id, a.nomeutente, a.password, u.nome, u.cognome " +
                "FROM account a " +
                "JOIN utente u ON a.id = u.id " +
                "WHERE a.tipo = 'UTENTE'";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> utente = new HashMap<>();
                utente.put("id", rs.getInt("id"));
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
        String sql = "SELECT a.id, a.nomeutente, a.password " +
                "FROM account a " +
                "WHERE a.tipo = 'ADMIN'";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> amministratore = new HashMap<>();
                amministratore.put("id", rs.getInt("id"));
                amministratore.put("nomeutente", rs.getString("nomeutente"));
                amministratore.put("password", rs.getString("password"));
                amministratori.add(amministratore);
            }
        }
        return amministratori;
    }


    @Override
    public boolean registraUtenteGenerico(UtenteGenerico utente) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            conn.setAutoCommit(false);
            try {
                int newId = inserisciAccount(conn, utente.getNomeUtente(), utente.getPassword(), "UTENTE");
                inserisciDatiUtente(conn, newId, utente);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean registraAmministratore(AmministratoreSistema admin) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            conn.setAutoCommit(false);
            try {
                int newId = inserisciAccount(conn, admin.getNomeUtente(), admin.getPassword(), "ADMIN");
                inserisciDatiAmministratore(conn, newId);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public void modificaUtente(UtenteGenerico utente) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            conn.setAutoCommit(false);
            try {
                // Prima aggiorniamo i dati nella tabella utente
                String sqlUtente = "UPDATE public.utente u SET nome = ?, cognome = ? " +
                        "FROM public.account a WHERE a.id = u.id AND a.nomeutente = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUtente)) {
                    stmt.setString(1, utente.getNome());
                    stmt.setString(2, utente.getCognome());
                    stmt.setString(3, utente.getNomeUtente());
                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated == 0) {
                        throw new SQLException("Nessun utente trovato con il nome utente: " + utente.getNomeUtente());
                    }
                }

                // Poi aggiorniamo la password nell'account
                String sqlAccount = "UPDATE public.account SET password = ? WHERE nomeutente = ?";
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
            conn.setAutoCommit(false);
            try {
                // Prima verifichiamo che l'utente esista
                String sqlCheck = "SELECT COUNT(*) FROM public.account WHERE nomeutente = ? AND tipo = 'UTENTE'";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCheck)) {
                    stmt.setString(1, username);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        throw new SQLException("L'utente '" + username + "' non esiste");
                    }
                }

                // Prima eliminiamo eventuali prenotazioni associate all'utente
                String sqlPrenotazioni = "DELETE FROM public.prenotazione WHERE username_prenotazione = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlPrenotazioni)) {
                    stmt.setString(1, username);
                    stmt.executeUpdate();
                }

                // Poi eliminiamo dalla tabella utente
                String sqlUtente = "DELETE FROM public.utente WHERE id = (SELECT id FROM public.account WHERE nomeutente = ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUtente)) {
                    stmt.setString(1, username);
                    stmt.executeUpdate();
                }

                // Infine eliminiamo dalla tabella account
                String sqlAccount = "DELETE FROM public.account WHERE nomeutente = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlAccount)) {
                    stmt.setString(1, username);
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
    public Utente verificaCredenziali(String nomeutente, String password) throws SQLException {
        String query = BASE_QUERY + " WHERE nomeutente = ? AND password = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nomeutente);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "ADMIN".equals(rs.getString("tipo")) ?
                            new AmministratoreSistema(nomeutente, password) :
                            caricaDatiUtenteGenerico(conn, rs.getInt("id"), nomeutente, password);
                }
            }
        }
        return null;
    }

    private void inserisciDatiUtente(Connection conn, int id, UtenteGenerico utente) throws SQLException {
        String sql = "INSERT INTO public.utente (id, nome, cognome) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, utente.getNome());
            stmt.setString(3, utente.getCognome());
            stmt.executeUpdate();
        }
    }

    private void inserisciDatiAmministratore(Connection conn, int id) throws SQLException {
        String sql = "INSERT INTO public.amministratore (id) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private int inserisciAccount(Connection conn, String nomeutente, String password, String tipo)
            throws SQLException {
        String sql = "INSERT INTO public.account (nomeutente, password, tipo) VALUES (?, ?, ?) RETURNING id";
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

    private void aggiornaAccount(Connection conn, UtenteGenerico utente) throws SQLException {
        String vecchioUsername = utente.getNomeUtente();

        String sql = "UPDATE public.account SET nomeutente = ?, password = ? WHERE nomeutente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utente.getNomeUtente());
            stmt.setString(2, utente.getPassword());
            stmt.setString(3, vecchioUsername);
            stmt.executeUpdate();
        }
    }


    private void aggiornaDatiUtente(Connection conn, UtenteGenerico utente) throws SQLException {
        String sql = "UPDATE public.utente u SET nome = ?, cognome = ? " +
                "FROM public.account a WHERE a.id = u.id AND a.nomeutente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getCognome());
            stmt.setString(3, utente.getNomeUtente());
            stmt.executeUpdate();
        }
    }

    private void eliminaAccount(Connection conn, String username) throws SQLException {
        String sql = "DELETE FROM public.account WHERE nomeutente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    private UtenteGenerico creaUtenteGenericoDaResultSet(ResultSet rs) throws SQLException {
        return new UtenteGenerico(
                rs.getString("nome"),
                rs.getString("cognome"),
                rs.getString("nomeutente"),
                rs.getString("password")
        );
    }

    private UtenteGenerico caricaDatiUtenteGenerico(Connection conn, int id, String nomeutente,
                                                    String password) throws SQLException {
        String sql = "SELECT nome, cognome FROM public.utente WHERE id = ?";
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

    @Override
    public void aggiornaUsername(String vecchioUsername, String nuovoUsername) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection) {
            conn.setAutoCommit(false);
            try {
                // Prima verifichiamo che il vecchio username esista e sia un utente
                String sqlCheckVecchio = "SELECT COUNT(*) FROM public.account WHERE nomeutente = ? AND tipo = 'UTENTE'";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCheckVecchio)) {
                    stmt.setString(1, vecchioUsername);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        throw new SQLException("Utente non trovato: " + vecchioUsername);
                    }
                }

                // Verifichiamo che il nuovo username non esista già
                String sqlCheckNuovo = "SELECT COUNT(*) FROM public.account WHERE nomeutente = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCheckNuovo)) {
                    stmt.setString(1, nuovoUsername);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("Il nome utente '" + nuovoUsername + "' è già in uso");
                    }
                }

                // Aggiorniamo il riferimento nelle prenotazioni
                String sqlPrenotazioni = "UPDATE public.prenotazione SET username_prenotazione = ? " +
                        "WHERE username_prenotazione = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlPrenotazioni)) {
                    stmt.setString(1, nuovoUsername);
                    stmt.setString(2, vecchioUsername);
                    stmt.executeUpdate();
                }

                // Infine aggiorniamo l'account
                String sqlAccount = "UPDATE public.account SET nomeutente = ? WHERE nomeutente = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlAccount)) {
                    stmt.setString(1, nuovoUsername);
                    stmt.setString(2, vecchioUsername);
                    stmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }


}