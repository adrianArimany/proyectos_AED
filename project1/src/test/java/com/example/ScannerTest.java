package com.example;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.dataManager.FSM;
import com.example.dataManager.Scanner;
import com.example.dataManager.Token;
import com.example.utils.TokenType;
public class ScannerTest {
    @Test
    public void testSetUpFiniteStateMachines() {
        Scanner scanner = new Scanner();
        // Test that two finite state machines are added
        assertEquals(4, scanner.finiteStateMachines.size());
        FSM fsm_operand = scanner.finiteStateMachines.get(1);
        fsm_operand.next((int) '6');
        
    }

    @Test
    public void testRunLine() {
        Scanner scanner = new Scanner();
        String line = "(+ 6 3)";
        ArrayList<Token> tokens = scanner.runLine(line);
        assertEquals(5, tokens.size());
        assertEquals(TokenType.PARENTESIS, tokens.get(0).getTokenType());
        assertEquals(TokenType.OPERANDARITHMETIC, tokens.get(1).getTokenType());
        assertEquals(TokenType.NUMBER, tokens.get(2).getTokenType());
        assertEquals(TokenType.NUMBER, tokens.get(3).getTokenType());
        assertEquals(TokenType.PARENTESIS, tokens.get(4).getTokenType());
    }


    @Test
    public void testRunLineFUN() {
        Scanner scanner = new Scanner();
        String line = "( DEFUN )";
        ArrayList<Token> tokens = scanner.runLine(line);
        assertEquals(3, tokens.size());
        assertEquals(TokenType.PARENTESIS, tokens.get(0).getTokenType());
        assertEquals(TokenType.FUN, tokens.get(1).getTokenType());
        assertEquals(TokenType.PARENTESIS, tokens.get(2).getTokenType());
    }

    @Test
    public void testRunLineGram() {
        Scanner scanner = new Scanner();
        String line = "( + X 5 )";
        ArrayList<Token> tokens = scanner.runLine(line);
        assertEquals(5, tokens.size());
    }

    @Test
    public void testSetQ() {
        Scanner scanner = new Scanner();
        String line = "(SETQ X 5 )";
        scanner.HashSetQ(line);
        assertEquals(1,scanner.getVariableSet().size());
    }

    @Test
    public void testSetQValue() {
        Scanner scanner = new Scanner();
        String line = "(SETQ X 5 )";
        scanner.HashSetQ(line);
        assertEquals(5,scanner.getVariableSet().get("X"));
    }

    @Test
    public void testDefun() {
        Scanner scanner = new Scanner();
        String line = "(DEFUN SUMA (X Y) (+ X Y))";
        scanner.HashDefun(line);
        assertEquals(1,scanner.getFunctionSet().size());
    }

    @Test
    public void testDefunArgument() {
        Scanner scanner = new Scanner();
        String line = "(DEFUN SUMA (X Y) (+ X Y))";
        scanner.HashDefun(line);
        assertEquals("( X Y ) ( + X Y )",scanner.getFunctionSet().get("SUMA"));
    }
}
