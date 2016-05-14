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
        TextView tv = (TextView)findViewById(R.id.textView_activity_results);
        String bitS = extras.getString("bitString");
        //tv.setText(extras.getString("Message"));
        String out = "message: " + msgDecode(bitS, key);
        //String out = msgDecode(receivedData, key);
        tv.setText(out);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
    public String msgDecode(String bitString, HashMap<String, String> key ){
        //do computation
        String output = "We processed!";
        String buffer = null;
        String buffer2 = null;
        String hexTodecimal = null;
        String outMsg = "";
        int decimal = 0;
        String decMsg = "";
        int j = 0;

        if(bitString.length() < 256) {
            Log.i(TAG, "short input , is " + bitString.length()+ " end");
            return "Short input";
        }
        else
            Log.i(TAG, "were past a short input " + bitString.substring(0,4)+ " end");
        //for i -> 256, take subarry of bitString of size 4 and pull from hex table.
        //commented code refers to use of an int[]
        for(int i = 0; i <256; i+=4){
            //buffer = Integer.toString(bitString[i])+ Integer.toString(bitString[i+1]) + Integer.toString(bitString[i+2]) + Integer.toString(bitString[i+3]);
            buffer = bitString.substring(i, i+4);
            //buffer2 =  bitString.substring(i+4, i+8);
            //hexTodecimal += key.get(buffer);
            //hexTodecimal += key.get(buffer2);
            //decMsg += key.get(buffer); //replace with buffer when working
            //decMsg += key.get(buffer2);
            //decimal = Integer.parseInt(hexTodecimal, 16);
            decMsg += key.get(buffer);
        }
        while(j < decMsg.length()){
            decimal = Integer.parseInt(decMsg.substring(j, j+2), 16);
            outMsg += ((char) decimal);
            j+=2;
        }
        return outMsg;
        //add error handling
    }

}
