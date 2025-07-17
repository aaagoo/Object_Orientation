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

    private static final String COLUMN_NOME_UTENTE = "nomeutente";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_COGNOME = "cognome";
    private static final String COLUMN_NOME = "nome";
    private static final String COLUMN_ID = "id";

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
                utente.put(COLUMN_ID, rs.getInt(COLUMN_ID));
                utente.put(COLUMN_NOME_UTENTE, rs.getString(COLUMN_NOME_UTENTE));
                utente.put(COLUMN_PASSWORD, rs.getString(COLUMN_PASSWORD));
                utente.put(COLUMN_NOME, rs.getString(COLUMN_NOME));
                utente.put(COLUMN_COGNOME, rs.getString(COLUMN_COGNOME));
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
                admin.put(COLUMN_ID, rs.getInt(COLUMN_ID));
                admin.put(COLUMN_NOME_UTENTE, rs.getString(COLUMN_NOME_UTENTE));
                admin.put(COLUMN_PASSWORD, rs.getString(COLUMN_PASSWORD));
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
    public void modificaUtente(String nomeutente, String nuovaPassword, String nuovoNome, String nuovoCognome) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall("CALL modifica_utente(?, ?, ?, ?)")) {

            stmt.setString(1, nomeutente);      // nomeutente
            stmt.setString(2, nuovaPassword);   // nuova password
            stmt.setString(3, nuovoNome);       // nuovo nome
            stmt.setString(4, nuovoCognome);    // nuovo cognome

            stmt.execute();
        }
    }

    @Override
    public void modificaAdmin(String nomeutente, String nuovaPassword) throws SQLException {
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             CallableStatement stmt = conn.prepareCall("CALL modifica_admin(?, ?)")) {

            stmt.setString(1, nomeutente);
            stmt.setString(2, nuovaPassword);
            stmt.execute();
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
                    String sqlDati = "SELECT " + COLUMN_NOME + ", " + COLUMN_COGNOME +
                            " FROM tutti_utenti WHERE " + COLUMN_NOME_UTENTE + " = ?";
                    try (PreparedStatement stmtDati = conn.prepareStatement(sqlDati)) {
                        stmtDati.setString(1, nomeutente);
                        ResultSet rsDati = stmtDati.executeQuery();

                        if (rsDati.next()) {
                            return new UtenteGenerico(
                                    rsDati.getString(COLUMN_NOME),
                                    rsDati.getString(COLUMN_COGNOME),
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