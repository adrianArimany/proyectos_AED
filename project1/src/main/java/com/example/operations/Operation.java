package com.example.operations;

public interface  Operation {
    /**
     * Execute the operation with the given arguments and return the result.
     * 
     * @param args The arguments to pass to the operation.
     * @return The result of the operation.
     */
    Object execute(Object... args);
}

