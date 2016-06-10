package com.decoder.led.chromeled;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //displays errorLog.txt from assets (includes initializations)
        //textView = (TextView)findViewById(R.id.main_textView);
        textView = (TextView)findViewById(R.id.main_textView);

        textView.setVisibility(View.GONE);

        InputStream is = null;

        /*try {
            is = getResources().getAssets().open("errorLog.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //commented out for your saftey
        /*String Message;
        try {
            FileInputStream fileInputStream = openFileInput("savefile");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(inputStreamReader);
        //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer stringBuffer = new StringBuffer();
        try {
            while ((Message = reader.readLine()) != null)
            {
                stringBuffer.append(Message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.setText(stringBuffer.toString());//end of error message display
        */


    }

    //clear savefile
    public void clearLog(View view) {
        //try {
            //FileOutputStream clearMe = new FileOutputStream("savefile");
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Clearing Log")
                    .setMessage("Are you sure you want to clear log?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                            FileOutputStream clearMe = openFileOutput("savefile", Context.MODE_PRIVATE);
                            clearMe.write(("").getBytes());
                            clearMe.close();
                            Toast.makeText(getApplicationContext(), "Log Cleared!", Toast.LENGTH_LONG).show();
                            } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            } catch (IOException e) {
                            e.printStackTrace();
                            }
                        }
                    }).setNegativeButton("No", null).show();
    }

    public void loadText(View view) {
        String Message;
        try {
            FileInputStream fileInputStream = openFileInput("savefile");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer stringBuffer = new StringBuffer();
            try {
                while ((Message = reader.readLine()) != null)
                {
                    stringBuffer.append(Message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            textView.setText(stringBuffer.toString());//end of error message display
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager fragmentMangager = getFragmentManager();

        if (id == R.id.nav_detect) {
            fragmentMangager.beginTransaction()
                    .replace(R.id.content_main, new DetectFragment())
                    .commit();
            textView.setVisibility(View.GONE);
            // Handle the camera action
        } else if (id == R.id.nav_log) {
            fragmentMangager.beginTransaction()
                    .replace(R.id.content_main, new ErrorLogFragment())
                    .commit();
            textView.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_tutorial) {
            fragmentMangager.beginTransaction()
                    .replace(R.id.content_main, new TutorialFragment())
                    .commit();
            textView.setVisibility(View.GONE);

        } else if (id == R.id.nav_contact_us) {
            fragmentMangager.beginTransaction()
                    .replace(R.id.content_main, new ContactUsFragment())
                    .commit();
            textView.setVisibility(View.GONE);
        } else if (id == R.id.nav_message_encodings) {
            fragmentMangager.beginTransaction()
                    .replace(R.id.content_main, new MessageEncodingsFragment())
                    .commit();
            textView.setVisibility(View.GONE);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void startDetect(View view) {
        Intent myintent = new Intent (MainActivity.this, DetectActivity.class);
        MainActivity.this.startActivity(myintent);
    }



    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
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
