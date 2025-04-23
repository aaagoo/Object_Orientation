package org.Aeroporto;

import java.util.List;

public class Amministratore_Del_Sistema extends Utente {
    public Amministratore_Del_Sistema(String Nome_Utente, String Password) {
        super(Nome_Utente, Password);
    }

    public List<Prenotazione> Visualizza_Voli() {

        return null;
    }

    public void Nuovo_Volo(Volo volo) {

    }

    public void Aggiorna_Volo(Volo volo) {

    }

    public void Assegna_Gate(Volo Volo, Gate gate) {

    }
}