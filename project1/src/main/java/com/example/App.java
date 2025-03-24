package com.example;

import javax.swing.SwingUtilities;

import com.example.gui.FileManagerGUI;

/**
 * Hello world!
 * 
 * 
 * IMPORTANT:
 * @TODO
 * 
 */
public class App 
{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileManagerGUI().setVisible(true);
        });
    }
}

