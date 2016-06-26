package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileOpener{
	File selectedFile;

    protected File createFileUI(Component parent) {
                int result =0;
                JFileChooser openFile = new JFileChooser();
                openFile.setCurrentDirectory(new File(".."));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Bilder","jpg", "png");
                openFile.setFileFilter(filter);
                result = openFile.showOpenDialog(null);
                
                switch (result)
                {
                   case JFileChooser.APPROVE_OPTION:
                      selectedFile = openFile.getSelectedFile();
                      break;

                   case JFileChooser.CANCEL_OPTION:
                      JOptionPane.showMessageDialog(parent, "Cancelled",
                                                    "FileOpener",
                                                    JOptionPane.OK_OPTION);
                      break;
                
                   case JFileChooser.ERROR_OPTION:
                      JOptionPane.showMessageDialog(parent, "Error",
                                                    "FileOpener",
                                                    JOptionPane.OK_OPTION);
                }
                return selectedFile;
    }
}