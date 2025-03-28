package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Amin implements Operation{
private static final String CATEGORY = "arthmetic";

    /**
     * Execute the MIN operation. The MIN operation takes a variable number of
     * arguments and returns the smallest one. If there are no arguments, it
     * logs a warning and returns null. If any argument is null or not a number,
     * it logs an unsupported operation warning and returns null.
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
            if(((Number)arg).doubleValue() < result ){
                result = ((Number) arg).doubleValue();
            }

        }


        if (hasDouble) return result;
        if (hasFloat) return (float) result;
        return (int) result;
    }
}
