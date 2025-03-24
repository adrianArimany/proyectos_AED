package com.example.operations.operationsBoolean;

import com.example.operations.Operation;

public class BEqual implements Operation {
    /**
     * Checks if two objects are equal.
     * 
     * @param operands
     *            The two objects to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public Object execute(Object... operands) {
        if (operands.length != 2) {
            throw new RuntimeException("EQUAL operator requires exactly 2 operands.");
        }
        // If both operands are numbers, compare numerically.
        if (operands[0] instanceof Number && operands[1] instanceof Number) {
            double a = ((Number) operands[0]).doubleValue();
            double b = ((Number) operands[1]).doubleValue();
            return a == b;
        }
        // Otherwise, use the standard .equals() method.
        return operands[0].equals(operands[1]);
    }
}
