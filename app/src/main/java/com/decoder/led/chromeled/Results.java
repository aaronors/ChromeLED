package com.decoder.led.chromeled;

import android.content.Context;
import android.os.Bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.String;
import java.util.List;
import java.util.Map;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

public class Results extends AppCompatActivity {
    private static final String TAG = "ResultsPage";
    boolean saved = false;
    String outString;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //bundle setup, refer between results activity and tutorial1activity
        Bundle extras = getIntent().getExtras();
        tv = (TextView) findViewById(R.id.title_textView);

        String processResults = processImages();
        String samplingResults = imageSampling(processResults, extras.getLongArray("times"));

        List<String> parseResults = msgParse(samplingResults);
        msgFix(parseResults);
        List<String> decodeResults = msgDecode(parseResults);
        outString = printOut(decodeResults);

        if (outString != "")
            tv.setText(outString);
        else
            tv.setText("We are sorry. We were unable to detect your LED signal. Please ensure that the camera and ROI are positioned as correctly as possible, and try again.");
    }

    public void saveText(View view){
        if (saved)
            return;
        String filename = "savefile";
        FileOutputStream outputStream;
        String internalString = outString + "\n";
        try{
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(internalString.getBytes());
            outputStream.close();
            tv.setText("Thank you. Your error has been saved to the error log. You can view the changes from the error log fragment of the main activity page.");
            saved = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void msgFix(List<String> inputList){

        String tempString = "";

        for(int y =0; y<inputList.size();y++){

            tempString=inputList.get(y);
            if (tempString.length()-4 < 0)
                continue;
            tempString=tempString.substring(0,tempString.length()-4);
            tempString="10"+tempString;

            inputList.set(y,tempString);
            tempString="";

        }

    }

    List<String> msgParse(String inputArr){
        List<String> dataList = new ArrayList<String>();
        String dataString = "";
        String bitSignal = "1010";
        char currChar;
        String bitWindow = "";
        String tempString = "";

        for(int x = 0; x < inputArr.length(); x++){
            Log.i(TAG, "============================================");
            currChar = inputArr.charAt(x);

            if(bitWindow.length()==4){
                if(bitWindow.equals(bitSignal)){
                    Log.i(TAG, "new String! ===============");
                    dataList.add(dataString);
                    dataString="";
                }

                tempString = bitWindow.substring(1);
                Log.i(TAG, "tempString= " + tempString);
                tempString += currChar;
                bitWindow = tempString;										 // ---
            }
            else{
                bitWindow += currChar;
            }
            dataString += currChar;
        }
        return dataList;
    }

    List<String> msgDecode(List<String> inputList){                                      // decodes the message, change to take in List<String>
        //Log.i(TAG, "msgDecode() called ---------------------------");
        String retString = "";
        String bitCap = "10";
        String bitWindow = "";
        String tempString = "";
        char currChar;
        String inputArr;
        List<String> retList = new ArrayList<String>();
        for(int y =0; y<inputList.size();y++){
            inputArr=inputList.get(y);
            retString = "";
            bitWindow = "";
            tempString = "";
            for (int x = 0; x < inputArr.length(); x++) {
                //Log.i(TAG, "============================================");
                //Log.i(TAG, "bitWindow= " + bitWindow);
                currChar = inputArr.charAt(x);                                  // get current character
                if(bitWindow.length()==2){                                       // if the number of characters in the window is 4
                    if(bitWindow.equals(bitCap)){                                // ---
                        //Log.i(TAG, "inside");
                        retString += currChar;
                    }
                    tempString = bitWindow.substring(1);                        // change
                    //Log.i(TAG, "tempString= " + tempString);
                    tempString += currChar;
                    bitWindow = tempString;										 // ---
                }
                else{
                    bitWindow += currChar;                                       // append to the end of bitWindow
                }
            }
            retList.add(retString);
        }
        return retList;
    }

    String printOut(List<String> listString){
        Log.i(TAG, "printing================================================================");
        Log.i(TAG, "listString.size()= " + listString.size());

        Map<String, Integer> freq = new HashMap<String, Integer>(); // use a hashmap to count occurances
        for(int i =0; i< listString.size(); i++){
            String currString = listString.get(i);
            Log.i(TAG, "string[" + i + "]= " + currString);

            if ((currString.length()%8) == 0 && currString.length() != 0) { // this way we know that the string captured is a multiple of one byte
                if (freq.containsKey(currString)) { // if the string is already in the hashmap, increment its counter by 1
                    freq.put(currString, freq.get(currString)+1);
                }
                else { // add string to hashmap and increment by 1
                    freq.put(currString, 1);
                }
            }
        }

        int count = 0;
        String retString = "";
        for (Map.Entry<String, Integer> entry : freq.entrySet()) { //iterate through hashmap to find the highest occurance value
            int value = entry.getValue();
            if (value > count) { // note if there are several equal number of occurances, this will take the first one
                count = value;
                retString = "Your LED encoding was: \n\n" + entry.getKey();
            }
        }
        return retString;
    }

    String imageSampling(String bitArr, long[] timeArr) {                   // takes bitArr and samples it
        String retString = "";

        int startPos = 0;
        int endPos = 0;
        int length = 0;
        long timeDiff = 0;
        double bitLen = 0;
        char currChar = bitArr.charAt(0);

        for (int x = 0; x < bitArr.length(); x++) {
            // iterate trough list
            if (currChar != bitArr.charAt(x)) {                 // if bits are not equal
                Log.i(TAG, "--------------------------------------------");

                endPos = x - 1;
                timeDiff = timeArr[endPos] - timeArr[startPos];
                length = endPos - startPos + 1;

                Log.i(TAG, "currChar= " + currChar);
                Log.i(TAG, "timeDiff= " + timeDiff);
                Log.i(TAG, "length= " + length + " samples");

                if (timeDiff <= 100) {                           // if timeDiff < 100 then its one value
                    retString += currChar;

                    Log.i(TAG, "out bit length= 1");
                } else {
                    bitLen = Math.round(timeDiff / 100d);
                    Log.i(TAG, "out bit length= " + bitLen);
                    for (int y = 0; y < bitLen; y++) {
                        retString += currChar;
                    }
                }
                startPos = x;
                currChar = bitArr.charAt(x);                 // set vars for next loop
            }
        }
        return retString;
    }

    // this function takes in the array of preprocessed samples from the previous activity, then applies contour finding to determine if the LED was on/off, the return value will be a string of raw oversampled data.
    String processImages() {
        String retString = "";

        int numFrames = DetectActivity.images.size();
        for (int i = 0; i < numFrames; i++) {
            Mat mHierarchy = new Mat();
            List<MatOfPoint> contours = new ArrayList<>();
            Mat myMat = DetectActivity.images.get(i);
            Imgproc.findContours(myMat, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            if (contours.size() > 0)
                retString += "1";
            else
                retString += "0";
        }
        return retString;
    }
}
