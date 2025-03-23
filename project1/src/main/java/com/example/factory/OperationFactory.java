package com.example.factory;

import com.example.operations.operationArthmetic.Asum;
import com.example.operations.operationArthmetic.Asub;
import com.example.operations.operationArthmetic.Ares;
import com.example.operations.operationArthmetic.Amult;
import com.example.operations.operationArthmetic.Amin;
import com.example.operations.operationArthmetic.Amax;
import com.example.operations.operationArthmetic.Adiv;
import com.example.operations.operationArthmetic.Aabs;
import com.example.operations.Operation;

public class OperationFactory {
    public static Operation getOperation(String operator) {
        return switch (operator) {
            case "+" -> (Operation) new Asum();
            case "-" -> (Operation) new Asub();
            case "*" -> (Operation) new Amult();
            case "/" -> (Operation) new Adiv();
            case "%" -> (Operation) new Ares();
            case "ABS" -> (Operation) new Aabs();
            case "MAX" -> (Operation) new Amax();
            case "MIN" -> (Operation) new Amin();
            default -> throw new IllegalArgumentException();
        };
    }
}
