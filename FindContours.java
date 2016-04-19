package org.opencv.samples.imagemanipulations;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * Created by sensor on 3/22/2016.
 */
public class FindContours {

    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private double mMinContourArea = 0.1;

    public void detect(Mat rgbImage){
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        MatOfPoint maxContour = new MatOfPoint();
        Mat mMask = Mask.getMask(rgbImage,false);
        Mat mHierarchy = new Mat();

        Imgproc.findContours(mMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
                maxContour = wrapper;
        }
        mContours.add(maxContour);
/*
        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(1, 1), contour);
                mContours.add(contour);
            }
        }
*/
    }
    public List<MatOfPoint> getContours() {
        return mContours;
    }

    public List<MatOfPoint> maskDetect(Mat maskOfImg){
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        MatOfPoint maxContour = new MatOfPoint();
        Mat mHierarchy = new Mat();

        Imgproc.findContours(maskOfImg, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea) {
                maxArea = area;
                maxContour = wrapper;
            }
        }
        mContours.add(maxContour);

        return mContours;
    }

    public Point findCoord(List<MatOfPoint> contour){

        Point center = new Point();

        MatOfPoint mop = new MatOfPoint();
        mop.fromList(contour);

        Moments moments = Imgproc.moments(mop);

        Point centroid = new Point();

        centroid.x = moments.get_m10() / moments.get_m00();
        centroid.y = moments.get_m01() / moments.get_m00();

        center.x = 0;
        center.y = 0;

        return center;
    }
}
