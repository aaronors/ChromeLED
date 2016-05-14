package com.decoder.led.chromeled;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sackofcodetatoes.LedDetector.R;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void switchDetect(View view) {
        Intent myintent = new Intent (home.this, Tutorial1Activity.class);
        home.this.startActivity(myintent);
    }

    public void switchTutorial(View view) {
        Intent myIntent = new Intent (home.this, tutorial.class);
        home.this.startActivity(myIntent);
    }

    public void switchHistory(View view) {
        Intent myIntent = new Intent (home.this, history.class);
        home.this.startActivity(myIntent);
    }

    public void ContactUs(View view) {
        Intent chooser = null;
        //String filelocation="/mnt/sdcard/contacts_sid.vcf";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        String[] to={"3805la+djdigi02fp9bo@sharklasers.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Customer Support");
        //intent.putExtra(Intent.EXTRA_STREAM, filelocation);
        intent.putExtra(Intent.EXTRA_TEXT, "Sent from mobile app.");
        intent.setType("message/rfc822");
        chooser = Intent.createChooser(intent, "Send Email");
        startActivity(chooser);
    }

}
