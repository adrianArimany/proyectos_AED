package com.example.objectInterpreter;

public interface Iinterpreter {
    Object evaluate() throws Exception; // evaluate the string of tokens
    Object evalExpression() throws Exception; // evaluate the expression from the tokens
}
