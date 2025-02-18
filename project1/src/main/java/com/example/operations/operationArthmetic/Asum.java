package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Asum implements Operation {
    private static final String CATEGORY = "arthmetic";
    
    @Override
    public Object execute(Object... args) {
        if (args.length < 2) {
            LoggerManager.logWarning(CATEGORY, "must have at least two operands");
            return null;
        }

        double result = 0;
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

            result += num.doubleValue();
        }

        if (hasDouble) return result;
        if (hasFloat) return (float) result;
        return (int) result;
    }    
    // @Override
    // public Object execute(Object... args) {
    //     if (args[0] instanceof Float && args[1] instanceof Float) {
    //         return ((float) args[0]) + ((float) args[1]);
    // } else if (args[0] instanceof Double && args[1] instanceof Double) {
    //         return ((double) args[0]) + ((double) args[1]);
    // } else if (args[0] instanceof Integer && args[1] instanceof Integer) {
    //         return ((int) args[0]) + ((int) args[1]);
    // } else {
    //     LoggerManager.logUnsupportedOperation(CATEGORY, args[0].getClass());
    //     return null;
    // }    
    // }
    
}
