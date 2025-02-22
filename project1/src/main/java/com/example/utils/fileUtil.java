package com.example.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 
 * reads the file and sends it to dataManager to get the tokens from the GUI
 * 
 * it also stores given file to the directory /root/files
 * 
 */
public class fileUtil {
    private static final String CATEGORY = "fileReader";
    /**
     * Reads the content of a file at the given path and returns it as a String.
     *
     * @param filePath the path to the file (plain text or .lsp)
     * @return the file's content as a String
     * @throws IOException if an error occurs reading the file
     */
    public static String readFileAsString(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try {
            // Reads all characters from the file into a String
            return Files.readString(path);
        } catch (IOException e) {
            LoggerManager.logSevere(CATEGORY, "Error reading file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Copies the file at the given sourceFilePath into the directory
     * specified by (user.dir) + "/files", preserving the original file name.
     * If a file with the same name already exists, a suffix is appended to the name.
     *
     * @param sourceFilePath the path to the file that you want to store.
     * @throws IOException if an I/O error occurs during the file copy.
     */
    public static void storeFile(String sourceFilePath) throws IOException {
        //defining a directory path
        String targetDirString =  System.getProperty("user.dir") + "/files";
        Path targetDir = Paths.get(targetDirString);

        if (!Files.exists(targetDir)) {
            Files.createDirectory(targetDir);
        }
         // Get the source file's path and original file name.
        Path sourcePath = Paths.get(sourceFilePath);
        Path targetPath = targetDir.resolve(sourcePath.getFileName());
        String originalFileName = sourcePath.getFileName().toString();
        
        if (Files.exists(targetPath)) {
            int counter = 1;
            int dotIndex = originalFileName.lastIndexOf('.');
            String baseName = (dotIndex == -1) ? originalFileName : originalFileName.substring(0, dotIndex);
            String extension = (dotIndex == -1) ? "" : originalFileName.substring(dotIndex);

            while (Files.exists(targetPath)) {
                String newFileName = baseName + "_(" + counter + ")" + extension;
                targetPath = targetDir.resolve(newFileName);
                counter++;
        }
    }
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
}

