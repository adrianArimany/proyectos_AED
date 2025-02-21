package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Amin implements Operation{
private static final String CATEGORY = "arthmetic";

     @Override
    public Object execute(Object... args) {
        if (args.length < 1) {
            LoggerManager.logWarning(CATEGORY, "must have at least one operand");
            return null;
        }

        double result = (double) args[0];
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
            if((double)arg < result ){
                result = (double) arg;
            }

        }


        if (hasDouble) return result;
        if (hasFloat) return (float) result;
        return (int) result;
    }
}
