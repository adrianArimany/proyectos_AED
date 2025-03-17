package com.example.gui;


import com.example.utils.fileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class FileManagerGUI {
    //private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("fileManager");

    private final JFrame frame;
    private final JTextArea textArea;
    private final JButton btnSelectFile;
    private final JButton btnStoreFile;
    private File selectedFile;

    public FileManagerGUI() {
        // Configuración de la ventana
        frame = new JFrame("LISP File Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Área de texto para mostrar el contenido del archivo
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // Botón para seleccionar archivo
        btnSelectFile = new JButton("Select LISP File");
        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });
        panel.add(btnSelectFile);

        // Botón para almacenar archivo
        btnStoreFile = new JButton("Store File");
        btnStoreFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storeFile();
            }
        });
        panel.add(btnStoreFile);

        frame.add(panel, BorderLayout.SOUTH);
    }

    private void selectFile() {
        // Abre un cuadro de diálogo para seleccionar el archivo .lsp
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Filtro para seleccionar solo archivos .lsp
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("LISP Files", "lsp"));

        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            //logger.info(String.format("LISP file selected: " + selectedFile.getAbsolutePath()));
            try {
                // Leer el contenido del archivo y mostrarlo en el área de texto
                String content = fileUtil.readFileAsString(selectedFile.getAbsolutePath());
                textArea.setText(content);
            } catch (IOException ex) {
                //logger.severe(String.format("Error reading file: " + ex.getMessage()));
                JOptionPane.showMessageDialog(frame, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void storeFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(frame, "No file selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Almacenar el archivo en el directorio /files
            fileUtil.storeFile(selectedFile.getAbsolutePath());
            //logger.info(String.format("LISP file stored successfully: %s", selectedFile.getName()));
            JOptionPane.showMessageDialog(frame, "File stored successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            //logger.severe(String.format("Error storing file: " + ex.getMessage()));
            JOptionPane.showMessageDialog(frame, "Error storing file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void show() {
        frame.setVisible(true);
    }
}
