package com.example.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.example.dataManager.Scanner;
import com.example.dataManager.Token;
import com.example.objectInterpreter.interpreterLisp;
import com.example.operations.OperationFun.DefunOperator;
import com.example.operations.OperationFun.SetqOperator;
import com.example.utils.fileUtil;

/**
 * FileManagerGUI provides an interface to either load a LISP file or write LISP code directly.
 * The user can then run the interpreter on the code.
 */
public class FileManagerGUI extends JFrame {

    private JTextArea codeTextArea;
    private JTextArea outputTextArea;
    private JButton runButton;
    private JButton loadFileButton;
    private JButton resetButton;

    public FileManagerGUI() {
        super("LISP Interpreter - Code Runner");
        initComponents();
    }

    private void initComponents() {
        // Create the text area where the user can type or see loaded code.
        codeTextArea = new JTextArea(15, 50);
        JScrollPane codeScrollPane = new JScrollPane(codeTextArea);

        // Create the text area to display output or errors.
        outputTextArea = new JTextArea(8, 50);
        outputTextArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);

        // Create buttons.
        runButton = new JButton("Run Code");
        loadFileButton = new JButton("Load File");
        resetButton = new JButton("Reset Program");

        // Panel for buttons.
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadFileButton);
        buttonPanel.add(runButton);
        buttonPanel.add(resetButton);

        // Arrange all components in the main panel.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(codeScrollPane, BorderLayout.CENTER);
        mainPanel.add(outputScrollPane, BorderLayout.SOUTH);

        // Add the main panel to the frame.
        this.add(mainPanel);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Center the frame on the screen

        // Action for loading a file.
        loadFileButton.addActionListener((ActionEvent e) -> loadFileAction());
        // Action for running the interpreter.
        runButton.addActionListener((ActionEvent e) -> runInterpreterAction());
        //REseting the program
        resetButton.addActionListener((ActionEvent e) -> resetProgram());
    }

    private void loadFileAction() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(FileManagerGUI.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                fileUtil.storeFile(selectedFile.getAbsolutePath());
                String fileContent = fileUtil.readFileAsString(selectedFile.getAbsolutePath());
                codeTextArea.setText(fileContent);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(FileManagerGUI.this,
                        "Error reading file: " + ex.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Uses the Scanner to tokenize the code from the text area,
     * then uses interpreterLisp to evaluate it.
     */
    private void runInterpreterAction() {
        String code = codeTextArea.getText();
        try {
            // Tokenize and evaluate the code from the text area.
            Scanner scanner = new Scanner();
            List<Token> tokens = scanner.runLine(code);
            // Create the outer interpreter; its placeholderCache will be shared.
            interpreterLisp interpreter = new interpreterLisp(tokens);
            Object result = interpreter.evaluate();
            outputTextArea.setText("Result: " + result);
    
            // If the code defines a function, automatically prompt for a function call.
            if (code.toUpperCase().contains("DEFUN")) {
                if (!DefunOperator.functionDefinitions.isEmpty()) {
                    // For simplicity, use the first defined function.
                    String functionName = DefunOperator.functionDefinitions.keySet().iterator().next();
                    Object[] definition = DefunOperator.functionDefinitions.get(functionName);
                    List<String> parameters = (List<String>) definition[0];
    
                    // Build the function call automatically.
                    StringBuilder callExpr = new StringBuilder("(" + functionName);
                    for (String param : parameters) {
                        String input = JOptionPane.showInputDialog(
                                FileManagerGUI.this,
                                "Enter value for parameter " + param + ":",
                                "Parameter Input",
                                JOptionPane.QUESTION_MESSAGE);
                        if (input == null || input.trim().isEmpty()) {
                            throw new Exception("No value provided for parameter " + param);
                        }
                        callExpr.append(" ").append(input);
                    }
                    callExpr.append(")");
    
                    // Evaluate the function call using the same shared placeholder cache.
                    List<Token> callTokens = scanner.runLine(callExpr.toString());
                    // Use the two-argument constructor to pass the existing placeholderCache.
                    interpreterLisp callInterpreter = new interpreterLisp(callTokens, interpreter.getPlaceholderCache());
                    Object callResult = callInterpreter.evaluate();
                    outputTextArea.append("\nCall Result: " + callResult);
                }
            }
        } catch (Exception ex) {
            outputTextArea.setText("Error: " + ex.getMessage());
        }
    }
    
     private void resetProgram() {
        // Clear function definitions and variables (adjust if you store more global state)
        DefunOperator.functionDefinitions.clear();
        SetqOperator.variables.clear(); // If you have such a map for SETQ
        outputTextArea.setText("Program state has been reset.");
        // Optionally clear the code area:
        codeTextArea.setText("");
    }

    



    
}
