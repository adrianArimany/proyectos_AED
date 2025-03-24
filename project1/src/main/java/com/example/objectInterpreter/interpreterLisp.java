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
import com.example.operations.operationFun.DefunOperator;
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

    /**
     * Finds the end index (exclusive) of the expression starting at 'start'.
     * If the token at 'start' is LPAREN, returns the index after its matching RPAREN;
     * otherwise, returns start+1.
     */
    private int findEndOfExpression(int start) throws Exception {
        int index = start;
        if (tokens.get(index).getTokenType() == TokenType.LPAREN) {
            int count = 0;
            while (index < tokens.size()) {
                Token t = tokens.get(index);
                if (t.getTokenType() == TokenType.LPAREN) {
                    count++;
                } else if (t.getTokenType() == TokenType.RPAREN) {
                    count--;
                    if (count == 0) {
                        return index + 1;
                    }
                }
                index++;
            }
            throw new Exception("Unbalanced parentheses in expression.");
        } else {
            return start + 1;
        }
    }

    /**
     * Evaluates a function body by substituting parameter names with operand values.
     *
     * @param functionBody The function body as a String.
     * @param parameterNames The list of parameter names.
     * @param operandValues The list of operand values.
     * @return The result of evaluating the substituted function body.
     * @throws Exception if evaluation fails.
     */
    private Object evaluateFunctionBody(String functionBody, List<String> parameterNames, List<Object> operandValues) throws Exception {
        String substitutedBody = functionBody;
        for (int i = 0; i < parameterNames.size(); i++) {
            String param = parameterNames.get(i);
            Object value = operandValues.get(i);
            substitutedBody = substitutedBody.replaceAll("\\b" + param + "\\b", value.toString());
        }

        Scanner scanner = new Scanner();
        List<Token> bodyTokens = scanner.runLine(substitutedBody);
        interpreterLisp bodyInterpreter = new interpreterLisp(bodyTokens, placeholderCache);
        Object result = bodyInterpreter.evaluate();

        return result;
    }

    /**
     * Reads a literal list from the tokens.
     * Assumes the current token is LPAREN and collects all tokens until the matching RPAREN,
     * returning a list of their lexemes (without further evaluation).
     */
    private List<Object> readLiteralList() throws Exception {
        if (tokens.get(current).getTokenType() != TokenType.LPAREN) {
            throw new Exception("Expected '(' at beginning of literal list");
        }
        current++; // Skip '('
        List<Object> literalList = new ArrayList<>();
        while (current < tokens.size() && tokens.get(current).getTokenType() != TokenType.RPAREN) {
            // Instead of evaluating, we simply take the lexeme.
            literalList.add(tokens.get(current).getLexeme());
            current++;
        }
        if (current >= tokens.size() || tokens.get(current).getTokenType() != TokenType.RPAREN) {
            throw new Exception("Missing closing parenthesis in literal list");
        }
        current++; // Skip ')'
        return literalList;
    }

    /**
     * Reads a raw function body from the tokens as a String.
     * Assumes that the function body starts at the current token and goes until the matching RPAREN.
     */
    private String readRawExpression() throws Exception {
        StringBuilder sb = new StringBuilder();
        int parenCount = 0;
        while (current < tokens.size()) {
            Token token = tokens.get(current);
            if (token.getTokenType() == TokenType.LPAREN) {
                parenCount++;
            } else if (token.getTokenType() == TokenType.RPAREN) {
                parenCount--;
            }
            sb.append(token.getLexeme()).append(" ");
            current++;
            if (parenCount == 0) {
                break;
            }
        }
        if (parenCount != 0) {
            throw new Exception("Unmatched parentheses in function body.");
        }
        return sb.toString().trim();
    }

    public Map<String, Object> getPlaceholderCache() {
        return placeholderCache;
    }
    

}
