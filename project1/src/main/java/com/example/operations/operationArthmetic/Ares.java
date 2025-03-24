package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Ares implements Operation{
    private static final String CATEGORY = "arthmetic";

    /**
     * 
     * @param args
     * @return The remainder of the division between the two given numbers.
     * Only works with two integer numbers.
     */
    @Override
    public Object execute(Object... args){
        if (!(args.length == 2)) {
            LoggerManager.logWarning(CATEGORY, "must have two operands");
            return null;
        }
        
        double cond1 = ((Number)args[0]).doubleValue() - ((Number)args[0]).intValue();
        double cond2 = ((Number)args[1]).doubleValue() - ((Number)args[1]).intValue();

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

        result = ((Number)args[0]).intValue() % ((Number)args[1]).intValue();
        
        return result;
    }
}
