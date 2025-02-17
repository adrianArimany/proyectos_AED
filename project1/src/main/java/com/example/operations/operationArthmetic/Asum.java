package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Asum implements Operation {
    private static final String CATEGORY = "arthmetic";
    @Override
    public Object execute(Object... args) {
        if (args[0] instanceof Float && args[1] instanceof Float) {
            return ((float) args[0]) + ((float) args[1]);
    } else if (args[0] instanceof Double && args[1] instanceof Double) {
            return ((double) args[0]) + ((double) args[1]);
    } else if (args[0] instanceof Integer && args[1] instanceof Integer) {
            return ((int) args[0]) + ((int) args[1]);
    } else {
        LoggerManager.logUnsupportedOperation(CATEGORY, args[0].getClass());
        return null;
    }    
    }
    
}
