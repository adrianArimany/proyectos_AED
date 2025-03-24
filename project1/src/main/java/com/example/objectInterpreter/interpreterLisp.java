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
