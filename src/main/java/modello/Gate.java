package modello;

public class Gate {
    private int numeroGate;

    public Gate(int numeroGate) {
        this.numeroGate = numeroGate;
    }

    public int getNumeroGate() {
        return numeroGate;
    }

    public void setNumeroGate(int numeroGate) {
        this.numeroGate = numeroGate;
    }

    @Override
    public String toString() {
        return "Gate: " + numeroGate;
    }
}