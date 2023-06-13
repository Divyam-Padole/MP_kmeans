package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
public class CsvFileSelector extends JFrame {
    private JButton button;

    public CsvFileSelector() {
        super("CSV File Selector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        button = new JButton("Select CSV File");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(CsvFileSelector.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
// Do something with the selected file
                    System.out.println("path is"+selectedFile);
                  //  KMeans meanalgo=new KMeans();

                }
            }
        });
        add(button);
        pack();
        setVisible(true);
    }
    public static void main(String[] args) {
        new CsvFileSelector();
    }
}