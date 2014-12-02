package com.company;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args)
    {
        int blockSize = 11;
        System.load("/usr/local/opencv/lib/libopencv_java2410.dylib");

        Mat original = Highgui.imread("Images/normal1.jpg",Highgui.CV_LOAD_IMAGE_COLOR);
        Mat gaussian = new Mat(original.rows(),original.cols(),Imgproc.COLOR_RGB2RGBA);
        Mat grayscale = new Mat();
        Mat threshold = new Mat(original.rows(),original.cols(),Imgproc.COLOR_RGB2RGBA);
        Mat threshold2 = new Mat(original.rows(),original.cols(),Imgproc.COLOR_RGB2RGBA);
        Mat mask = new Mat(original.rows()+2, original.cols()+2, CvType.CV_8UC1, new Scalar(0));
        Mat edges = new Mat(original.rows(),original.cols(),Imgproc.COLOR_RGB2RGBA);

        Imgproc.cvtColor(original, grayscale, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(grayscale, gaussian, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(grayscale, threshold, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, blockSize, 1);
        Imgproc.Canny(threshold, edges, 100.0, 100.0 * 3);
        Imgproc.dilate(threshold, threshold, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
        Imgproc.dilate(threshold, threshold, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
        Imgproc.erode(threshold, threshold, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));

        contours(threshold, original, threshold2, mask);

        new WindowDisplayer("Images/threshold.jpg", threshold, "Threshold");
        new WindowDisplayer("Images/normal1e2.jpg", original, "Original");
        //new WindowDisplayer("Images/gaussian.jpg", gaussian, "gaussian");
        //new WindowDisplayer("Images/threshold.jpg", threshold, "Threshold");
        //new WindowDisplayer("Images/grayscale.jpg", grayscale, "Gray");
        //new WindowDisplayer("Images/result.jpg", edges, "Edges");
    }

    public static void contours(Mat threshold, Mat original, Mat threshold2, Mat mask)
    {
        double X, Y;
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(threshold, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
        //Imgproc.drawContours(threshold,contours,1,new Scalar(255,255,255),1);
        for(int i=0; i< contours.size();i++){
            System.out.println(Imgproc.contourArea(contours.get(i)));
            if (Imgproc.contourArea(contours.get(i)) > 10 ){
                Rect rect = Imgproc.boundingRect(contours.get(i));
                System.out.println(rect.width);
                if (rect.height > 8 && rect.height < 17){
                    //System.out.println(rect.x +","+rect.y+","+rect.height+","+rect.width);
                    Core.rectangle(original, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,0,255));
                    X = rect.width/2;
                    Y = rect.height/2;
                    Imgproc.floodFill(threshold2, mask, new Point(X,Y),new Scalar(0));
                }
            }
        }
    }
}
