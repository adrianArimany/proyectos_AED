package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.operations.operationArthmetic.*;
import org.junit.Test;

public class operationsTest {
    private final Asum asum = new Asum();
    private final Asub asub = new Asub();
    @Test
    public void testLessThanTwoOperands() {
        Object[] args = new Object[1];
        assertNull(asum.execute(args));
    }
    @Test
    public void testNullOperand() {
        Object[] args = new Object[] {1, null};
        assertNull(asum.execute(args));
    }
    @Test
    public void testNonNumberOperand() {
        Object[] args = new Object[] {1, "a"};
        assertNull(asum.execute(args));
    }

    @Test
    public void testSum() {
        Object[] args = new Object[] {1, 2, 1.5, 0.5};
        assertEquals(5.0, asum.execute(args));
    }

    @Test
    public void testSub() {
        Object[] args = new Object[] {5, 2, 1.5, 0.5};
        assertEquals(1.0, asub.execute(args));
    }

    @Test
    public void testMult() {
        Amult amult = new Amult();
        Object[] args = new Object[] {5, 2,2.0};
        assertEquals(20.0, amult.execute(args));
    }

    @Test
    public void testRes() {
        Ares ares = new Ares();
        Object[] args = new Object[] {5, 2};
        assertEquals(1, ares.execute(args));
    }

    @Test
    public void testDiv(){
        Adiv adiv = new Adiv();
        Object[] args = new Object[] {5, 2};
        assertEquals(2.5, adiv.execute(args));
    }
}
