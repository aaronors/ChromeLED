package com.decoder.led.chromeled;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.String;
import java.util.List;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;


public class Results extends AppCompatActivity {
    private static final String TAG = "ResultsPage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HashMap<String, String> key = new HashMap<String, String>();
        key.put("0000", "0");
        key.put("0001", "1");
        key.put("0010", "2");
        key.put("0011", "3");
        key.put("0100", "4");
        key.put("0101", "5");
        key.put("0110", "6");
        key.put("0111", "7");
        key.put("1000", "8");
        key.put("1001", "9");
        key.put("1010", "A");
        key.put("1011", "B");
        key.put("1100", "C");
        key.put("1101", "D");
        key.put("1110", "E");
        key.put("1111", "F");

        //bundle setup, refer between results activity and tutorial1activity
        Bundle extras = getIntent().getExtras();
        TextView tv = (TextView)findViewById(R.id.textView_activity_results);
        tv.setText(processImages(extras.getLongArray("times"), extras.getInt("timeIndex"), extras.getBoolean("missedFrame"), extras.getIntArray("missedFrames"), extras.getInt("missedFrameIndex")));
    }

    String processImages(long[] times, int numTimes, boolean missedFrame, int[] missedFrames, int missedFrameIndex) {
        String retString = "";

        if (missedFrame) {
            for (int i=0; i < missedFrameIndex; i++) {
                Log.i(TAG, "Missed frame " + missedFrames[i]);
            }
        }

        for (int i=0; i < numTimes; i++) {
            long myTimestamp = times[i];

            Mat mHierarchy = new Mat();
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Scalar CONTOUR_COLOR = new Scalar(0,255,0);

            if (DetectActivity.images.size() != 0) {
                Mat myMat = DetectActivity.images.get(i);
                Imgproc.findContours(myMat, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            }

            if(contours.size() > 0)
                retString += "1";
            else
                retString += "0";
            Log.i(TAG, "retString = " + retString + ", times[" + i + "] = " + times[i] + ", numContours = " + contours.size());
        }
        return retString;
    }
}
