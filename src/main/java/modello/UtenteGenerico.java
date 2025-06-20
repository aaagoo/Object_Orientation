package modello;


public class UtenteGenerico extends Utente {
    private String nome;
    private String cognome;

    public UtenteGenerico(String nome, String cognome, String nomeUtente, String password) {
        super(nomeUtente, password);
        this.nome = nome;
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public static boolean registraNuovo(String nome, String cognome, String username, String password) {
        UtenteGenerico nuovoUtente = new UtenteGenerico(nome, cognome, username, password);
        return nuovoUtente.registra();
    }
}