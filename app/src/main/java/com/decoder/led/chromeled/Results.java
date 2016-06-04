package com.decoder.led.chromeled;

import android.content.Context;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.lang.String;

import android.os.Environment;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class Results extends AppCompatActivity {
    private static final String TAG = "ResultsPage";
    //public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ChromeLED";
    TextView tv;
    String out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HashMap<String, String> key = new HashMap<String, String>();
        //regular binary to hex representations
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


        //bundle setup, pass data between activities
        Bundle extras = getIntent().getExtras();
        //int[] receivedData = extras.getIntArray("Array");
        String signalValues = extras.getString("signalIn");

        tv = (TextView)findViewById(R.id.textView_activity_results);
        TextView tv2 = (TextView)findViewById(R.id.bugInfo);
        //String bitS = extras.getString("bitString");
        //tv.setText(extras.getString("Message"));
        String debugOut = null;
        debugOut = "Signal length: " + signalValues.length() + '\n' + "Some 8 bytes: ";
        if (signalValues.length() < 32) {
            if(signalValues != null) {
                debugOut += "signalValues shorter than 64";
            }
        }
        else {
            debugOut += "here should be substring of signals";
            //debugOut += signalValues.substring(0, 32);
        }
            //int[] signalBinary = valuetoBinary(signalValues);

        out = "Message in Hex: " + msgDecode(signalValues, key);
        //String out = msgDecode(receivedData, key);
        tv2.setText(debugOut);
        tv.setText(out);

    }//end of on create

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

    public void loadText(View view){
        String Message;
        TextView textView = (TextView)findViewById(R.id.extratextView);

        try{
        FileInputStream fileInputStream = openFileInput("savefile");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        while ((Message = bufferedReader.readLine()) != null)
        {
            stringBuffer.append(Message + "\n");
        }
        textView.setText(stringBuffer.toString());
        textView.setVisibility(View.VISIBLE);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public String msgDecode(String bitString, HashMap<String, String> key ){
        //start here
        String buffer = null;
        //String buffer2 = null;
        //String hexTodecimal = null;
        String outMsg = "";
        //int decimal = 0;
        String decMsg = "";
        int j = 0;
        int modLength = 0;
        int bitLength = bitString.length();
        int decLength;
        //decoding process
        modLength = bitLength % 4;
        if(bitLength > 0) {
            if(bitLength < 4) {
                Log.i(TAG, "short input , is " + bitString.length() + " end");
                return "Short input";
                //will bitString.length be same as bitLength? during our process
            }
            else if(bitLength > 4) {
                for (int i = 0; i < (bitLength - modLength); i += 4) {
                    //for int array
                    //buffer = Integer.toString(bitString[i]) + Integer.toString(bitString[i + 1]) + Integer.toString(bitString[i + 2]) + Integer.toString(bitString[i + 3]);
                    //** Code for String of bits input **
                    //decMsg contains a string of hex characters from byte
                    buffer = bitString.substring(i, i + 4);
                    decMsg += key.get(buffer);
                    //decMsg contains Hex
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
            Toast.makeText(getApplicationContext(), "Odd length, message has been padded", Toast.LENGTH_LONG).show();
        }
        if((decLength > 4) && ((decMsg.length() % 2) == 0)) {
            while (j < decMsg.length()) {
                //check for escape char 0xDB
                if (decMsg.substring(j, j + 2) == "DB") {
                    j += 2;
                    if (decMsg.substring(j + 2, j + 4) == "50")
                        outMsg += "C0";
                    else if (decMsg.substring(j + 2, j + 4) == "51")
                        outMsg += "AA";
                    else if (decMsg.substring(j + 2, j + 4) == "52")
                        outMsg += "DB";
                    else
                        outMsg += "??";
                } else {
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
    }


}//end of results activity
