package com.company;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando on 02/12/14.
 */
public class CellFinder
{
    int blockSize = 15;
    int cellCount = 0;
    Mat original, gaussian, grayscale, threshold, threshold2, mask, edges;

    public CellFinder()
    {
        original = Highgui.imread("img/sample001.jpg", Highgui.CV_LOAD_IMAGE_COLOR);
        gaussian = new Mat(original.rows(), original.cols(), Imgproc.COLOR_RGB2RGBA);
        grayscale = new Mat();
        threshold = new Mat(original.rows(), original.cols(), Imgproc.COLOR_RGB2RGBA);
        threshold2 = new Mat(original.rows(), original.cols(), Imgproc.COLOR_RGB2RGBA);
        mask = new Mat(original.rows() + 2, original.cols() + 2, CvType.CV_8UC1, new Scalar(0));
        edges = new Mat(original.rows(), original.cols(), Imgproc.COLOR_RGB2RGBA);
    }

    public void imageProcessing()
    {
        Imgproc.cvtColor(original, grayscale, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(grayscale, gaussian, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(gaussian, threshold, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, blockSize, 1);
        Imgproc.adaptiveThreshold(gaussian, threshold2, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, blockSize, 1);
        Imgproc.Canny(threshold, edges, 100.0, 100.0 * 3);
        //Imgproc.dilate(threshold, threshold, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
        Imgproc.erode(threshold, threshold, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));

    }

    public void windowsDisplayer()
    {
        new WindowDisplayer("img/gaussian/gaussian001.jpg", gaussian, "gaussian");
        new WindowDisplayer("img/grayscale/grayscale001.jpg", grayscale, "Gray");
        new WindowDisplayer("img/threshold/threshold001.jpg", threshold2, "Threshold");
        new WindowDisplayer("img/edges/edges001.jpg", edges, "Edges");
        new WindowDisplayer("img/marked/sample001.jpg", original, "Original");
        JOptionPane.showMessageDialog(null, cellCount + " cells found.");
    }

    public void findCells()
    {
        double X, Y;
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        List<Point> usedPoints = new ArrayList<Point>();
        Point pt, pt2;
        Imgproc.findContours(threshold, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
        //Imgproc.drawContours(threshold,contours,1,new Scalar(255,255,255),1);

        for (int i = 0; i < contours.size(); i++)
        {
            //System.out.println(Imgproc.contourArea(contours.get(i)));
            if (Imgproc.contourArea(contours.get(i)) > 10 )
            {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                //System.out.println(rect.width);

                pt = new Point(rect.x,rect.y);
                pt2 = new Point(rect.x + rect.width, rect.y + rect.height);
                X = rect.width / 4;
                Y = rect.height / 4;

                if (rect.height > 11 && rect.height < 30)
                {

                    Imgproc.floodFill(threshold2, mask, new Point(X, Y), new Scalar(0));

                    if (usedPoints.size()==0)
                    {
                        usedPoints.add(pt);
                    }

                    //System.out.println(rect.x +","+rect.y+","+rect.height+","+rect.width);
                    if (!isUsed(usedPoints,pt))
                    {
                        Core.rectangle(original, pt, pt2, new Scalar(0, 0, 255));
                        cellCount++;
                    }
                }
                usedPoints.add(pt);
            }
        }
    }

    public boolean isUsed(List<Point> usedPoints, Point pt)
    {
        boolean ret = false;
        double plusOrMinus = 10.0;
        double plusX = pt.x+plusOrMinus;
        double plusY = pt.y+plusOrMinus;
        double minusX = pt.x-plusOrMinus;
        double minusY = pt.y-plusOrMinus;

        for (Point usedPoint : usedPoints) {
            if (pt == usedPoint || usedPoint.x <= plusX && pt.y == usedPoint.y || usedPoint.y <= plusY && pt.x == usedPoint.x || usedPoint.x >= minusX && pt.y == usedPoint.y || usedPoint.y >= minusY && pt.x == usedPoint.x) {
                ret = true;
            }
        }
        return ret;
    }
}