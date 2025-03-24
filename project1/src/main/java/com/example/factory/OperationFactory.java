package com.example.factory;

import com.example.operations.Operation;
import com.example.operations.operationArthmetic.Adiv;
import com.example.operations.operationArthmetic.Amult;
import com.example.operations.operationArthmetic.Asub;
import com.example.operations.operationArthmetic.Asum;
import com.example.operations.operationFun.DefunOperator;
import com.example.operations.operationFun.SetqOperator;
import com.example.operations.operationsBoolean.BEqual;
import com.example.operations.operationsConditional.CEqual;
import com.example.operations.operationsConditional.CGreaterThan;
import com.example.operations.operationsConditional.CGreaterThanOrEqual;
import com.example.operations.operationsConditional.CLessThan;
import com.example.operations.operationsConditional.CLessThanOrEqual;

public class OperationFactory {

    public static Operation getOperation(String operator) {
        return switch (operator.toUpperCase()) {
            case "+" -> new Asum();
            case "-" -> new Asub();
            case "*" -> new Amult();
            case "/" -> new Adiv();
            case "DEFUN" -> new DefunOperator();
            case "SETQ" -> new SetqOperator();
            case "<" -> new CLessThan();
            case ">" -> new CGreaterThan();
            case "=" -> new CEqual();
            case "<=" -> new CLessThanOrEqual();
            case ">=" -> new CGreaterThanOrEqual();
            case "EQ" -> new BEqual();
            case "EQL" -> new BEqual();
            case "EQUAL" -> new BEqual();
            default -> null;    
        };
    }
}
