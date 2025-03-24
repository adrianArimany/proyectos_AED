package com.example.operations.operationFun;


import java.util.HashMap;
import java.util.Map;

import com.example.operations.Operation;

public class SetqOperator implements Operation {
    // A simple global environment for variables.
    public static Map<String, Object> variables = new HashMap<>();

    
    //   Executes the SETQ operation.
    //   Expected operands:
    //     operand[0]: variable name (String)
    //     operand[1]: value to assign 
    /**
     * Sets the value of the variable specified by the first operand to the value
     * of the second operand.
     * 
     * @param operands
     *            The two operands. The first operand is the variable name and the
     *            second operand is the value to assign.
     * @return The value that was assigned to the variable.
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