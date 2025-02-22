package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Ares implements Operation{
    private static final String CATEGORY = "arthmetic";

    @Override
    public Object execute(Object... args){
        if (!(args.length == 2)) {
            LoggerManager.logWarning(CATEGORY, "must have two operands");
            return null;
        }
        
        double cond1 = (double)args[0] - (int)args[0];
        double cond2 = (double)args[1] - (int)args[1];

        if((cond1 != 0)||(cond2 != 0)){
            LoggerManager.logWarning(CATEGORY, "must be integers");
            return null;
        }

        int result;

        for (Object arg : args) {
            if (arg == null) {
                LoggerManager.logUnsupportedOperation(CATEGORY, null);
                return null;
            } 
        }

        result = (int) args[0] % (int) args[1];
        
        return result;
    }
}
