package com.company;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;

public class Main {


    public static void main(String[] args)
    {
        int erosion_size = 10;
        int dilation_size = 10;

        System.load( "/usr/local/opencv/lib/libopencv_java2410.dylib" );

        Mat original = Highgui.imread("Images/normal1.jpg",Highgui.CV_LOAD_IMAGE_COLOR);
        //Mat gaussian = new Mat(original.rows(),original.cols(),Imgproc.COLOR_RGB2RGBA);
        Mat grayscale = new Mat(original.rows(),original.cols(),Imgproc.COLOR_RGB2GRAY);
        Mat threshold = new Mat(original.rows(),original.cols(),Imgproc.COLOR_RGB2RGBA);
        Mat result = new Mat(original.rows(),original.cols(),Imgproc.COLOR_RGB2RGBA);

        Mat erosion = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(2*erosion_size + 1, 2*erosion_size+1));

        Mat dilation = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(2 * dilation_size + 1, 2*dilation_size+1));


        Imgproc.cvtColor(original, grayscale, Imgproc.COLOR_RGB2GRAY);
        //Imgproc.GaussianBlur(grayscale, gaussian, new Size(11, 11), 0);
        Imgproc.threshold(grayscale, threshold, 200.0, 255.0, Imgproc.THRESH_BINARY_INV);
        Imgproc.erode(threshold, result, erosion);
        Imgproc.dilate(result, result, dilation);


        new WindowDisplayer("Images/normal1.jpg",original);
        //new WindowDisplayer("Images/gaussian.jpg",gaussian);
        new WindowDisplayer("Images/threshold.jpg",threshold);

    }
}