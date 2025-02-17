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


    /**
     * Retrieves or creates a logger for the specified category.
     * @param category The category name (e.g., "gui", "operations", etc.)
     * @return The logger instance for the category
     */
    public static Logger getLogger(String category) {
        if (!loggers.containsKey(category)) {
            return loggers.get(category);    
        }

        synchronized (LoggerManager.class) {
            if (loggers.containsKey(category)) {
                return loggers.get(category);
            }
        }
        Logger logger = Logger.getLogger(category);
        setUpLogger(logger, category);
        loggers.put(category, logger);
        return logger;

    }

    /**
     * Configures a logger to log messages to a specific file.
     * @param logger The logger instance
     * @param category The category name (used for the filename)
     */
    private static void setUpLogger(Logger logger, String category) {
        try {
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            String logFilePath = LOG_DIR + "/" + category + ".log";
            
            
            // Configure file handler
            FileHandler fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

            for (var handler : logger.getParent().getHandlers()) {
                logger.getParent().removeHandler(handler);
            }
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Error setting up logger: " + e.getMessage());
        }
    }

    /**
     * Logs an info message to the specified category.
     * Literally like a println(), only that this is stored in a log file, so only use it when is necessary.
     * 
     * @param category The category name
     * @param message The message to log
     */
    public static void logInfo(String category, String message) {
        getLogger(category).info(message);
    }

    /**
     * Logs a warning message to the specified category.
     * Use it cases like a catch or if a condition is not met
     * 
     * @param category The category name
     * @param message The message to log
     */
    public static void logWarning(String category, String message) {
        getLogger(category).warning(message);
    }

    /**
     * Logs a severe error message to the specified category.
     * Use it when you ecounter a bug or dealing with code you aren't very sure if it works.
     * 
     * @param category The category name
     * @param message The message to log
     */
    public static void logSevere(String category, String message) {
        getLogger(category).severe(message);
    }

    /**
    * Logs a message indicating a configuration issue.
    * The log is stored based on the provided category.
    * Especially useful in the class DataManager
    *
    * @param category The category name (e.g., "gui", "operations", etc.)
    * @param message The configuration issue message
    */
    public static void logConfigIssue(String category, String message) {
        getLogger(category).warning(String.format("Configuration Issue: %s", message));
    }

    /**
    * Logs a message indicating that an operation is currently unsupported for the given data type.
    * The log is stored based on the provided category.
    * Especially useful in the class Operations, in case a user tries to perform an unsupported operation with a unsupported data type
    *
    * @param category The category name (e.g., "gui", "operations", etc.)
    * @param clazz The class object representing the unsupported data type
    */
    public static void logUnsupportedOperation(String category, Class<?> clazz) {
        getLogger(category).info(String.format("Currently Unsupported operation with this data type: %s", clazz.getName()));
    }
}
