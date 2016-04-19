package org.opencv.samples.imagemanipulations;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by sensor on 3/15/2016.
 */
public class Mask {

    public static Mat getMask(Mat ROI, boolean colorMask){   //Mask a region of interest.

        Mat                  hsvROI = new Mat(); //create hsv matrix.
        Mat                  maskROI = new Mat(); //Create mask matrix.
        Mat                  maskGreyROI = new Mat(); //Create inverse mask matrix?
        Mat                  colorROI = new Mat(); //Create masked matrix (only showing colored regions).

        Imgproc.cvtColor(ROI, hsvROI, Imgproc.COLOR_RGB2HSV_FULL); //Go from rgb to hsv of submatrix, and output an intermediate matrix.

        // Set lower and upper limits for mask.
        Scalar Regionlower_lim = new Scalar(0,80,0);
        Scalar Regionupper_lim = new Scalar(255,255,255);
        //Set mask matrix based on lower/upper limits using hsv matrix.
        Core.inRange(hsvROI, Regionlower_lim, Regionupper_lim, maskROI);
        //Set grey mask matrix.
        Core.bitwise_not(maskROI, maskGreyROI);
        //Set mask matrix to only display colored pixels.
        Core.bitwise_and(ROI, ROI, colorROI, maskROI);

        if(colorMask){
            return colorROI;
        }
        return maskROI;

    }
    public static Mat magenta(Mat ROI){
        Mat                  hsvROI = new Mat(); //create hsv matrix.
        Mat                  maskROI = new Mat(); //Create mask matrix.
        Mat                  greenROI = new Mat(); //Create masked matrix (only showing green regions).

        Imgproc.cvtColor(ROI, hsvROI, Imgproc.COLOR_RGB2HSV_FULL); //Go from rgb to hsv of submatrix, and output an intermediate matrix.

        // Set lower and upper limits for mask.
        Scalar Regionlower_lim = new Scalar(207,60,0);
        Scalar Regionupper_lim = new Scalar(218,255,255);
        //Set mask matrix based on lower/upper limits using hsv matrix.
        Core.inRange(hsvROI, Regionlower_lim, Regionupper_lim, maskROI);
        //Set mask matrix to only display colored pixels.
        Core.bitwise_and(ROI, ROI, greenROI, maskROI);
        return maskROI;
    }
    public static Mat cyan(Mat ROI){
        Mat                  hsvROI = new Mat(); //create hsv matrix.
        Mat                  maskROI = new Mat(); //Create mask matrix.
        Mat                  redROI = new Mat(); //Create masked matrix (only showing colored regions).

        Imgproc.cvtColor(ROI, hsvROI, Imgproc.COLOR_RGB2HSV_FULL); //Go from rgb to hsv of submatrix, and output an intermediate matrix.

        // Set lower and upper limits for mask.
        Scalar Regionlower_lim = new Scalar(122,60,0);
        Scalar Regionupper_lim = new Scalar(133,255,255);
        //Set mask matrix based on lower/upper limits using hsv matrix.
        Core.inRange(hsvROI, Regionlower_lim, Regionupper_lim, maskROI);
        //Set mask matrix to only display colored pixels.
        Core.bitwise_and(ROI, ROI, redROI, maskROI);
        return maskROI;
    }
    public static Mat blue(Mat ROI){
        Mat                  hsvROI = new Mat(); //create hsv matrix.
        Mat                  maskROI = new Mat(); //Create mask matrix.
        Mat                  blueROI = new Mat(); //Create masked matrix (only showing colored regions).

        Imgproc.cvtColor(ROI, hsvROI, Imgproc.COLOR_RGB2HSV_FULL); //Go from rgb to hsv of submatrix, and output an intermediate matrix.

        // Set lower and upper limits for mask.
        Scalar Regionlower_lim = new Scalar(165,60,0);
        Scalar Regionupper_lim = new Scalar(176,255,255);
        //Set mask matrix based on lower/upper limits using hsv matrix.
        Core.inRange(hsvROI, Regionlower_lim, Regionupper_lim, maskROI);
        //Set mask matrix to only display colored pixels.
        Core.bitwise_and(ROI, ROI, blueROI, maskROI);
        return maskROI;
    }
    public static Mat yellow(Mat ROI){
        Mat                  hsvROI = new Mat(); //create hsv matrix.
        Mat                  maskROI = new Mat(); //Create mask matrix.
        Mat                  yellowROI = new Mat(); //Create masked matrix (only showing colored regions).

        Imgproc.cvtColor(ROI, hsvROI, Imgproc.COLOR_RGB2HSV_FULL); //Go from rgb to hsv of submatrix, and output an intermediate matrix.

        // Set lower and upper limits for mask.
        Scalar Regionlower_lim = new Scalar(32,60,0);
        Scalar Regionupper_lim = new Scalar(38,255,255);
        //Set mask matrix based on lower/upper limits using hsv matrix.
        Core.inRange(hsvROI, Regionlower_lim, Regionupper_lim, maskROI);
        //Set mask matrix to only display colored pixels.
        Core.bitwise_and(ROI, ROI, yellowROI, maskROI);
        return maskROI;
    }
}
