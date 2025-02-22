package com.example.dataManager;

import java.util.HashMap;

public class FSM {
    // state (accepableSymbol nextState finalState)
    public HashMap<Integer, Transition> fsm;
    public String tokenName;
    private int currentState;
    private boolean isFinalState;
    
    public FSM(HashMap<Integer, Transition> fsm, String tokenName) {
        this.fsm = fsm;
        this.tokenName = tokenName;
        reset();
    }
    
    private void reset(){
        currentState = 0;
        isFinalState = false;
    }
    
    public void next(int symbol) {
        Transition transition = this.fsm.get(this.currentState);
        if (transition == null) {
            // THe case where the transition is unidentifiable, then go to the error state
            currentState = -1;
            isFinalState = false;
            return; 
        }
        boolean foundMatch = false;
        //Checking for any symbol matches
        for (int acceptableSymbol : transition.acceptableSymbol) {
            if (acceptableSymbol == symbol) {
                this.currentState = transition.nextState;
                this.isFinalState = transition.finalState;
                foundMatch = true;
                break;
            }
        }
        if (!foundMatch) {
            currentState = -1;
            isFinalState = false;
        }
    
    }


    public boolean isFinalState() {
        return this.isFinalState;
    }

}

/**
 * 
 * @TOFIX:
 * Problem: In the loop, if the very first acceptable symbol does not match the input symbol, it immediately sets the current state to an error state (–1) without checking any subsequent acceptable symbols.
 * Also, the use of isFinalState() inside the loop is just retrieving the flag—it doesn’t reset or adjust the state.
 * 
 * 
 * 
 */
// public class FSM {
//     // state (accepableSymbol nextState finalState)
//     public HashMap<Integer, Transition> fsm;
//     public String tokenName;
//     private int currentState = 0;
//     private boolean isFinalState = false;
    
//     public FSM(HashMap<Integer, Transition> fsm, String tokenName) {
//         this.fsm = fsm;
//         this.tokenName = tokenName;
//     }

//     public void next(String symbol) {
//         // if currentState and symbol are match then go to the next state
//         Transition transition = this.fsm.get(this.currentState);

//         if (transition == null) {
//             return;
//         }


        
//         int symbolValue = symbol.charAt(0); // get the ASCII value of the symbol
//         for (int acceptableSymbol : transition.acceptableSymbol) {
//             if (acceptableSymbol == symbolValue) {
//                 this.currentState = transition.nextState;
//                 this.isFinalState = transition.finalState;
//                 return;
//             } else {
//                 this.currentState = -1;
//                 isFinalState();
//             }
//         }
    
//         // if the symbol is not found, set the currentState to -1 and isFinalState to false
//         this.currentState = -1;
//         isFinalState();


//         //In Java, arrays do not have a contains() method. Instead, you need to manually iterate over the array to check if a certain value exists. (the code above is the fixeded version)
//         // if (transition.acceptableSymbol.contains(symbol)) {
//         //     this.currentState = transition.nextState;
//         //     this.isFinalState = transition.finalState;
//         // } else {
//         //     this.currentState = -1;
//         //     this.isFinalState = false;
//         // }
        
//     }

//     public boolean isFinalState() {
//         return this.isFinalState;
//     }
// }