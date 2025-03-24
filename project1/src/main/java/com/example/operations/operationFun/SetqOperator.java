package com.example.operations.operationFun;


import java.util.HashMap;
import java.util.Map;

import com.example.operations.Operation;

public class SetqOperator implements Operation {
    // A simple global environment for variables.
    public static Map<String, Object> variables = new HashMap<>();

    /**
     * Executes the SETQ operation.
     * Expected operands:
     *   operand[0]: variable name (String)
     *   operand[1]: value to assign
     */
    @Override
    public Object execute(Object... operands) {
        if (operands.length != 2) {
            throw new RuntimeException("SETQ requires exactly 2 operands: variable name and value.");
        }
        String variableName = (String) operands[0];
        Object value = operands[1];
        variables.put(variableName, value);
        return value;
    }
}