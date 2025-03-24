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
   * Then you read a seperator " " identify which finite state machine is in the
   * final state machine "q_f"
   * 
   * Add the token to the tokens list
   * 
   * Once the token list is read though, returns the tokensList
   */
  public ArrayList<FSM> finiteStateMachines = new ArrayList<>();
  private static final String CATEGORY = "scanner";
  private HashMap<String,String> FunctionSet = new HashMap<>();
  private HashMap<String, Integer> VariableSet = new HashMap<>();

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
    //Remember that lisp takes all letters as capital when they are compiled.
    HashMap<Integer, Transition> indentifier = new HashMap<>();
    indentifier.put(0, new Transition(new int[] {
        (int) 'a',
        (int) 'b',
        (int) 'c',
        (int) 'd',
        (int) 'e',
        (int) 'f',
        (int) 'g',
        (int) 'h',
        (int) 'i',
        (int) 'j',
        (int) 'k',
        (int) 'l',
        (int) 'm',
        (int) 'n',
        (int) 'o',
        (int) 'p',
        (int) 'q',
        (int) 'r',
        (int) 's',
        (int) 't',        
        (int) 'u',
        (int) 'v',
        (int) 'w',
        (int) 'x',
        (int) 'y',
        (int) 'z',
    }, 1, true));
    
    this.finiteStateMachines.add(new FSM(indentifier, TokenType.IDENTIFIER));

    HashMap<Integer, Transition> conditionals = new HashMap<>();
    conditionals.put(0, new Transition(new int[] {
        (int) '<',
        (int) '>',
        (int) '=',
    }, 1, true));
    this.finiteStateMachines.add(new FSM(conditionals, TokenType.CONDITIONALS));
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
        
        //Check if the TOKEN is DEFUN 
        if (i + 4 < line.length() && line.substring(i, i + 5).toUpperCase().equals("DEFUN")) {
          tokens.add(new Token(TokenType.FUN, "DEFUN"));
          i += 4;
          int j = i;
          String functionname = "";
          while(line.charAt(j) != '('){
            if(!Character.isWhitespace(line.charAt(j))) {
              functionname += line.charAt(j);
            }
            j +=1;
          }
        }


        //Check if the TOKEN is SETQ
        if (i + 3  < line.length() && line.substring(i, i + 4).toUpperCase().equals("SETQ")) {
          tokens.add(new Token(TokenType.FUN, "SETQ"));
          i += 3;
          int j = i +5;
          String variablename = "";
          while(!Character.isWhitespace(line.charAt(j))) {
            variablename += line.charAt(j);
            j +=1;
          }
          j+=1;
          String variableValue = "";
          while(line.charAt(j) != ')') {
            variableValue += line.charAt(j);
          }
          int intvalue = Integer.parseInt(variableValue);
          VariableSet.put(variablename, intvalue);


        }

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
   * Método para acceder al valor de las variables guardadas
   * @return
   */
  public HashMap<String, Integer> getVariableSet() {
    return VariableSet;
  }

  /**
   * Método para acceder a las funciones guardadas
   * @return
   */
  public HashMap<String, String> getFunctionSet() {
    return FunctionSet;
  }

  /**
   * Método para guardar el valor de una variable
   * @param line
   */
  public void HashSetQ(String line){
    for(int i = 0; i < runLine(line).size(); i++){
      if(runLine(line).get(i).getLexeme().equals("SETQ")){
        VariableSet.put(runLine(line).get(i+1).getLexeme(), Integer.parseInt(runLine(line).get(i+2).getLexeme()));
      }
    }
  }

  /**
   * Método para guardar una función
   * @param line
   */
  public void HashDefun(String line){
    for(int i = 0; i < runLine(line).size(); i++){
      if(runLine(line).get(i).getLexeme().equals("DEFUN")){
        int j = i+2;
        String functionargument = "";
        while(j < runLine(line).size()-2){
          functionargument += runLine(line).get(j).getLexeme() + " ";
          j +=1;
        }
        functionargument += runLine(line).get(j).getLexeme();
        FunctionSet.put(runLine(line).get(i+1).getLexeme(), functionargument);
      }
    }
  }

  /**
   * Método para determinar la cantidad de parámetros de una función
   * @param FunctionName
   * @return
   */
  public int getNumberofParameters(String FunctionName){
    String argument = FunctionSet.get(FunctionName);
    int i = 0;
    int numberofparameters = 0;
    while(argument.charAt(i) != ')'){
      if(!Character.isWhitespace(argument.charAt(i)) && argument.charAt(i) != '('){
        numberofparameters++;
      }
      i += 1;
    }
    return numberofparameters;
  }

  /**
   * Método para determinar si ya existe una función
   * @param line
   * @return
   */
  public boolean hasafunction(String line) {
    if(FunctionSet.containsKey(runLine(line).get(1).getLexeme())){
      return true;
    }
    return false;
  }

  /**
   * Método para determinar si existe una función (si es válida) la cual es ingresada en una línea acorde a la cantida de parámetros con los que se ingresa
   * @param line
   * @return
   */
  public boolean ValidateFunction(String line) {
    if(hasafunction(line)){
      String keytofunction = runLine(line).get(1).getLexeme();
      int parametersofcandidate = runLine(line).size() - 3;
      if(parametersofcandidate == getNumberofParameters(keytofunction)){
        return true;
      }
    }
    return false;
  }

  /**
   * Método para obtener cuales son las variables de la función
   * @param keyToFunction
   * @return
   */
  public ArrayList<Character> listOfParameters(String keyToFunction){
    String argument = FunctionSet.get(keyToFunction);
    int i = 0;
    ArrayList<Character> parameters = new ArrayList<>();
    while(argument.charAt(i) != ')') {
      if (!Character.isWhitespace(argument.charAt(i)) && argument.charAt(i) != '(') {
        parameters.add(argument.charAt(i));
      }
      i+=1;
    }

    return parameters;
  }

  /**
   * Método que se utilizará para obtener unicamente el argumento o regla de asignación de una función
   * @param keyToFunction
   * @return
   */
  public String FunctionArgument(String keyToFunction) {
      String argument = FunctionSet.get(keyToFunction);
      int i = 0;
      while(argument.charAt(i) != ')') {
        i+=1;
      }
      return argument.substring(i+2);
  }

  /**
   * Método para colocar los valores ingresados a una función y tokenizar esa nueva cadena
   * @param line
   * @return
   */
  public ArrayList<Token> ValuateInFunction(String line) {

    if(hasafunction(line)){

      ArrayList<Token> tovalue = runLine(line);
      String keyToFunction = tovalue.get(1).getLexeme();
      ArrayList<Token> tokens = runLine(FunctionArgument(keyToFunction));
      tovalue.remove(0);
      tovalue.remove(0);
      tovalue.remove(tovalue.size()-1);

      for(int i = 0; i< tokens.size(); i++){
        for(int j = 0; j < tovalue.size(); j++){
          if(tokens.get(i).getLexeme().equals(String.valueOf(listOfParameters(keyToFunction).get(j)))){
            tokens.set(i,tovalue.get(j));
          }
        }
      }
      return tokens;
    }

    return null;
  }



}
