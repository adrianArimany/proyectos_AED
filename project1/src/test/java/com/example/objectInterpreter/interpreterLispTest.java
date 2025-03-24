package com.example.objectInterpreter;

import com.example.dataManager.Token;
import org.junit.Test;
import com.example.dataManager.Scanner;


import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class interpreterLispTest {
    @Test
    public void testInterpreterLispSuma() throws Exception {
        String line = "( + 5 3 )";
        Scanner scanner = new Scanner();
        ArrayList<Token> tokens = scanner.runLine(line);
        interpreterLisp interprete = new interpreterLisp(tokens);
        assertEquals(8.0, interprete.evalExpression());
    }

    @Test
    public void testInterpreterLispMult() throws Exception {
        String line = "( * 5 3 )";
        Scanner scanner = new Scanner();
        ArrayList<Token> tokens = scanner.runLine(line);
        interpreterLisp interprete = new interpreterLisp(tokens);
        assertEquals(15.0, interprete.evalExpression());
    }

    @Test
    public void testInterpreterLispDiv() throws Exception {
        String line = "( / 15 5 )";
        Scanner scanner = new Scanner();
        ArrayList<Token> tokens = scanner.runLine(line);
        interpreterLisp interprete = new interpreterLisp(tokens);
        assertEquals(3.0, interprete.evalExpression());
    }

    @Test
    public void testInterpreterLispDiv2() throws Exception {
        String line = "( / 5 )";
        Scanner scanner = new Scanner();
        ArrayList<Token> tokens = scanner.runLine(line);
        interpreterLisp interprete = new interpreterLisp(tokens);
        assertEquals(0.2, interprete.evalExpression());
    }

    @Test
    public void testInterpreterLispSub() throws Exception {
        String line = "( - 5 2 )";
        Scanner scanner = new Scanner();
        ArrayList<Token> tokens = scanner.runLine(line);
        interpreterLisp interprete = new interpreterLisp(tokens);
        assertEquals(3.0, interprete.evalExpression());
    }

    @Test
    public void testInterpreterLispPrecedence() throws Exception {
        String line = "( * ( - 5 2 ) 2 )";
        Scanner scanner = new Scanner();
        ArrayList<Token> tokens = scanner.runLine(line);
        interpreterLisp interprete = new interpreterLisp(tokens);
        assertEquals(6.0, interprete.evalExpression());
    }

    @Test
    public void TestSetQ() throws Exception {
        String line = "( SETQ X 5 )";
        Scanner scanner = new Scanner();
        ArrayList<Token> tokens = scanner.runLine(line);
        interpreterLisp interprete = new interpreterLisp(tokens);
        assertEquals(5.0, interprete.evalExpression());
    }

    @Test
    public void testDefun() throws Exception {
        String line = "( DEFUN SUMA ( X Y ) ( + X Y ) )";
        Scanner scanner = new Scanner();
        ArrayList<Token> tokens = scanner.runLine(line);
        HashMap<String, Object> cacheplaceholder = new HashMap<>();
        interpreterLisp interprete = new interpreterLisp(tokens, cacheplaceholder);
        assertEquals("Function SUMA defined.", interprete.evalExpression());
    }
}