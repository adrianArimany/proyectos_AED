package com.example.utils;

/**
 * Some of the tokens used in the lexer
 * 
 */


public enum TokenType {
 PARENTESIS,
 
 OPERANDARITHMETIC, // + - * / %
 
 LESS,
 GREATER,
 EQUAL,
 LESS_EQUAL,
 GREATER_EQUAL,
 
 NUMBER,
 STRING,

 IDENTIFIER,

 EOF,
 ERROR,
}
