package org.Aeroporto;

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

    public String toString() {
        return "Gate{" +
                "numeroGate=" + numeroGate +
                '}';
    }
}