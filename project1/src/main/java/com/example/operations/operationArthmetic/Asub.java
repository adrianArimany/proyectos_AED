package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Asub implements Operation{
    private static final String CATEGORY = "arthmetic";

    /**
     * Subtract all the arguments to the first argument.
     * This operation needs at least two arguments.
     * If the arguments are not numbers, the operation is not supported.
     * If the arguments are a mix of double and float, the result is a double.
     * If the arguments are only float, the result is a float.
     * If the arguments are only int, the result is an int.
     * @param args the arguments to the operation
     * @return the result of the operation or null if the operation is not supported
     */
    @Override
    public Object execute(Object... args){
        if (args.length < 2) {
            LoggerManager.logWarning(CATEGORY, "must have two operands");
            return null;
        }

        Number nuevo = (Number) args[0];
        double result = 2*nuevo.doubleValue();
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

            result -= num.doubleValue();
        }
        if (hasDouble) return result;
        if (hasFloat) return (float) result;
        return (int) result;
    }
}
