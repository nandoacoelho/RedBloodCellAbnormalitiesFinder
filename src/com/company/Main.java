package com.company;

import org.opencv.core.*;
import org.opencv.highgui.*;

public class Main {

    public static void main(String[] args)
    {
        System.load( "/usr/local/opencv/lib/libopencv_java2410.dylib" );
        Mat m=Highgui.imread("G4045749.jpg",Highgui.CV_LOAD_IMAGE_COLOR);
        new WindowDisplayer("G4045749.jpg",m);
    }
}