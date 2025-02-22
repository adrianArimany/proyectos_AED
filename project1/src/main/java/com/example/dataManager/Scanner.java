package com.example.dataManager;
//import com.example.dataManager.Transition;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.utils.LoggerManager;
import com.example.utils.TokenType;
/**
 * 
 * 
 * Here is where all the finite state machines run also known as a Scanner.
 * 
 */
public class Scanner {
   /**
 * 
 * Instance <- finite state machines {one for each type of token}
 * 
 * Call one line from the file
 *  
 * For each character k advance the state machine (starting from state q_0)
 * 
 * Then you read a seperator " " identify which finite state machine is in the final state machine "q_f"
 * 
 * Add the token to the tokens list
 * 
 * Once the token list is read though, returns the tokensList
 */ 
    public ArrayList<FSM> finiteStateMachines = new ArrayList<>();
    private static final String CATEGORY = "scanner";

    public Scanner(){
      this.setUpFiniteStateMachines();
    }
    // <--------------------------------(Finite state machines)-------------------------------->
    private void setUpFiniteStateMachines(){
      HashMap<Integer, Transition> parenteses = new HashMap<>();
      parenteses.put(0, new Transition(new int[] {
        (int) '(',
        (int) ')',
      },
      1,
      true
      ));
      this.finiteStateMachines.add(new FSM(parenteses, TokenType.PARENTESIS)); //the issue: "(FSM(object), token)" (not good), since is a map then the object has to be in the same map with the token name  (FSM(object, token))

      HashMap<Integer, Transition> operationsArithmetic = new HashMap<>();
      operationsArithmetic.put(0, new Transition(new int[] {
        (int) '+',
        (int) '-',
        (int) '*',
        (int) '/',
      },
      1,
      true
      ));
      this.finiteStateMachines.add(new FSM(operationsArithmetic, TokenType.OPERANDARITHMETIC));

      HashMap<Integer, Transition> integers = new HashMap<>();
      integers.put(
        0, new Transition(new int[] {
        (int) '1',
        (int) '2',
        (int) '3',
        (int) '4',
        (int) '5',
        (int) '6',
        (int) '7',
        (int) '8',
        (int) '9', }, 1, true)
      );

      integers.put(1, new Transition(new int[] {
        (int) '0',
        (int) '1',
        (int) '2',
        (int) '3',
        (int) '4',
        (int) '5',
        (int) '6',
        (int) '7',
        (int) '8',
        (int) '9',
      }, 1, true));
      
      this.finiteStateMachines.add(new FSM(integers, TokenType.NUMBER));
  } 
  // <--------------------------------(Finite state machines)-------------------------------->

  public ArrayList<Token> runLine(String line)  {
    ArrayList<Token> tokens = new ArrayList<>();
    int coloumnChar = 0;

    for(int i = 0; i < line.length(); i++){
      // Read char and make it a number
      int lineChar = (int) line.charAt(i);

      for (FSM finiteState : this.finiteStateMachines) {
        if (lineChar == ' ') {
          boolean atLeastOneFinished = false;
          // check if any of the finite state machines are in the final state
          for (FSM fst : this.finiteStateMachines) {
           if (fst.isFinalState())  {
            tokens.add(new Token(fst.tokenName, line.substring(coloumnChar, i)));
            coloumnChar = i;
            atLeastOneFinished = true;
            break;
           } 
          }
          // if no finiteState was found it means there is an error (grammatical error)
          if (atLeastOneFinished == false) {
            LoggerManager.logSevere("Scanner", "Grammatical error: " + line.substring(coloumnChar, i)); 
          }
          resetFiniteStateMachines();
        }

        finiteState.next(lineChar);
      }
      
    }
    return tokens;
  }

  public void resetFiniteStateMachines(){
    for (FSM finiteState : this.finiteStateMachines) {
      finiteState.reset();
    }
  }


  //(+ 1 2)
// ( '(', PARENETES)
// ( '+', SUM)
// ( '1', INTEGER)
// ( '2', INTEGER)
// ( ')', PARENETES)


}
