package com.example.utils;

/**
 * Some of the tokens used in the lexer
 * 
 */


public enum TokenType {
 PARENTESIS, // ( )
 
 OPERANDARITHMETIC, // + - * / %
 
 CONDITIONALS, // < > = 
 EQUALITY, // EQ, EQL, EQUAL
 NUMBER, // 0, 1,2,3,4,5,6,7,8,9

 BOOLEAN, // true, false

 STRING, // The comments in the code

 IDENTIFIER, //a,b,c,d,e,f,g,.....,z

 EOF, // End of file
 ERROR, // Error token (can be when nothing is detected or an unexpected token is detected)



 FUN, // Pa los defun y setq
}
