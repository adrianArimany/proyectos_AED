package com.example.objectInterpreter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.example.dataManager.Scanner;
import com.example.dataManager.Token;
import com.example.factory.OperationFactory;
import com.example.operations.Operation;
import com.example.operations.OperationFun.DefunOperator;
import com.example.utils.TokenType;

public class interpreterLisp implements  Iinterpreter{
    private Map<String, Object> placeholderCache = new HashMap<>(); // so the code can recognize the parameter in the defun,
    //For simplicity we define $ as our special character in the code.
    private final List<Token> tokens;
    private int current;

    public interpreterLisp(List<Token> tokens, Map<String, Object> placeholderCache) {
        this.tokens = tokens;
        this.current = 0;
        this.placeholderCache = placeholderCache;
    }

    public interpreterLisp(List<Token> tokens) {
        this(tokens, new HashMap<>()); // Creates a new, empty placeholder cache.
    }

    // Starts the evaluation process.
    @Override
    public Object evaluate() throws Exception {
        placeholderCache.clear(); // Clear the cache for each new evaluation.
        if (current >= tokens.size()) {
            throw new Exception("No tokens to evaluate.");
        }
        return evalExpression();
    }

    // Recursively evaluates an expression.
    @Override
    public Object evalExpression() throws Exception {
        Token currentToken = tokens.get(current);
        switch (currentToken.getTokenType()) {
            case LPAREN -> {
                current++; // Skip '('

                if (current >= tokens.size()) {
                    throw new Exception("Expected operator after '('");
                }
                Token operatorToken = tokens.get(current);
                current++;
                String operator = operatorToken.getLexeme().toUpperCase();

                // Special handling for IF.
                if ("IF".equals(operator)) {
                    // Expect: (IF condition then-branch else-branch)
                    Object condition = evalExpression();

                    // Determine boundaries for then-branch and else-branch.
                    int thenStart = current;
                    int thenEnd = findEndOfExpression(thenStart);
                    int elseStart = thenEnd;
                    int elseEnd = findEndOfExpression(elseStart);

                    boolean cond;
                    switch (condition) {
                        case Boolean aBoolean -> cond = aBoolean;
                        case Number number -> cond = number.doubleValue() != 0;
                        default -> cond = condition != null;
                    }

                    Object result;
                    if (cond) {
                        // Evaluate the then branch.
                        List<Token> thenTokens = tokens.subList(thenStart, thenEnd);
                        result = new interpreterLisp(thenTokens, getPlaceholderCache()).evaluate();
                    } else {
                        // Evaluate the else branch.
                        List<Token> elseTokens = tokens.subList(elseStart, elseEnd);
                        result = new interpreterLisp(elseTokens, getPlaceholderCache()).evaluate();
                    }
                    current = elseEnd;
                    if (current >= tokens.size() || tokens.get(current).getTokenType() != TokenType.RPAREN) {
                        throw new Exception("Missing closing parenthesis for IF");
                    }
                    current++; // Skip the closing ')'
                    return result;
                }

                // Special handling for DEFUN.
                if ("DEFUN".equals(operator)) {
                    if (current >= tokens.size()) {
                        throw new Exception("Expected function name after DEFUN");
                    }
                    Token functionNameToken = tokens.get(current);
                    current++;
                    String functionName = functionNameToken.getLexeme().toUpperCase();

                    if (current >= tokens.size() || tokens.get(current).getTokenType() != TokenType.LPAREN) {
                        throw new Exception("Expected parameter list for function " + functionName);
                    }
                    List<Object> parameterList = readLiteralList();
                    String functionBody = readRawExpression();

                    if (current >= tokens.size() || tokens.get(current).getTokenType() != TokenType.RPAREN) {
                        throw new Exception("Missing closing parenthesis for DEFUN");
                    }
                    current++; // Skip the closing ')'

                    Operation defunOperation = OperationFactory.getOperation("DEFUN");
                    if (defunOperation == null) {
                        throw new Exception("Operation for DEFUN not found");
                    }
                    return defunOperation.execute(functionName, parameterList, functionBody);
                } else {
                    List<Object> operands = new ArrayList<>();
                    while (current < tokens.size() && tokens.get(current).getTokenType() != TokenType.RPAREN) {
                        operands.add(evalExpression());
                    }
                    if (current >= tokens.size() || tokens.get(current).getTokenType() != TokenType.RPAREN) {
                        throw new Exception("Missing closing parenthesis");
                    }
                    current++; // Skip ')'
                    Operation operation = OperationFactory.getOperation(operator);
                    if (operation != null) {
                        return operation.execute(operands.toArray());
                    } else if (DefunOperator.functionDefinitions.containsKey(operator)) {
                        Object[] definition = DefunOperator.functionDefinitions.get(operator);
                        List<String> parameterNames = (List<String>) definition[0];
                        Object body = definition[1];

                        if (operands.size() < parameterNames.size()) {
                            for (int i = operands.size(); i < parameterNames.size(); i++) {
                                String paramName = parameterNames.get(i);
                                String input = JOptionPane.showInputDialog("Enter value for parameter " + paramName + ":");
                                if (input == null) {
                                    throw new Exception("User cancelled input for parameter " + paramName);
                                }
                                double value = Double.parseDouble(input);
                                operands.add(value);
                            }
                        }
                        return evaluateFunctionBody((String) body, parameterNames, operands);
                    } else {
                        throw new Exception("Unknown operator: " + operator);
                    }
                }
            }
            case NUMBER -> {
                current++;
                try {
                    return Double.valueOf(currentToken.getLexeme());
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid number format: " + currentToken.getLexeme());
                }
            }
            default -> {
                current++;
                return currentToken.getLexeme();
            }
        }
    }



    @Override
    public Object compile(String code) {
    /**
     * Send the first line to the Scanner to get the token list 
     * 
     * Then create the syntax tree with the token list
     * 
     * Then Run syntax tree (if there is not error)
     * 
     * Then repeat for the rest of the lines (from the file)
     * 
     */
    return null;
    }

}
