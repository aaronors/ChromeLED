package com.decoder.led.chromeled;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.widget.Toast;


public class DetectActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVLed::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemResults;

    //*** PROCESSING VARIABLES DEFINITIONS BEGIN ***//
    int THRESHOLD = 200; //pixels above this threshold will display as black (used by Imgproc.threshold())
    int MAX = 255; //maximum possible pixel intensity (used by Imgproc.threshold())
    Mat myImage; //openCV matrix representing an image; used for processing frames retrieved from camera
    int COUNT, WIDTH, HEIGHT; //used to calculate of number of pixels above threshold
    public boolean first = true;
    Mat prev;
    private boolean screenTap = false;
    int tapCnt = 0;
    private boolean flag = true;
    int xPos;
    int yPos;
    Point rectPoint1;
    Point rectPoint2;
    Mat subImage;
    Mat mask;

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemResults  = menu.add("Results");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        //intent declaration to switch activities
        int[] arry = new int[256];
        double rand = Math.random();
        String randoLondo = "";
        int x;

        randoLondo = "0100100001100101011100100110010100100000011010010111001100100000011000010010000000110011001100100010000001100010011110010111010001100101001000000110110101100101011100110111001101100001011001110110010100100001001000010010000100100001001000010010000100100001";
        //randoLondo = "1110010010010101010101100100001101011110111010101001000101010100010101110000101000110001010110010000111111001010011000100110111100001011111111000100101001111001011110100111011101110111101011111010100101000001101001111111110000110111001000010100000111000001";
        if (item == mItemResults) {
            Intent myintent = new Intent(DetectActivity.this, Results.class);
            myintent.putExtra("Message", "Results have been passed!");
            myintent.putExtra("Array", arry);
            myintent.putExtra("bitString", randoLondo); //randoLondo is placeholder for input bit string
            //startActivity(myintent);
            DetectActivity.this.startActivity(myintent);
        }
        return true;
    }

    //*** PROCESSING VARIABLES DEFINITIONS END ***//

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.detect_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.detect_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            //Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            // Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
    }

    public void onCameraViewStopped() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xPos = (int)event.getX();
        yPos = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
        }
        //Toast.makeText(Tutorial1Activity.this, "xPos= "+xPos+","+"yPos= "+yPos, Toast.LENGTH_SHORT).show();
        screenTap = true;

        return false;
    }


    // screenTap

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        //DECLARATIONS
        COUNT=0;
        myImage = inputFrame.rgba();
        Mat origImage = inputFrame.rgba();

        /*** MOVING THESE OUT OF THIS FUNCTION CAUSES SEGFAULT ***/
        Scalar CONTOUR_COLOR = new Scalar(0,255,0);
        Mat mHierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        //---------------------------
        //      Image Processing
        //---------------------------

        if(flag) {
            subImage = Mat.zeros(myImage.size(), myImage.type());
            mask = Mat.zeros(myImage.size(), myImage.type());
            flag = false;
        }

        // if touch screen draw rectangle on frame which will be the ROI
        if(screenTap){
            rectPoint1=new Point(xPos+200,yPos+200);
            rectPoint2=new Point(xPos-200,yPos-200);

            Imgproc.rectangle(mask,rectPoint1,rectPoint2,new Scalar(255,255,255),-1,8,0);
            Imgproc.rectangle(origImage,rectPoint1,rectPoint2,new Scalar(255,0,0),5);

            myImage.copyTo(subImage,mask);

            Imgproc.cvtColor(subImage, subImage, Imgproc.COLOR_RGB2GRAY);

            Imgproc.threshold(subImage, subImage, THRESHOLD, MAX, Imgproc.THRESH_BINARY);

            Imgproc.findContours(subImage, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            Imgproc.drawContours(origImage,contours,-1,CONTOUR_COLOR,7);

            Log.i(TAG, "Count = " + contours.size());
        }

        return origImage;

    }
}
