package Modello;.

public class Gate {
    private int Numero_Gate;

    public Gate(int Numero_Gate) {
        this.Numero_Gate = Numero_Gate;
    }

    public int getNumero_Gate() {
        return Numero_Gate;
    }

    public void setNumero_Gate(int Numero_Gate) {
        this.Numero_Gate = Numero_Gate;
    }

    @Override
    public String toString() {
        return "Gate: " + Numero_Gate;
    }
}