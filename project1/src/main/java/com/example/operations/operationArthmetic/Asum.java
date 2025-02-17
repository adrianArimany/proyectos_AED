package com.example.operations.operationArthmetic;

import com.example.operations.Operation;
import com.example.utils.LoggerManager;

public class Asum implements Operation {
    private static final String CATEGORY = "arthmetic";
    @Override
    public Object execute(Object... args) {
        if (args[0] instanceof Float && args[1] instanceof Float) {
    return ((Float) args[0]).floatValue() + ((Float) args[1]).floatValue();
} else if (args[0] instanceof Double && args[1] instanceof Double) {
    return ((Double) args[0]).doubleValue() + ((Double) args[1]).doubleValue();
} else {
    LoggerManager.logUnsupportedOperation(CATEGORY, args[0].getClass());
    return null;
}


        
    }
    
}
