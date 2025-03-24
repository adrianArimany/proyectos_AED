package com.example.operations.operationsConditional;



import com.example.operations.Operation;

public class CLessThan implements Operation {
    @Override
    public Object execute(Object... operands) {
        if (operands.length != 2) {
            throw new RuntimeException("LessThan operator requires exactly 2 operands.");
        }
        double a = ((Number) operands[0]).doubleValue();
        double b = ((Number) operands[1]).doubleValue();
        return a < b;
    }
}
