package com.example.dataManager;

import com.example.utils.TokenType;

/**
 * Manages the Tokens from LISP TO JAVA
 * 
 */
public class Token {
    private TokenType type; //type of token (goto src/main/java/com/example/utils/TokenType.java)
    private String lexeme; //lexeme := Actual Text of the input..
    private int line; //Line number where the token is found, this will be used for error messages
    private int column; //Column number where the token is found, this will be used for error messages

    public Token(TokenType tokenType, String lexeme, int line, int column, TokenType type) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    public TokenType getTokenType() {
        return type;
    }
    public String getLexeme() {
        return lexeme;
    }
    public int getLine() {
        return line;
    }
    public int getColumn() {
        return column;
    }


    //Only for debugging and logging purposes
    @Override
    public String toString() {
        return String.format("Token{type=%s, lexeme='%s', line=%d, column=%d}", 
                             type, lexeme, line, column);
    }


    




}
