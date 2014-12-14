package com.company;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CellFinder
{
    private int cellCount = 0;
    private int sickleCellCount = 0;
    private Mat sample, gaussian, grayscale, threshold, edges, result, circles;

    String samplePath = "img/samples/SickleCellsB001.jpg";
    String resultPath = "img/result/resultSickleB001.jpg";
    String threshPath = "img/threshold/thresholdSickleB001.jpg";
    String edgesPath = "img/edges/edgesSickleB001.jpg";
    String blurPath = "img/blur/blurSickleB001.jpg";
    
    public CellFinder()
    {
        sample = Highgui.imread(samplePath, Highgui.CV_LOAD_IMAGE_COLOR);
        result = Highgui.imread(samplePath, Highgui.CV_LOAD_IMAGE_COLOR);
        gaussian = new Mat(sample.rows(), sample.cols(), Imgproc.COLOR_RGB2RGBA);
        grayscale = new Mat();
        circles = new Mat();
        threshold = new Mat(sample.rows(), sample.cols(), Imgproc.COLOR_RGB2RGBA);
        edges = new Mat();
    }

    public void imageProcessing()
    {
        int blockSize = 15;

        Imgproc.cvtColor(sample, grayscale, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(grayscale, gaussian, new Size(21, 21), 0);
        Imgproc.adaptiveThreshold(gaussian, threshold, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, blockSize, 1);
        //Imgproc.erode(threshold, threshold, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
        //Imgproc.dilate(threshold, threshold, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5)));

        //Canny was applied on the first try, which was not successful
        //Imgproc.Canny(grayscale, edges, 100.0, 100.0 * 3);

    }

    public void windowsDisplayer()
    {
        new WindowDisplayer(blurPath, gaussian, "Gaussian");
        new WindowDisplayer(edgesPath, edges, "Edges");
        new WindowDisplayer(threshPath, threshold, "Threshold");
        new WindowDisplayer(resultPath, result, "Marked");


        JOptionPane.showMessageDialog(null, cellCount + " cells found. " + sickleCellCount + " sickle cells found.");
    }

    public void findCells()
    {
        int sickleCell = 0;
        int cell = 1;
        double minSize = 60;
        double maxSize = 120;
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(threshold, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0; i < contours.size(); i++)
        {
            int count = (int) contours.get(i).size().area();

            if (count < 50) continue;

            MatOfPoint2f pointsf = new MatOfPoint2f();
            contours.get(i).convertTo(pointsf, CvType.CV_32F);
            RotatedRect box = Imgproc.fitEllipse(pointsf);

            if (box.size.height > minSize && box.size.height < maxSize
                    && box.size.width > (box.size.height/5) && box.size.width < (box.size.height/2))
            {
                drawContoursAndBoxes(contours, i, box, sickleCell);
                sickleCellCount++;
            }
            else if (box.size.height > minSize || box.size.height < maxSize
                    || box.size.width > minSize || box.size.width < maxSize)
            {
                drawContoursAndBoxes(contours, i, box, cell);
                cellCount++;
            }
        }
    }

    private void drawContoursAndBoxes(List<MatOfPoint> contours, int i, RotatedRect box, int type)
    {
        if (Math.max(box.size.width, box.size.height) > Math.min(box.size.width, box.size.height) * 30) return;

        // Draw contours outside the cells
        // if(type == 0) Imgproc.drawContours(result, contours, i, new Scalar(0, 255, 9), 2);
        // else if (type == 1) Imgproc.drawContours(result, contours, i, new Scalar(255, 0, 0), 2);

        Point[] vtx = new Point[4];
        box.points(vtx);


        // Draw the box outside the cells
        for (int j = 0; j < 4; j++)
        {
            if(type == 0)
                Core.line(result, vtx[j], vtx[(j + 1) % 4], new Scalar(0, 255, 0), 2);
            if(type == 1)
                Core.line(result, vtx[j], vtx[(j + 1) % 4], new Scalar(255, 0, 0), 2);
        }
    }

    /**
     * Tried this, didn't worked.
     */
//    public void findCellsWithShapes()
//    {
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        List<Point> points = new ArrayList<Point>();
//        Point pt, pt2;
//        Rect rect;
//
//        Imgproc.findContours(edges, contours, contour, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
//
//        for (MatOfPoint contour1 : contours)
//        {
//            if (Imgproc.contourArea(contour1) > 50)
//            {
//                rect = Imgproc.boundingRect(contour1);
//                if (rect.height > 30 && rect.height < 100 && rect.width > 30 && rect.width < 90)
//                {
//                    pt = new Point(rect.x, rect.y);
//                    pt2 = new Point(rect.x + rect.width, rect.y + rect.height);
//                    center = new Point(rect.x + (rect.width / 2), rect.y + (rect.height / 2));
//                    Imgproc.floodFill(threshold, mask, center, new Scalar(255));
//                    Core.rectangle(original, pt, pt2, new Scalar(0, 0, 255));
//                    cellCount++;
//                    points.add(pt);
//                }
//            }
//        }
//    }

    /**
     * Tried this, didn't worked either.
     */
//    public void houghCircles() {
//        Imgproc.HoughCircles(grayscale, circles, Imgproc.CV_HOUGH_GRADIENT, 2, grayscale.rows() / 8, 1000, 10, 20, 50 );
//
//        if (circles.cols() > 0)
//            for (int x = 0; x < circles.cols(); x++)
//            {
//                double vCircle[] = circles.get(0,x);
//
//                if (vCircle == null)
//                    break;
//
//                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
//                int radius = (int)Math.round(vCircle[2]);
//
//                // draw the found circle
//                Core.circle(result, pt, radius, new Scalar(0,255,0), 2);
//                Core.circle(result, pt, 3, new Scalar(0,0,255), 2);
//                cellCount++;
//            }
//    }


}

