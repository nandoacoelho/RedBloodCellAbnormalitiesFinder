package com.company;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javax.swing.*;

public class WindowDisplayer extends JFrame {

    public WindowDisplayer(String imgStr, Mat m){
        Highgui.imwrite(imgStr,m);
        JFrame frame = new JFrame("Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);


        ImageIcon image = new ImageIcon(imgStr);
        frame.setSize(image.getIconWidth()+10,image.getIconHeight()+35);
        // Draw the Image data into the BufferedImage
        JLabel label1 = new JLabel(" ", image, JLabel.CENTER);
        frame.getContentPane().add(label1);

        frame.validate();
        frame.setVisible(true);
    }
}