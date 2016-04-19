package org.opencv.samples.imagemanipulations;

//Import Packages
//Java Packages
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
//OpenCv Packages
import android.view.*;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
//Android Packages
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 *
 * This App accesses the camera of an android phone through opencv and displays the hue inside a specified region
 * Chemical Sensors Lab 2015
 * Cantrell
 *
 */
public class ImageManipulationsActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {

    private static final String  TAG                 = "ChemSensor::Activity"; //tag for messages outputted to the log
    /*
    Menu Key
    RGBA:       Displays a plain camera view.
    Hist:       Displays a histogram based on the entire camera view.
    StaticROI:  Displays a histogram based on a static ROI that is in the center of the screen.
    Whatman:    Displays a histogram for the 4 ROI's and a white ballance for a Whatman Ph strip.
     */

    //Declare the variables for connecting the menu button to the code it implements.
    public static final int      VIEW_MODE_RGBA         = 0;
    public static final int      VIEW_MODE_HIST         = 1;
    public static final int      VIEW_MODE_STATIC_ROI   = 2;
    public static final int      VIEW_MODE_CONTOURS      = 3;
    public static final int      VIEW_MODE_MASK         = 4;
    public static final int      VIEW_MODE_BLOBDETECT   = 5;

    //Declare menu button variables.
    private MenuItem             mItemPreviewRGBA;
    private MenuItem             mItemPreviewHist;
    private MenuItem             mItemPreviewStatic;
    private MenuItem             mItemPreviewContours;
    private MenuItem             mItemPreviewMask;
    private MenuItem             mItemPreviewBlobDetect;

    //Create instance of CameraBridgeViewBase from the openCV library and name it mOpenCVCameraView.
    private CameraBridgeViewBase mOpenCvCameraView;

    // Create a size variable for ...
    private Size                 mSize0;

    // Initialize Matricies.



    private Mat                  zoomCorner;
    private Mat                  mZoomWindow;
    private Mat                  rgbROI;
    private Mat                  rgbBlob;
    private List<MatOfPoint>     mContours;
    private Size                 wsize;
    private Mat                  mIntermediateMat; //This is an intermediate matrice for basic openCV function call outputs.
    /* COLOR BLOB DEC  */
    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;
        /* END */


    public static int           viewMode = VIEW_MODE_RGBA; //Set default view mode to RGBA (no analysis of image).

    //Load openCV library, enables camera view.
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(ImageManipulationsActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    // Outputs message to log when new activity starts.
    public ImageManipulationsActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.image_manipulations_surface_view); //DIFERENT FROM BLOB DETECT

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()//Action when app is paused.
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()//Action when unPaused
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() { //Action when app is quit.
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    //Create Options Menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemPreviewRGBA  = menu.add("Preview RGBA");
        mItemPreviewStatic  = menu.add("Histogram (Static)");
        mItemPreviewHist  = menu.add("Histogram");
        mItemPreviewContours = menu.add("Contours");
        mItemPreviewMask = menu.add("Check Mask");
        mItemPreviewBlobDetect = menu.add("Blob Detector");

        return true;
    }

