package com.decoder.led.chromeled;

import android.content.Context;
import android.os.Bundle;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.String;
import java.util.List;
import java.util.Map;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

        /*HashMap<String, String> key = new HashMap<String, String>();
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
        key.put("1111", "F");*/

        //bundle setup, refer between results activity and tutorial1activity
        Bundle extras = getIntent().getExtras();
        TextView tv = (TextView) findViewById(R.id.textView_activity_results);
        String diffStr = "";
        String bitStr = "";

        diffStr = long2str(extras.getLongArray("diff"), extras.getInt("diffIndex"));
        bitStr = bit2com(extras.getLongArray("times"), extras.getInt("timeIndex"));


        //tv.setText("Raw Data");
        //tv.setText(processImages(extras.getLongArray("times"), extras.getInt("timeIndex"), extras.getBoolean("missedFrame"), extras.getIntArray("missedFrames"), extras.getInt("missedFrameIndex")));
        //tv.setText("-----");
        //tv.setText("Sampled Data");

        //Log.i(TAG, "bitStr  = " + bitStr);
        //Log.i(TAG, "diffStr = " + diffStr);

        String processResults = processImages();

        String samplingResults = imageSampling(processResults, extras.getLongArray("times"));

        List<String> parseResults = msgParse(samplingResults);

        Log.i(TAG, "parseResults output");
        //String outString = printOut(parseResults);


        msgFix(parseResults);
        Log.i(TAG, "msgFix output");
        //outString = printOut(parseResults);

        List<String> decodeResults = msgDecode(parseResults);


        Log.i(TAG, "decodeResults output");
        //outString = "Sampled Data" + "\n\n" + samplingResults + "\n\n" + "YOUR MESSAGE: " + printOut(decodeResults);
        String outString = "YOUR MESSAGE WAS: " + printOut(decodeResults);
        tv.setText(outString);
    }



    void msgFix(List<String> inputList){

        String tempString = "";

        for(int y =0; y<inputList.size();y++){

            tempString=inputList.get(y);
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
                retString = entry.getKey();
            }
        }

        return retString;
    }




    String imageSampling(String bitArr, long[] timeArr) {                   // takes bitArr and samples it
        String retString = "";


        String chunk = "";
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


    String processImages() {                   // takes images and gets bitArr
        String retString = "";

        int numFrames = DetectActivity.images.size();
        for (int i = 0; i < numFrames; i++) {

            Mat mHierarchy = new Mat();
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat myMat = DetectActivity.images.get(i);
            Imgproc.findContours(myMat, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            if (contours.size() > 0)
                retString += "1";
            else
                retString += "0";
            //Log.i(TAG, "retString = " + retString + ", times[" + i + "] = " + times[i] + ", numContours = " + contours.size());
        }

        return retString;
    }

    String long2str(long[] frameDiff, int ind) {                        // make first one zero add diff to zero for 2nd index
        String retString = "";
        long total = 0;

        for (int i = 0; i < ind; i++) {

            total = total + frameDiff[i];

            String strLong = Long.toString(total);

            retString += strLong + ",";
        }


        return retString;
    }

    String bit2com(long[] times, int numTimes) {
        String retString = "";


        for (int i = 0; i < numTimes; i++) {
            long myTimestamp = times[i];

            Mat mHierarchy = new Mat();
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Scalar CONTOUR_COLOR = new Scalar(0, 255, 0);

            if (DetectActivity.images.size() != 0) {
                Mat myMat = DetectActivity.images.get(i);
                Imgproc.findContours(myMat, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            }

            if (contours.size() > 0)                             // change display so that it adjusts to the length
                retString += "1 ,";
            else
                retString += "0 ,";
            //Log.i(TAG, "retString = " + retString + ", times[" + i + "] = " + times[i] + ", numContours = " + contours.size());
        }

        return retString;
    }


}
