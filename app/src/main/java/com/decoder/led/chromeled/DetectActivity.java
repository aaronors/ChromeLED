package com.decoder.led.chromeled;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.widget.Toast;
import android.util.Log;
import java.util.ArrayList;

public class DetectActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVLed::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;

    //*** PROCESSING VARIABLES DEFINITIONS BEGIN ***//
    int THRESHOLD = 200; //pixels above this threshold will display as black (used by Imgproc.threshold())
    int MAX = 255; //maximum possible pixel intensity (used by Imgproc.threshold())
    Mat myImage, croppedImg; //openCV matrix representing an image; used for processing frames retrieved from camera
    int WIDTH, HEIGHT; // used for dimension of screen and images
    public static ArrayList<Mat> images = new ArrayList<Mat>(); // public static allows this to be accessed from other activities

    boolean screenTap = false; // used for drawing ROI, procesing, and setting toast
    float x1, y1, x2, y2;
    Point rectPoint1, rectPoint2; // used to draw ROI in onCameraFrame; initialized in onTouch
    Scalar redOutline = new Scalar(255, 0, 0); // used to draw ROI
    Rect subRectangle; // used for ROI

    int MAXBUFFSIZE = 512; // must be before declaration of times[]; controls number of timestamps and frames before trying to detect
    int MAXSIGNALTIME = 100; // amount of time signal is high, used to determine if a frame was missed
    int MISSEDFRAMESIZE = 50; // max number of missed frames to store
    long diff; // used to report the difference between timestamps
    long times[] = new long[MAXBUFFSIZE]; // long array of timestamps
    int timeIndex = 0; // start index at 0, used to iterate through time[]
    int missedFrames[] = new int[MISSEDFRAMESIZE]; // int array of index where a miss occurred so index can be used to try to recover from repeated signal
    int missedFrameIndex=0;
    //*** PROCESSING VARIABLES DEFINITIONS END ***//
    Toast toaster;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.detect_surface_view);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.detect_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        toaster = Toast.makeText(this,"", Toast.LENGTH_LONG);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        toaster.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            reset();
            screenTap = false;
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        toaster.cancel();
    }

    public void onCameraViewStarted(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
    }

    public void onCameraViewStopped() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
        }

        //grab points where screen is touched to use as corners for rectangle based on dimensions of phone screen

        x1=event.getX() + (HEIGHT + WIDTH)/16;
        y1=event.getY() + (HEIGHT + WIDTH)/16;
        x2=event.getX() - (HEIGHT + WIDTH)/16;
        y2=event.getY() - (HEIGHT + WIDTH)/16;

        if (x1 <= 0) {
            x1 = 1;
        }
        if (x2 <= 0) {
            x2 = 1;
        }
        if (y1 <= 0) {
            y1 = 1;
        }
        if (y2 <= 0) {
            y2 = 1;
        }

        if (x1 >= WIDTH) {
            x1 = WIDTH-1;
        }
        if (x2 >= WIDTH) {
            x2 = WIDTH-1;
        }
        if (y1 >= HEIGHT) {
            y1 = HEIGHT-1;
        }
        if (y2 >= HEIGHT) {
            y2 = HEIGHT-1;
        }

        rectPoint1=new Point(x1,y1);
        rectPoint2=new Point(x2,y2);
        subRectangle = new Rect(rectPoint1,rectPoint2);

        //set bool to draw ROI in onCameraFrame
        if(event.getAction() == android.view.MotionEvent.ACTION_UP) {
            if (!screenTap) {
                screenTap = true;
                //Toast.makeText(getApplicationContext(), "ROI Selected. Now processing . . .", Toast.LENGTH_LONG).show();
                toaster.setText("ROI Selected. Now processing . . .");
                toaster.show();
            } else if (screenTap) {
                //Toast.makeText(getApplicationContext(), "New ROI Selected. Restarted processing . . .", Toast.LENGTH_LONG).show();
                toaster.setText("New ROI Selected. Restarted processing . . .");
                toaster.show();
            }
        }
        reset();
        return false;
    }

    public Void reset() {
        timeIndex = 0;
        images.clear();
        missedFrameIndex=0;
        return null;
    }

    public Void processIntent() {
        //toaster.setText(MAXBUFFSIZE + " SAMPLES COLLECTED. ATTEMPTING TO PROCESS, PLEASE WAIT.");
        //toaster.show();
        Intent myIntent = new Intent(DetectActivity.this, Results.class);
        myIntent.putExtra("times", times);
        myIntent.putExtra("missedFrames", missedFrames);
        myIntent.putExtra("missedFrameIndex", missedFrameIndex);
        DetectActivity.this.startActivity(myIntent);
        // NOTE: thread may not be fully suspended yet, so this may still be running while calling next intent and could cause out of bounds array exception
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //if we think we have enough samples, call the next intent to process and pass it necessary values
        if (timeIndex == MAXBUFFSIZE) {
            processIntent();
        }

        myImage = inputFrame.rgba();

        // if touch screen draw rectangle on frame which will be the ROI, process, and store timestamp
        if (screenTap && timeIndex < MAXBUFFSIZE) {
            times[timeIndex] = SystemClock.elapsedRealtimeNanos()/1000000; //get the time when we capture a frame; apparently elapsedRealtimeNano requires >= API 17

            Imgproc.rectangle(myImage, rectPoint1, rectPoint2, redOutline, 5); // draw red ROI rectangle

            //get just the ROI, then grayscale and threshold so more memory efficient (need to store many matrices into arrayList)
            croppedImg = myImage.submat(subRectangle);
            Imgproc.cvtColor(croppedImg, croppedImg, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(croppedImg, croppedImg, THRESHOLD, MAX, Imgproc.THRESH_BINARY);
            images.add(croppedImg.clone()); // may need to clone to actually get the image

            if (timeIndex > 1) { //only take the difference if we have a time already
                diff = ((times[timeIndex] - times[timeIndex - 1]));
                if (diff >= MAXSIGNALTIME && missedFrameIndex < MISSEDFRAMESIZE) { // check if we missed a frame based on delay; check if enough space in missedFrames
                    missedFrames[missedFrameIndex] = timeIndex;
                    missedFrameIndex++;
                }
                else if (missedFrameIndex > MISSEDFRAMESIZE) {
                    toaster.setText("WARNING YOUR PHONE MAY NOT BE CAPABLE OF SAMPLING QUICKLY ENOUGH. OVER" + MISSEDFRAMESIZE + "FRAMES HAVE BEEN MISSED");
                    toaster.show();
                }
            }
            timeIndex++; // very last thing we should do
        }
        return myImage;
    }

}
