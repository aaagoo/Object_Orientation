// DAOimpl_Utente.java
package implementazioneDAO;

import dao.DAO_Utente;
import database.ConnessioneDatabase;
import modello.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public List<UtenteGenerico> getTuttiUtenti() throws SQLException {
        List<UtenteGenerico> utenti = new ArrayList<>();
        String query = BASE_QUERY + " a JOIN public.utente u ON a.id = u.id WHERE a.tipo = 'UTENTE'";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                utenti.add(creaUtenteGenericoDaResultSet(rs));
            }
        }
        return utenti;
    }

    @Override
    public List<AmministratoreSistema> getTuttiAmministratori() throws SQLException {
        List<AmministratoreSistema> amministratori = new ArrayList<>();
        String query = BASE_QUERY + " WHERE tipo = 'ADMIN'";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                amministratori.add(new AmministratoreSistema(
                        rs.getString("nomeutente"),
                        rs.getString("password")
                ));
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
                aggiornaAccount(conn, utente);
                aggiornaDatiUtente(conn, utente);
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
                eliminaAccount(conn, username);
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
        String sql = "UPDATE public.account SET password = ? WHERE nomeutente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utente.getPassword());
            stmt.setString(2, utente.getNomeUtente());
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
}