package com.decoder.led.chromeled;

import android.os.Bundle;
import java.util.HashMap;
import java.lang.String;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
        int[] receivedData = extras.getIntArray("Array");
        //int[]
        String signalValues = extras.getString("signalIn");
        TextView tv = (TextView)findViewById(R.id.textView_activity_results);
        TextView tv2 = (TextView)findViewById(R.id.bugInfo);
        String bitS = extras.getString("bitString");
        //tv.setText(extras.getString("Message"));
        String debugOut = "Signal length: " + signalValues.length() + '\n' + "Some bytes: ";
        debugOut += signalValues.substring(0, 64);
        //int[] signalBinary = valuetoBinary(signalValues);

        String out = "Message in Hex: " + msgDecode(signalValues, key);
        //String out = msgDecode(receivedData, key);
        tv2.setText(debugOut);
        tv.setText(out);


    }

    /*public int[] valuetoBinary(int[] signalValues){
        int[] signalBinary;

        return signalBinary;
    }
    */

    public String msgDecode(String bitString, HashMap<String, String> key ){
        //start here
        String output = "We processed!";
        String buffer = null;
        String buffer2 = null;
        String hexTodecimal = null;
        String outMsg = "";
        int decimal = 0;
        String decMsg = "";
        int j = 0;
        int modLength = 0;
        int bitLength = bitString.length();

        //value -> binary
        /*for(int i = 0; i < bitLength; i++){
            if(bitString[i] > 0)
                bitString[i] = 1;
            else
                bitString[i] = 0;
            //if 0 or -x, set to zero for whatever reason we get a negative number
        }*/

        //bitString contains our binary input,
        //decoding process
        modLength = bitLength % 4;
        if(bitLength > 0) {
            if(bitLength < 4) {
                Log.i(TAG, "short input , is " + bitString.length() + " end");
                return "Short input";
                //will bitString.length be same as bitLength? during our process
            }
            else
            //add a input.length mod 4 and create while loop w/ end condition as input.length - remainder cutting off last bits
            //add checker to see if high or low, if value exist then yes
            //for i -> 256, take subarry of bitString of size 4 and pull from hex table.


            for (int i = 0; i < (bitLength - modLength); i += 4) {
                //for int array
                //buffer = Integer.toString(bitString[i]) + Integer.toString(bitString[i + 1]) + Integer.toString(bitString[i + 2]) + Integer.toString(bitString[i + 3]);

                //code to convert hex to ascii ( might not actually be used because of code below)
                //buffer2 =  bitString.substring(i+4, i+8);
                //hexTodecimal += key.get(buffer);
                //hexTodecimal += key.get(buffer2);
                //decMsg += key.get(buffer); //replace with buffer when working
                //decMsg += key.get(buffer2);
                //decimal = Integer.parseInt(hexTodecimal, 16);

                //** Code for String of bits input **
                //decMsg contains a string of hex characters from byte
                buffer = bitString.substring(i, i+4);
                decMsg += key.get(buffer);
                //decMsg contains Hex
            }
        }
        //without ascii conversion ( prints decMsg )
        //outMsg = decMsg;

        //convert decMsg to ASCII
        while(j < decMsg.length()){
            //check for escape char 0xDB
            if(decMsg.substring(j,j+2) == "DB") {
                j+=2;
                if(decMsg.substring(j+2, j+4) == "50")
                    outMsg += "C0";
                else if(decMsg.substring(j+2, j+4) == "51")
                    outMsg += "AA";
                else if(decMsg.substring(j+2, j+4) == "52")
                    outMsg += "DB";
                else
                    outMsg += "??";
            }
            else
                outMsg += decMsg.substring(j,j+2);

            //covert 1 byte into ascii
            //decimal = Integer.parseInt(decMsg.substring(j, j+2), 16);
            //outMsg += ((char) decimal);
            j+=2;
        }

        return outMsg;
        //add error handling
    }

}
