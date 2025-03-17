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
 * @TODO:
 * 1. Implement the recursive expression in the Scanner. 
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
   * Then you read a seperator " " identify which finite state machine is in the
   * final state machine "q_f"
   * 
   * Add the token to the tokens list
   * 
   * Once the token list is read though, returns the tokensList
   */
  public ArrayList<FSM> finiteStateMachines = new ArrayList<>();
  private static final String CATEGORY = "scanner";

  public Scanner() {
    this.setUpFiniteStateMachines();
  }

  // <--------------------------------(Finite statemachines)-------------------------------->
  private void setUpFiniteStateMachines() {
    HashMap<Integer, Transition> operationsArithmetic = new HashMap<>();
    operationsArithmetic.put(0, new Transition(new int[] {
        (int) '+',
        (int) '-',
        (int) '*',
        (int) '/',
    },
        1,
        true));
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
            (int) '9', }, 1, true));

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

    //The problem with this machine is that it doesn't considers capital letter, so words like poTato might  appear, and it would ignore the T.
    
    // //Remember that lisp takes all letters as capital when they are compiled.
    // HashMap<Integer, Transition> indentifier = new HashMap<>();
    // indentifier.put(0, new Transition(new int[] {
    //     (int) 'a',
    //     (int) 'b',
    //     (int) 'c',
    //     (int) 'd',
    //     (int) 'e',
    //     (int) 'f',
    //     (int) 'g',
    //     (int) 'h',
    //     (int) 'i',
    //     (int) 'j',
    //     (int) 'k',
    //     (int) 'l',
    //     (int) 'm',
    //     (int) 'n',
    //     (int) 'o',
    //     (int) 'p',
    //     (int) 'q',
    //     (int) 'r',
    //     (int) 's',
    //     (int) 't',        
    //     (int) 'u',
    //     (int) 'v',
    //     (int) 'w',
    //     (int) 'x',
    //     (int) 'y',
    //     (int) 'z',
    // }, 1, true));
    // this.finiteStateMachines.add(new FSM(indentifier, TokenType.IDENTIFIER));
    // The following a more sophisticated version of the identifier machine, which handles capital letters.
    //  Assited by CHATGPT (2025).
    HashMap<Integer, Transition> identifier = new HashMap<>();
      int[] allowedChars = new int[52];
        for (int i = 0; i < 26; i++) {
          allowedChars[i] = (int) ('a' + i);
          allowedChars[i + 26] = (int) ('A' + i);
    }
      // State 0: On reading the first letter, transition to state 1.
    identifier.put(0, new Transition(allowedChars, 1, true));
    // State 1: For each subsequent letter, stay in state 1.
    identifier.put(1, new Transition(allowedChars, 1, true));
  this.finiteStateMachines.add(new FSM(identifier, TokenType.IDENTIFIER));


    HashMap<Integer, Transition> conditionals = new HashMap<>();
    // In state 0, accept any of '<', '>', or '=' and transition to state 1 (final state).
    //Recall LISP doesn't recognize == or != as conditionals.
    //For the conditionals that are EQ, EQL, EQUAL, these will be included in the isReserved method. 
    //(note these are not conditionals but functions and should be consider EQUALITY) 
    // State 0: Accept initial characters for comparison operators.
    conditionals.put(0, new Transition(new int[] {
      (int) '<',
      (int) '>',
      (int) '='
    }, 1, true));

    // State 1: If the operator started with '<' or '>', optionally accept '=' to form '<=' or '>='.
    // It will consider ==, but is not USED in lisp, so is not expected to be used.
    conditionals.put(1, new Transition(new int[] {
      (int) '='
    }, 2, true));
    this.finiteStateMachines.add(new FSM(conditionals, TokenType.CONDITIONALS));

    HashMap<Integer, Transition> inequality = new HashMap<>();
    // State 0: Accept '/'
    inequality.put(0, new Transition(new int[] { (int) '/' }, 1, false));
    // State 1: Accept '=' to form '/='
    inequality.put(1, new Transition(new int[] { (int) '=' }, 2, true));
    this.finiteStateMachines.add(new FSM(inequality, TokenType.CONDITIONALS));
  }
  // <--------------------------------(Finite statemachines)-------------------------------->

  /**
   * The following code is the original and has an error that doesn't detect properly the spaces between char.
   */
  // public ArrayList<Token> runLine(String line) {
  //   ArrayList<Token> tokens = new ArrayList<>();
  //   int coloumnChar = 0;

  //   for (int i = 0; i < line.length(); i++) {
  //     int lineChar = (int) line.charAt(i); // Read char and make it a number
       
  //     if (lineChar == (int) '(') {
  //       tokens.add(new Token(TokenType.PARENTESIS, "("));
  //       coloumnChar = i;
  //     } else {
  //       for (FSM finiteState : this.finiteStateMachines) {
  //         if (lineChar == ' ' || lineChar == ')') {
  //           boolean atLeastOneFinished = false;
  //           // check if any of the finite state machines are in the final state
  //           for (FSM fst : this.finiteStateMachines) {
  //             if (fst.isFinalState()) {
  //               tokens.add(new Token(fst.tokenName, line.substring(coloumnChar, i)));
  //               coloumnChar = i;
  //               atLeastOneFinished = true;
  //               break;
  //             }
  //           }
  //           // if no finiteState was found it means there is an error (grammatical error)
  //           if (atLeastOneFinished == false) {
  //             //LoggerManager.logSevere(CATEGORY, "Grammatical error: " + line.substring(coloumnChar, i));
  //           }
  //           if (lineChar == ')') {
  //             tokens.add(new Token(TokenType.PARENTESIS, ")"));
  //             coloumnChar += 1;
  //             i++;
  //           }
  //           resetFiniteStateMachines();
  //         }

  //         finiteState.next(lineChar);
  //       }
  //     }
  //   }
  //   return tokens;
  // }

  // public void resetFiniteStateMachines() {
  //   for (FSM finiteState : this.finiteStateMachines) {
  //     finiteState.reset();
  //   }
  // }

  /**
   * Revised runLine method:
   * - Uses tokenStart to mark the beginning of a token.
   * - When a delimiter (space or parenthesis) is encountered, it extracts the candidate
   *   (trimming any extra whitespace) and processes it via processCandidate().
   * - Delimiter characters are handled separately (parentheses become their own tokens).
   * 
   * Assisted by OPENAI (2025):
   */
  public ArrayList<Token> runLine(String line) {
    ArrayList<Token> tokens = new ArrayList<>();
    int tokenStart = 0;

    for (int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);
//uses the function defun or setq. 
        
      // When we hit a delimiter, process the token from tokenStart to i.
      if (ch == ' ' || ch == '(' || ch == ')') {
        if (i > tokenStart) {
          String candidate = line.substring(tokenStart, i).trim();
          if (!candidate.isEmpty()) {
            tokens.add(processCandidate(candidate));
          }
        }
        // If the delimiter is a parenthesis, add it as its own token.
        if (ch == '(') {
          tokens.add(new Token(TokenType.PARENTESIS, "("));
        } else if (ch == ')') {
          tokens.add(new Token(TokenType.PARENTESIS, ")"));
        }
        
        // Set tokenStart to the character after the delimiter.
        tokenStart = i + 1;
      }
    }

    // Process any remaining candidate token after the last delimiter.
    if (tokenStart < line.length()) {
      String candidate = line.substring(tokenStart).trim();
      if (!candidate.isEmpty()) {
        tokens.add(processCandidate(candidate));
      }
    }

    return tokens;
  }

  /**
   * Feeds a candidate string to each FSM.
   * If an FSM reaches a final state after processing all characters,
   * returns a token using that FSM’s token type.
   * Otherwise, returns an error token.
   */
  private Token processCandidate(String candidate) {
    
    //Special Token:
    //Token DEFUN to create functions
    Token reserved = isReserved(candidate);
    if (reserved != null) {
      return reserved;
    }
    
    
    
    for (FSM fsm : finiteStateMachines) {
      fsm.reset();
      for (char c : candidate.toCharArray()) {
        fsm.next((int)c);
      }
      if (fsm.isFinalState()) {
        return new Token(fsm.tokenName, candidate);
      }
    }
    LoggerManager.logWarning(CATEGORY, "Grammatical error: unrecognized token \"" + candidate + "\"");
    return new Token(TokenType.ERROR, candidate);
  }
  


  /**
   * Checks for any Reserved or special case in the scanner.
   * Currently the reserved is for DEFUN and SETQ
   * 
   * 
   * @param candidate
   * @return
   */
  private Token isReserved(String candidate) {
    if (candidate == null) {
        return null;
    }
    String normalized = candidate.trim().toUpperCase();
    // Reserved tokens for function definitions or assignments
    if (normalized.equals("DEFUN") || normalized.equals("SETQ")) {
        return new Token(TokenType.FUN, candidate);
    }
    // Special equality operators in Lisp
    if (normalized.equals("EQ") || normalized.equals("EQL") || normalized.equals("EQUAL")) {
        return new Token(TokenType.EQUALITY, candidate); // Or TokenType.IDENTIFIER if not special
    }
    return null;
}

  
  }
