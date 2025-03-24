package com.example.dataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.utils.TokenType;

public class FSM {
    // Use a Map from state number to a List of Transitions.
    private final Map<Integer, List<Transition>> transitions;
    public TokenType tokenName;
    private int currentState;
    private boolean isFinalState;
    
    // Constructor for single-transition-per-state FSM (legacy)
    public FSM(HashMap<Integer, Transition> fsm, TokenType tokenName) {
        // Convert to a Map with single element lists.
        this.transitions = new HashMap<>();
        for (Map.Entry<Integer, Transition> entry : fsm.entrySet()) {
            this.transitions.put(entry.getKey(), List.of(entry.getValue()));
        }
        this.tokenName = tokenName;
        reset();
    }

    // Constructor for FSM with multiple transitions per state.
    public FSM(Map<Integer, List<Transition>> fsm, TokenType tokenName) {
        this.transitions = new HashMap<>(fsm);
        this.tokenName = tokenName;
        reset();
    }
    
    public final void reset(){
        currentState = 0;
        isFinalState = false;
    }
    
    /**
     * Processes the given symbol. Iterates over all transitions for the current state.
     * If any transition accepts the symbol, update the state accordingly.
     * Otherwise, set currentState to -1 (error state) and isFinalState to false.
     */
    public void next(int symbol) {
        List<Transition> possibleTransitions = transitions.get(currentState);
        if (possibleTransitions == null) {
            currentState = -1;
            isFinalState = false;
            return;
        }
        boolean foundMatch = false;
        // Check all transitions for the current state.
        for (Transition transition : possibleTransitions) {
            for (int acceptableSymbol : transition.acceptableSymbol) {
                if (acceptableSymbol == symbol) {
                    // If we match, update the state and final flag, then return.
                    currentState = transition.nextState;
                    isFinalState = transition.finalState;
                    foundMatch = true;
                    break;
                }
            }
            if(foundMatch) break;
        }
        if (!foundMatch) {
            currentState = -1;
            isFinalState = false;
        }
    }
    
    public boolean isFinalState() {
        return this.isFinalState;
    }
    
    public int getCurrentState() {
        return this.currentState;
    }
}
