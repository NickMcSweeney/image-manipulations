package org.opencv.samples.imagemanipulations;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by sensor on 3/15/2016.
 */
public class HistCalc {

    //Find Hue of a specified region of the camera.
    public static Mat findHist(Mat rgba, Mat rgbROI, boolean RGB, boolean value, boolean hue, int xStart){

        /**************************/
        int                  mHistSizeNum = 25; //Set Resolution of Hue Histogram.
        MatOfInt mChannels[] = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };//Matrice for color channels.
        MatOfInt             mHistSize = new MatOfInt(mHistSizeNum); //Matrice for size of histogram.
        MatOfFloat           mRanges = new MatOfFloat(0f, 256f); //Range of colors.
        Scalar               mColorsRGB[] = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) }; //Define Red, Green, Blue numerical color values.
        Scalar               mColorsHue[] = new Scalar[] {
                new Scalar(255, 0, 0, 255),   new Scalar(255, 60, 0, 255),  new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),
                new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255),  new Scalar(20, 255, 0, 255),  new Scalar(0, 255, 30, 255),
                new Scalar(0, 255, 85, 255),  new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),
                new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255),  new Scalar(0, 0, 255, 255),   new Scalar(64, 0, 255, 255),  new Scalar(120, 0, 255, 255),
                new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255),  new Scalar(255, 0, 0, 255)
        }; //Define hue numerical values.
        Scalar               mWhilte = Scalar.all(255); //Set value of White.
        Point                mP1 = new Point(); //Point used in displaying the histogram.
        Point                mP2 = new Point(); //Point used in displaying the histogram.
        Point                mP3 = new Point();
        float                mBuff[] = new float[mHistSizeNum]; //Buffer?
        String               mostFreqVal; // Most often occuring hue.
        Mat                  histReturnMat = new Mat();
        Size                 sizeRgba = rgba.size();
        Size                 sizeROI  = rgbROI.size();
        Mat                  mIntermediateMat = new Mat(); //This is an intermediate matrice for basic openCV function call outputs.
        Mat                  hsvROI = new Mat(); //create hsv matrix.
        Mat                  maskROI = new Mat();
        /**************************/

        Imgproc.cvtColor(rgbROI, hsvROI, Imgproc.COLOR_RGB2HSV_FULL);

        //Call Masking Method
        maskROI = Mask.getMask(rgbROI, false);

        String vals[] =  HSV_RGB_Num(rgbROI, hsvROI, maskROI);

        //draw a rectangle around the ROI
        Imgproc.rectangle(rgbROI, new Point(1, 1), new Point(sizeROI.width - 2, sizeROI.height - 2), new Scalar(0, 0, 0, 255), 2);

        int thiknessZoom = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
        int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
        if (thiknessZoom > 5) thikness = 5;
        int offsetZoom = (int) ((sizeRgba.width - (5 * mHistSizeNum + 4 * 10) * thiknessZoom) / 2);


        // RGB
        if(RGB) {
            for (int c = 0; c < 3; c++) {
                Imgproc.calcHist(Arrays.asList(rgbROI), mChannels[c], maskROI, histReturnMat, mHistSize, mRanges);
                Core.normalize(histReturnMat, histReturnMat, sizeRgba.height / 3, 0, Core.NORM_INF);
                histReturnMat.get(0, 0, mBuff);
                for (int h = 0; h < mHistSizeNum; h++) {
                    mP1.x = mP2.x = offsetZoom + (c * (mHistSizeNum + 10) + h) * thiknessZoom;
                    mP1.y = sizeRgba.height - 1;
                    mP2.y = mP1.y - 2 - (int) mBuff[h];
                    Imgproc.line(rgba, mP1, mP2, mColorsRGB[c], thiknessZoom);
                }
                mP3.x = offsetZoom + (c * (mHistSizeNum + 10)) * thiknessZoom;
                mP3.y = 2*sizeRgba.height / 3;
                mostFreqVal = (vals[c+3]);
                Imgproc.putText(rgba,mostFreqVal,mP3,1,2.0,new Scalar(255, 255, 255, 0),2);
            }
        }
        // Saturation
        if(value) {
            Imgproc.calcHist(Arrays.asList(hsvROI), mChannels[1], maskROI, histReturnMat, mHistSize, mRanges);
            Core.normalize(histReturnMat, histReturnMat, sizeRgba.height / 3, 0, Core.NORM_INF);
            histReturnMat.get(0, 0, mBuff);
            for (int h = 0; h < mHistSizeNum; h++) {
                mP1.x = mP2.x = offsetZoom + (3 * (mHistSizeNum + 10) + h) * thiknessZoom;
                mP1.y = sizeRgba.height - 1;
                mP2.y = mP1.y - 2 - (int) mBuff[h];
                Imgproc.line(rgba, mP1, mP2, mWhilte, thiknessZoom);
            }

            mP3.x = offsetZoom + (3 * (mHistSizeNum + 10)) * thiknessZoom;
            mP3.y = 2*sizeRgba.height / 3;
            mostFreqVal = (vals[2]);
            Imgproc.putText(rgba,mostFreqVal,mP3,1,2.0,new Scalar(255, 255, 255, 0),2);
        }
        // Hue
        if(hue) {
            if(!RGB&&!value){
                Imgproc.calcHist(Arrays.asList(hsvROI), mChannels[0], maskROI, histReturnMat, mHistSize, mRanges);
                Core.normalize(histReturnMat, histReturnMat, sizeRgba.height / 4, 0, Core.NORM_INF);
                histReturnMat.get(0, 0, mBuff);
                for (int h = 0; h < mHistSizeNum; h++) {
                    mP1.x = mP2.x = xStart+(h*thiknessZoom);
                    mP1.y = sizeRgba.height - 1;
                    mP2.y = mP1.y - 2 - (int) mBuff[h];
                    Imgproc.line(rgba, mP1, mP2, mColorsHue[h], thiknessZoom);

                }

                mP3.x = xStart;
                mP3.y = 2*sizeRgba.height / 3;
                mostFreqVal = (vals[0]);
                Imgproc.putText(rgba,mostFreqVal,mP3,1,2.0,new Scalar(255, 255, 255, 0),2);


            }
            else {
                Imgproc.calcHist(Arrays.asList(hsvROI), mChannels[0], maskROI, histReturnMat, mHistSize, mRanges);
                Core.normalize(histReturnMat, histReturnMat, sizeRgba.height / 3, 0, Core.NORM_INF);
                histReturnMat.get(0, 0, mBuff);
                for (int h = 0; h < mHistSizeNum; h++) {
                    mP1.x = mP2.x = offsetZoom + (4 * (mHistSizeNum + 10) + h) * thiknessZoom;
                    mP1.y = sizeRgba.height - 1;
                    mP2.y = mP1.y - 2 - (int) mBuff[h];
                    Imgproc.line(rgba, mP1, mP2, mColorsHue[h], thiknessZoom);
                }
                mP3.x = offsetZoom + (4 * (mHistSizeNum + 10)) * thiknessZoom;
                mP3.y = 2*sizeRgba.height / 3;
                mostFreqVal = (vals[0]);
                Imgproc.putText(rgba,mostFreqVal,mP3,1,2.0,new Scalar(255, 255, 255, 0),2);


            }
        }

        rgbROI.release();
        return rgbROI;
    }

    private static String[] HSV_RGB_Num(Mat rgbROI, Mat hsvROI, Mat maskROI){ //This Function creates a double & a string array of all the most often occuring HSV&RGB values in an ROI and outputs a string array.

        /**************************/
        int                  mHistSizeNumR = 360;
        MatOfInt mChannels[] = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };//Matrice for color channels.
        MatOfInt             mHistSize_highRes = new MatOfInt(mHistSizeNumR);
        MatOfFloat           mRanges = new MatOfFloat(0f, 256f); //Range of colors.
        Scalar               mColorsHue[] = new Scalar[] {
                new Scalar(255, 0, 0, 255),   new Scalar(255, 60, 0, 255),  new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),
                new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255),  new Scalar(20, 255, 0, 255),  new Scalar(0, 255, 30, 255),
                new Scalar(0, 255, 85, 255),  new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),
                new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255),  new Scalar(0, 0, 255, 255),   new Scalar(64, 0, 255, 255),  new Scalar(120, 0, 255, 255),
                new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255),  new Scalar(255, 0, 0, 255)
        }; //Define hue numerical values.
        float                mBuff_highRes[] = new float[mHistSizeNumR];
        Mat                  histReturnMat_highRes = new Mat();
        /**************************/

        double colorVals[] = new double[6];
        String colorVals_str[] = new String[6];
        /*
        This creates an array that stores the color values for an ROI.
        colorVals[0] = the hue of the ROI
        colorVals[1] = the saturation of the ROI
        colorVals[2] = the value of the ROI
        colorVals[3] = the Red value of the ROI
        colorVals[4] = the Blue value of the ROI
        colorVals[5] = the Green value of the ROI
        */
        /******************************************/
        DecimalFormat RGB_format = new DecimalFormat("#000");
        DecimalFormat HSV_format = new DecimalFormat("#0.00");
        /******************************************/

        //Hue
        mHistSizeNumR = 360;
        Imgproc.calcHist(Arrays.asList(hsvROI), mChannels[0], maskROI, histReturnMat_highRes, mHistSize_highRes
                , mRanges);
        histReturnMat_highRes.get(0, 0, mBuff_highRes);
        float hueNum;
        hueNum = mBuff_highRes[0];
        int index = 0;
        for(int i=0; i<mHistSizeNumR;i++){
            if(hueNum<mBuff_highRes[i]){
                hueNum = mBuff_highRes[i];
                index = i;
            }
        }
        colorVals[0] = (double)index/(mHistSizeNumR-1);
        colorVals_str[0] = ""+HSV_format.format(colorVals[0]);
        //Saturation
        mHistSizeNumR = 255;
        Imgproc.calcHist(Arrays.asList(hsvROI), mChannels[1], maskROI, histReturnMat_highRes, mHistSize_highRes
                , mRanges);
        histReturnMat_highRes.get(0, 0, mBuff_highRes);
        hueNum = mBuff_highRes[0];
        index = 0;
        for(int i=0; i<mHistSizeNumR;i++){
            if(hueNum<mBuff_highRes[i]){
                hueNum = mBuff_highRes[i];
                index = i;
            }
        }
        colorVals[1] = (double)index/(mHistSizeNumR-1);
        colorVals_str[1] = ""+HSV_format.format(colorVals[1]);
        //Value
        mHistSizeNumR = 360;
        Imgproc.calcHist(Arrays.asList(hsvROI), mChannels[2], maskROI, histReturnMat_highRes, mHistSize_highRes
                , mRanges);
        histReturnMat_highRes.get(0, 0, mBuff_highRes);
        hueNum = mBuff_highRes[0];
        index = 0;
        for(int i=0; i<mHistSizeNumR;i++){
            if(hueNum<mBuff_highRes[i]){
                hueNum = mBuff_highRes[i];
                index = i;
            }
        }
        colorVals[2] = (double)index/(mHistSizeNumR-1);
        colorVals_str[2] = ""+HSV_format.format(colorVals[2]);
        //Red
        mHistSizeNumR = 255;
        Imgproc.calcHist(Arrays.asList(rgbROI), mChannels[0], maskROI, histReturnMat_highRes, mHistSize_highRes
                , mRanges);
        histReturnMat_highRes.get(0, 0, mBuff_highRes);
        hueNum = mBuff_highRes[0];
        index = 0;
        for(int i=0; i<mHistSizeNumR;i++){
            if(hueNum<mBuff_highRes[i]){
                hueNum = mBuff_highRes[i];
                index = i;
            }
        }
        colorVals[3] = (double)index;
        colorVals_str[3] = ""+RGB_format.format(colorVals[3]);
        //Blue
        Imgproc.calcHist(Arrays.asList(rgbROI), mChannels[1], maskROI, histReturnMat_highRes, mHistSize_highRes
                , mRanges);
        histReturnMat_highRes.get(0, 0, mBuff_highRes);
        hueNum = mBuff_highRes[0];
        index = 0;
        for(int i=0; i<mHistSizeNumR;i++){
            if(hueNum<mBuff_highRes[i]){
                hueNum = mBuff_highRes[i];
                index = i;
            }
        }
        colorVals[4] = (double)index;
        colorVals_str[4] = ""+RGB_format.format(colorVals[4]);
        //Green
        Imgproc.calcHist(Arrays.asList(rgbROI), mChannels[2], maskROI, histReturnMat_highRes, mHistSize_highRes
                , mRanges);
        histReturnMat_highRes.get(0, 0, mBuff_highRes);
        hueNum = mBuff_highRes[0];
        index = 0;
        for(int i=0; i<mHistSizeNumR;i++){
            if(hueNum<mBuff_highRes[i]){
                hueNum = mBuff_highRes[i];
                index = i;
            }
        }
        colorVals[5] = (double)index;
        colorVals_str[5] = ""+RGB_format.format(colorVals[5]);
        return colorVals_str;
    }

}
