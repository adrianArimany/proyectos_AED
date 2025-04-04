package com.example.dataManager;

import com.example.utils.TokenType;

/**
 * Manages the Tokens from LISP TO JAVA
 * 
 */
public class Token {
    private final TokenType type; //type of token (goto src/main/java/com/example/utils/TokenType.java)
    private final String lexeme; //lexeme := Actual Text of the input..
    private int line; //Line number where the token is found, this will be used for error messages
    private int column; //Column number where the token is found, this will be used for error messages


    /**
     * Constructor a new Token with given type, lexeme, line, coloumn 
     * @param lexeme : Actual Text
     * @param line : the line that the token is
     * @param column : the coloumn where the token is
     * @param type : the type of the token (again check the src/main/java/com/example/utils/TokenType.java)
     * 
     * Remember that a Token is a tuple that has two values: type and lexeme, so what we have here is a sort of matrix that consists of tokens.
     */
    public Token(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    /**
     * 
     * @param type : actual text
     * @param lexeme : the type of the token 
     */
    public Token(TokenType type, String lexeme){
        this.type = type;
        this.lexeme = lexeme;
    }

    /**
     * 
     * @return the type of the token
     */
    public TokenType getTokenType() {
        return type;
    }
    /**
     * @return the lexeme of the token (the actual text of the token)
     */
    public String getLexeme() {
        return lexeme;
    }
    /**
     * 
     * @return the line number that the token starts at
     */
    public int getLine() {
        return line;
    }
    /**
     * @return the column number where the token is found
     */
    public int getColumn() {
        return column;
    }

    /**
     * 
     * @return the string representation of the token
     */
    @Override
    public String toString() {
        return String.format("Token{type=%s, lexeme='%s'}", 
                             type, lexeme);
    }
}
