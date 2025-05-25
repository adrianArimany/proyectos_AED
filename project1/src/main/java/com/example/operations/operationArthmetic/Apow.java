package com.example.operations.operationArthmetic;

import com.example.utils.LoggerManager;
import com.example.operations.Operation;

public class Apow implements Operation {
    private static final String CATEGORY = "arthmetic";

    /**
     * Calculate the absolute value of a number.
     *
     * @param args The only argument should be a number, either an instance of Double or Float.
     * @return The absolute value of the argument.
     */
    @Override
    public Object execute(Object... args) {
        if (args.length != 1) {
            LoggerManager.logWarning(CATEGORY, "must have one operand");
            return null;
        }

        double result;
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
        }
        Number number = (Number) args[0];
        double numbervalue = number.doubleValue();
        result = Math.abs(numbervalue);

        if (hasDouble) return result;
        if (hasFloat) return (float) result;
        return (int) result;
    }
}
