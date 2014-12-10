package com.company;

public class Main {


    public static void main(String[] args)
    {
        System.load("/usr/local/opencv/lib/libopencv_java2410.dylib");

        CellFinder cellFinder = new CellFinder();

        cellFinder.imageProcessing();
        cellFinder.findCells();
        cellFinder.windowsDisplayer();

    }
}
