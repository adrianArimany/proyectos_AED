package com.example.utils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class LoggerManager {
    private static final Map<String, Logger> loggers = new HashMap<>();
    private static final String LOG_DIR = System.getProperty("user.dir") + "/Logs";

    public static Logger getLogger(String category) {
        if (!loggers.containsKey(category)) {
            return loggers.get(category);    
        }
    }

    

}
