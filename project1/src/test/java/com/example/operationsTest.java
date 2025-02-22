package com.example;

import static org.junit.Assert.assertNull;
import org.junit.Test;

import com.example.operations.operationArthmetic.Asum;

public class operationsTest {
    private Asum asum = new Asum();
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
}
