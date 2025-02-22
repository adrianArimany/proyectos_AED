package com.example.dataManager;

public class Transition {
    public int[] acceptableSymbol;
    public int nextState;
    public boolean finalState;

    public Transition(int[] acceptableSymbol, int nextState, boolean finalState) {
        this.acceptableSymbol = acceptableSymbol;
        this.nextState = nextState;
        this.finalState = finalState;
    }
}
