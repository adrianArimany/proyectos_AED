package com.example;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dataManager.FSM;
import com.example.dataManager.Scanner;
import com.example.dataManager.Transition;
public class ScannerTest {
    @Test
    public void testSetUpFiniteStateMachines() {
        Scanner scanner = new Scanner();
        scanner.setUpFiniteStateMachines();
        // Test that two finite state machines are added
        assertEquals(2, scanner.finiteStateMachines.size());
        // Test the first finite state machine
        FSM fsm1 = scanner.finiteStateMachines.get(0);
        assertEquals("PARENTESIS", fsm1.tokenName);
        HashMap<Integer, Transition> transitions1 = fsm1.fsm;
        assertEquals(1, transitions1.size());
        Transition transition1 = transitions1.get(0);
        assertArrayEquals(new int[] {(int) '(', (int) ')'}, transition1.acceptableSymbol);
        assertEquals(1, transition1.nextState);
        assertTrue(transition1.finalState);
        // Test the second finite state machine
        FSM fsm2 = scanner.finiteStateMachines.get(1);
        assertEquals("OPERATIONARTHMETIC", fsm2.tokenName);
        HashMap<Integer, Transition> transitions2 = fsm2.fsm;
        assertEquals(1, transitions2.size());
        Transition transition2 = transitions2.get(0);
        assertArrayEquals(new int[] {(int) '+', (int) '-', (int) '*', (int) '/'}, transition2.acceptableSymbol);
        assertEquals(1, transition2.nextState);
        assertTrue(transition2.finalState);
    }
}
