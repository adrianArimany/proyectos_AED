package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Amult implements Operation{
    private static final String CATEGORY = "arthmetic";
    
    /**
     * This method implements the "*" operator.
     * It takes two or more operands and returns their product.
     * The result is a double if the product of the numbers is larger than the maximum int value,
     * a float if it is larger than the maximum long value, and an integer if it fits within the maximum int value.
     * If any of the operands are null, a log message is written and null is returned.
     * If any of the operands are not numbers, a log message is written and null is returned.
     */
    @Override
    public Object execute(Object... args) {
        if (args.length < 2) {
            LoggerManager.logWarning(CATEGORY, "must have at least two operands");
            return null;
        }

        double result = 1;
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

            result *= num.doubleValue();
        }

        if (hasDouble) return result;
        if (hasFloat) return (float) result;
        return (int) result;
}
}
