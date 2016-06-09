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
import java.util.zip.ZipEntry;

import android.os.Environment;
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
    String out;
    boolean startFound = false;
    boolean endFound = false;
    //TextView tv = (TextView)findViewById(R.id.textView_activity_results);
    String interpoles = "";
    TextView tv3;
    int blockSize = 0;

    Toast showUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showUser = Toast.makeText(this,"", Toast.LENGTH_LONG);
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
        String temp = "Statistics: \n", temp2 = "", temp3 = "";
        //String interpoles = "";
        Bundle extras = getIntent().getExtras();
        int i = 0;
        long[] ts = extras.getLongArray("times");
        long ts0 = ts[0], diff = 0;
        String bins = processImages(extras.getLongArray("times"), extras.getInt("timeIndex"), extras.getBoolean("missedFrame"), extras.getIntArray("missedFrames"), extras.getInt("missedFrameIndex"));
        tv3 = (TextView)findViewById(R.id.textView_activity_results);
        TextView tv2 = (TextView)findViewById(R.id.extratextView);
        TextView parseTV = (TextView)findViewById(R.id.parsed_bin_textView);
        TextView timeStampTV = (TextView)findViewById(R.id.timeStampTV);
        //TextView tv = (TextView)findViewById(R.id.textView_activity_results);


        Log.i(TAG, "array 0 time: " + extras.getLongArray("times")[0] + " array 1 time: " + extras.getLongArray("times")[1] + " diff = " + (extras.getLongArray("times")[30] - extras.getLongArray("times")[29] ));
        while(i < bins.length()){
            if(i > 0)
                diff = ts[i] - ts[i-1];
            temp+= ("T" + i+  ": "+ (ts[i] - ts0) + ", Diff = "+ diff +", Value: " + bins.charAt(i) + "\n");
            interpoles+= (ts[i] - ts0) + "\n";
            i++;
        }//
        i = 0;
        while(i < bins.length()){
            //Redundant loop to add values to interpoles for view on textfile
            interpoles+= bins.charAt(i) + "\n";
            i++;
        }
        if(isExternalStorageWritable() == true){
            //tv3.setText("external is available");
            writeToSDFile(interpoles);
        }
        else
            tv3.setText("External Storage not writable!");

        temp2 = rawParse(bins, ts);
        temp3 = stringParse(temp2);
        //tv.setText(interpoles);
        tv3.setText("Raw Data: " + bins + "\nLength: " + bins.length());
        //temp = signalParser(bins, extras.getLongArray("times"));
        //String test = "0101100111010101010101010111100110011111111111111110000001100111111111";
        //parseTV.setText("Parsed Data: " + temp + "\n Length: " + temp.length());
        parseTV.setText("rawParse return: " + temp2 + "\n Length: " + temp2.length());
        //timeStampTV.setText("Time Stamps: " + temp + "\n Length: " + temp.length());
        timeStampTV.setText("String Parsed: " + ezRead(temp3) + "\n Length: " + temp3.length());

        out = msgDecode(temp3, key);
        tv2.setText("MsgDecode: " + out);
        //showUser.setText("startFound: " +startFound + ", endFound: "+ endFound);
        //showUser.show();
        Toast.makeText(getApplicationContext(), "startFound: " +startFound + ", endFound: "+ endFound, Toast.LENGTH_LONG).show();
    }//end of on create



    public String ezRead(String in){
        int div = 4, modme = 0;
        String outbutt = "", boofer = "";
        for(int i = 0; i < in.length(); i++){
            if((i % 4) == 0)
                outbutt+=" ";
            outbutt+=in.charAt(i);
        }
        return outbutt;
    }//end o ezRead



    public String rawParse(String rawData, long[] timeStamps){
        int chopped = 0, i = 0;
        //nexus 6 = 3
        int avgBlocks = 3, startBlock = avgBlocks*8;
        blockSize = avgBlocks;
        String startSignal = "";//"111000111000111000111000";
        String outPutt = "";
        //String startSignal = "111111000000111111000000111111000000111111000000", outPutt = "";
        for(int k = 0; k < 4; k++){
            while(i < blockSize) {
                startSignal += "1";
                i++;
            }
            i = 0;
            while(i < blockSize) {
                startSignal += "0";
                i++;
            }
            i = 0;
        }//end of for loop
        Log.i(TAG, "Here is our start: " + startSignal);
        while((chopped + startBlock < rawData.length()) && (!rawData.substring(chopped, chopped+startBlock).equals(startSignal))){
            //Log.i(TAG, "Here is raw sub string: " + rawData.substring(chopped, chopped + nexBlocks));
            chopped++;
        }
        if(rawData.substring(chopped, chopped+startBlock).equals(startSignal)) {
            showUser.setText("I've got you in my sights...");
        } else
            showUser.setText("no start in sight");

        showUser.show();

        outPutt = rawData.substring(chopped, rawData.length());
        for(int j = 0; j < chopped; j++){
            outPutt+= "0";
        }//for loop to add back chopped bits, multiple base 2
        return outPutt;
    }//end of raw parse



    public String stringParse(String input){
        String buffer = "1", output = "";
        int i = 0;
        while (i < input.length()){
            if(i == 0)
                i++;//string should start at 1, thus ignore first bit
            else {
                //read 3 ins our if different from previous, needs leeway for 2 - 3 bits on occasion
                if(input.charAt(i) == buffer.charAt(0)){     //buffer of only single type o bits
                    buffer+=input.charAt(i);
                }
                else {
                    //else do judgement, append to output, and clear buffer
                    output += judgement(buffer);
                    buffer = ""; //clear buffer
                    buffer+= input.charAt(i);
                }
            }//endo not first input
            i++;
        }//endo while

        return output;
    }//end of stringparse

    public String judgement(String defendant){
        String value = "", output = "";
        int spillage = 0;
        int repeatFor = 0;
        //NOTE: can have discrepnency if signals are just recorded longer than should be


        if(defendant.length() > 0)
            value = defendant.substring(0,1);
        else return "We have a problem...";

        if(defendant.length() > blockSize / 2) {//origninally 2
            spillage = (defendant.length() % blockSize);
            repeatFor = (int)(defendant.length()/blockSize);
            if(spillage > blockSize / 2)
                repeatFor++;
        }//3 o mo
        else repeatFor = 1; //defendant.length() 1 or 2 thus count as 1

        for(int i = 0; i < repeatFor; i++){
            output+=value;
        }

        return output;
    }//end of judgement



    private void writeToSDFile(String toWrite){

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();
        //tv3.append("\nExternal file system root: "+root);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root.getAbsolutePath() + "/ChromeLED");
        dir.mkdirs();
        File file = new File(dir, "myData.txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(toWrite);
            //pw.println("Hello");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //tv3.append("\n\nFile written to "+file);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



    public void saveText(View view){
        String filename = "savefile";
        FileOutputStream outputStream;
        String internalString = out + "\n";

        try{
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(internalString.getBytes());
            //outputStream.write("\n")
            outputStream.close();
            Toast.makeText(getApplicationContext(), "Message saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Message not saved", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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

    public String msgDecode(String bitString, HashMap<String, String> key ){
        //start here
        String buffer = null;
        String outMsg = "";
        String decMsg = "";
        int j = 0, chopped = 0;
        int modLength = 0, modLength2 = 0;
        int bitLength = bitString.length();
        int decLength;
        String startCode = "1010101010101010";
        //decoding process
        modLength = bitLength % 4;
        if(bitLength > 0) {
            if(bitLength < 4) {
                Log.i(TAG, "short input , is " + bitString.length() + " end");
                return "Short input";
                //will bitString.length be same as bitLength? during our process
            }
            else if(bitLength > 4) {
                //account for chopping of bits
                while(((chopped + 16) < (bitLength - modLength)) && (startFound == false)){//ensure we dont go apst length
                    Log.i(TAG, "Here is sub string: " + bitString.substring(chopped, chopped + 16));
                    //if((bitString.substring(chopped, chopped + 16)) == startCode) {
                    if((bitString.substring(chopped, chopped + 16)).equals(startCode)){
                        startFound = true;
                    }
                    else chopped++;
                }//end while loop looks for start signal so we do decode starting at chopped
                if(startFound == true){
                    modLength2 = (bitLength - chopped) % 4;
                    //take into account the chopped
                    for (int i = chopped; i+4 < (bitLength - modLength2); i += 4) {
                        buffer = bitString.substring(i, i + 4);
                        decMsg += key.get(buffer);
                    }
                } else {
                    for (int i = 0; i < (bitLength - modLength); i += 4) {
                        buffer = bitString.substring(i, i + 4);
                        decMsg += key.get(buffer);
                    }
                    return "start not found, Entire converted string: " + decMsg;
                }
            }
        }

        //without ascii conversion ( prints decMsg )
        //outMsg = decMsg;
        decLength = decMsg.length();
        //convert decMsg to ASCII
        if((decLength % 2) != 0) {
            //decLength = decLength - 1;
            decMsg += "P";
            //showUser.setText("Odd length, message has been padded");
            //showUser.show();
            Toast.makeText(getApplicationContext(), "Odd length, message has been padded", Toast.LENGTH_LONG).show();
        }
        if((decLength > 4) && ((decMsg.length() % 2) == 0)) {
            while ((j < decMsg.length()) && (endFound == false)) {
                //check for escape char 0xDB
                if (decMsg.substring(j, j + 2).equals("DB")) {
                    j += 2;
                    if (decMsg.substring(j + 2, j + 4).equals("50"))
                        outMsg += "C0";
                    else if (decMsg.substring(j + 2, j + 4).equals("51"))
                        outMsg += "AA";
                    else if (decMsg.substring(j + 2, j + 4).equals("52"))
                        outMsg += "DB";
                    else
                        outMsg += "??";
                }
                else if(decMsg.substring(j, j + 2).equals("C0")) {
                    endFound = true;
                    outMsg += decMsg.substring(j, j + 2);
                }
                else {
                    outMsg += decMsg.substring(j, j + 2);
                }
                //covert 1 byte into ascii
                //decimal = Integer.parseInt(decMsg.substring(j, j+2), 16);
                //outMsg += ((char) decimal);
                j += 2;
            }
        }
        else
            outMsg += "while loop failed";
        return outMsg;
        //add error handling?
    }//end of msgDecode
}
