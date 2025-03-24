package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Amax implements Operation{
    private static final String CATEGORY = "arthmetic";

/**
 * Executes the operation to find the maximum value among the provided arguments.
 *
 * @param args The arguments to evaluate, must contain at least one operand.
 * @return The maximum value among the arguments, returned as a Double if any 
 *         argument is a Double, as a Float if any argument is a Float, 
 *         otherwise as an Integer. Returns null if the input is invalid.
 * Logs a warning if there are no operands or if any operand is null or not a Number.
 */
    @Override
    public Object execute(Object... args) {
        if (args.length < 1) {
            LoggerManager.logWarning(CATEGORY, "must have at least one operand");
            return null;
        }

        double result = ((Number) args[0]).doubleValue();
        boolean hasDouble = false;
        boolean hasFloat = false;

        for (Object arg : args) {
            if (arg == null) {
                LoggerManager.logUnsupportedOperation(CATEGORY, null);
                return null;
            } else if (!(arg instanceof Number)) {
                LoggerManager.logUnsupportedOperation(CATEGORY, arg.getClass());
                return null;
            }
            
            Number num = (Number) arg;

            if (num instanceof Double) hasDouble = true;
            if (num instanceof Float) hasFloat = true;
            if(result < ((Number)arg).doubleValue()) {
                result = ((Number)arg).doubleValue();
            }

        }


        if (hasDouble) return result;
        if (hasFloat) return (float) result;
        return (int) result;
    }
}
