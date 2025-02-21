package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Adiv implements Operation{
    private static final String CATEGORY = "arthmetic";

    @Override
    public Object execute(Object... args){
        if (args.length > 2) {
            LoggerManager.logWarning(CATEGORY, "must have two or one operands");
            return null;
        }

        double result = 0;
        boolean hasDouble = false;

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
        }

        if(args.length == 1){
            result = 1 / (double) args[0];
        }else{
            result = (double) args[0] / (double) args[1];
        }
        

        if (hasDouble) return result;
        return (float) result;
    }

}
