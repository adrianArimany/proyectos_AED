package com.example;

import javax.swing.SwingUtilities;

import com.example.gui.FileManagerGUI;

/**
 * Hello world!
 * 
 * 
 * IMPORTANT:
 * @TODO
 * - Finish all the other finite statemachines for the other operands and characters and comments etc
 */
public class App 
{
    public static void main( String[] args )
    {
        SwingUtilities.invokeLater(() -> {
            FileManagerGUI gui = new FileManagerGUI();
            gui.show();
        });
    }
}
