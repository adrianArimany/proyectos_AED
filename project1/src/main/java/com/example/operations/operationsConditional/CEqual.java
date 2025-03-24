package com.example.operations.operationsConditional;

import com.example.operations.Operation;
public class CEqual implements Operation {
    @Override
    public Object execute(Object... operands) {
        if (operands.length != 2) {
            throw new RuntimeException("= operator requires exactly 2 operands.");
        }
        // For numbers we compare numeric equality, for others use .equals()
        if (operands[0] instanceof Number && operands[1] instanceof Number) {
            double a = ((Number) operands[0]).doubleValue();
            double b = ((Number) operands[1]).doubleValue();
            return a == b;
        }
        return operands[0].equals(operands[1]);
    }
}
