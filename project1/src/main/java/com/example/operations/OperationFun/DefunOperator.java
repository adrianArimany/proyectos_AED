

package com.example.operations.OperationFun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.operations.Operation;

public class DefunOperator implements Operation {
    public static Map<String, Object[]> functionDefinitions = new HashMap<>();

    /**
     * Executes the DEFUN operation.
     * Expected operands: 
     *   operand[0]: function name (String)
     *   operand[1]: parameter list (could be a List<String> or another structure)
     *   operand[2]: function body (could be an expression or list of expressions)
     */
    @Override
    public Object execute(Object... operands) {
        if (operands.length < 3) {
            throw new RuntimeException("DEFUN requires at least 3 operands: name, parameter list, and body.");
        }
    // Convert function name to uppercase for consistent lookup.
    String functionName = operands[0].toString().toUpperCase();
    List<String> parameters = (List<String>) operands[1];
    Object body = operands[2];
    functionDefinitions.put(functionName, new Object[]{parameters, body});
    return "Function " + functionName + " defined.";
}
}