    //Points Options Menu Button to corresponding code.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemPreviewRGBA)
            viewMode = VIEW_MODE_RGBA;
        if (item == mItemPreviewHist)
            viewMode = VIEW_MODE_HIST;
        else if (item == mItemPreviewContours)
            viewMode = VIEW_MODE_CONTOURS;
        else if (item == mItemPreviewMask)
            viewMode = VIEW_MODE_MASK;
        else if (item == mItemPreviewBlobDetect)
            viewMode = VIEW_MODE_BLOBDETECT;
        else if (item == mItemPreviewStatic)
            viewMode = VIEW_MODE_STATIC_ROI;
        return true;
    }

    // Create Camera View set values of variables.
    public void onCameraViewStarted(int width, int height) {
        mIntermediateMat = new Mat();
        mSize0 = new Size();
        mContours = new ArrayList<MatOfPoint>();

        // BLOB DETECTOR
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(0, 100, 0, 255);;
        // END

    }

    public void onCameraViewStopped() { //When camera stops.
        // Explicitly deallocate Mats
        if (mIntermediateMat != null)
            mIntermediateMat.release();

        mIntermediateMat = null;
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(rgbROI, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        mIsColorSelected = true;

        touchedRegionHsv.release();
        touchedRegionRgba.release();

        return false; // don't need subsequent touch events
    }

    public Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    //Actions in camera frame (main code).
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba(); //Camera.
        Size sizeRgba = rgba.size();
        Mat mRoi = new Mat();

        Mat mPyrDownMat = new Mat();//temp for blob
        Mat mHsvMat = new Mat();//temp for blob

        Mat rgbaInnerWindow;

        int rows = (int) sizeRgba.height;
        int cols = (int) sizeRgba.width;

        int left = cols / 8;
        int top = rows / 8;

        int width = cols * 3 / 4;
        int height = rows * 3 / 4;

        //Declare base dimensions for the submatrix (full screen).
        int x1 = 0;
        int x2 = cols;
        int y1 = 0;
        int y2 = rows;
        rgbROI = rgba.submat(y1, y2, x1, x2); //create submatrix rgbROI that is the region of interest for finding hue etc...

        int num = rows/16;
        int xStart = rows/16;



        switch (ImageManipulationsActivity.viewMode) { //Switch between view modes when the corresponding button is pressed.

            //Plain RGBA view mode (no manipulation).
            case ImageManipulationsActivity.VIEW_MODE_RGBA:
                break;

            //Display Histogram based on the entire camera view.
            case ImageManipulationsActivity.VIEW_MODE_HIST:
                HistCalc.findHist(rgba, rgbROI, true, true, true, 0);
                break;

            //Display Histogram based on a static rectangle in the middle of the screen.
            case ImageManipulationsActivity.VIEW_MODE_STATIC_ROI:

                x1 = cols / 2 - 9 * cols / 100;
                x2 = cols / 2 + 9 * cols / 100;
                y1 = rows / 2 - 9 * rows / 100;
                y2 = rows / 2 + 9 * rows / 100;
                rgbROI = rgba.submat(y1, y2, x1, x2);
                HistCalc.findHist(rgba, rgbROI, true, true, true, 1);

                break;

            //Display Histogram for the 4 boxes and a white balance area on a Whatman pH strip.
            case ImageManipulationsActivity.VIEW_MODE_CONTOURS:
                //Blob detection Hue
                mRgba = inputFrame.rgba();

                FindContours mContour = new FindContours();
                mContour.detect(mRgba);
                List<MatOfPoint> contours = mContour.getContours();
                Log.e(TAG, "Contours count: " + contours.size());
                Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

                zoomCorner = rgba.submat(0, rows / 2 - rows / 10, 0, cols / 2 - cols / 10);
                mZoomWindow = rgba.submat(rows / 2 - 9 * rows / 100, rows / 2 + 9 * rows / 100, cols / 2 - 9 * cols / 100, cols / 2 + 9 * cols / 100);
                mRoi = Mask.getMask(mZoomWindow, true);
                Imgproc.resize(mRoi, zoomCorner, zoomCorner.size());
                wsize = mZoomWindow.size();
                zoomCorner.release();
                mZoomWindow.release();

                break;

            //Empty view mode.
            case ImageManipulationsActivity.VIEW_MODE_MASK:
                x1 = cols / 2 - 9 * cols / 100;
                x2 = cols / 2 + 9 * cols / 100;
                y1 = rows / 2 - 9 * rows / 100;
                y2 = rows / 2 + 9 * rows / 100;
                rgbROI = rgba.submat(y1, y2, x1, x2);
                HistCalc.findHist(rgba, rgbROI, true, true, true, 1);

                mRoi = Mask.getMask(rgbROI, true);

                zoomCorner = rgba.submat(0, rows / 2 - rows / 10, 0, cols / 2 - cols / 10);
                mZoomWindow = rgba.submat(rows / 2 - 9 * rows / 100, rows / 2 + 9 * rows / 100, cols / 2 - 9 * cols / 100, cols / 2 + 9 * cols / 100);
                mRoi = Mask.getMask(mZoomWindow, true);
                Imgproc.resize(mRoi, zoomCorner, zoomCorner.size());
                wsize = mZoomWindow.size();
                zoomCorner.release();
                mZoomWindow.release();

                break;

            //Test view mode.
            case ImageManipulationsActivity.VIEW_MODE_BLOBDETECT:

                mRgba = inputFrame.rgba();

                //Blob detection Hue

                FindContours mShape = new FindContours();
                Mat maskImg = new Mat();

                maskImg = Mask.cyan(inputFrame.rgba());
                List<MatOfPoint> cont1 = mShape.maskDetect(maskImg);
                Imgproc.drawContours(mRgba, cont1, -1, CONTOUR_COLOR);

                maskImg = Mask.magenta(inputFrame.rgba());
                List<MatOfPoint> cont2 = mShape.maskDetect(maskImg);
                Imgproc.drawContours(mRgba, cont2, -1, CONTOUR_COLOR);

                maskImg = Mask.blue(inputFrame.rgba());
                List<MatOfPoint> cont3 = mShape.maskDetect(maskImg);
                Imgproc.drawContours(mRgba, cont3, -1, CONTOUR_COLOR);

                maskImg = Mask.yellow(inputFrame.rgba());
                List<MatOfPoint> cont4 = mShape.maskDetect(maskImg);
                Imgproc.drawContours(mRgba, cont4, -1, CONTOUR_COLOR);

                break;
                //END


        }

        return rgba;
    }
}
