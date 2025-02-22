package com.example.dataManager;

import java.util.HashMap;

public class FSM {
    // state (accepableSymbol nextState finalState)
    HashMap<Integer, Transition> fsm;
    public String tokenName;
    private int currentState = 0;
    private boolean isFinalState = false;
    
    public FSM(HashMap<Integer, Transition> fsm, String tokenName) {
        this.fsm = fsm;
        this.tokenName = tokenName;
    }

    public void next(String symbol) {
        // if currentState and symbol are match then go to the next state
        Transition transition = this.fsm.get(this.currentState);

        if (transition == null) {
            return;
        }

        if (transition.acceptableSymbol.contains(symbol)) {
            this.currentState = transition.nextState;
            this.isFinalState = transition.finalState;
        } else {
            this.currentState = -1;
            this.isFinalState = false;
        }
        
    }

}