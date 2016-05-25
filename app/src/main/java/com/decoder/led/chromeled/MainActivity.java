package com.decoder.led.chromeled;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.os.EnvironmentCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        textView = (TextView)findViewById(R.id.main_textView);
        textView.setVisibility(View.GONE);

        InputStream is = null;

        try {
            is = getResources().getAssets().open("errorLog.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String Message;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
        } else if (id == R.id.nav_errorLog) {
            fragmentMangager.beginTransaction()
                    .replace(R.id.content_main, new ErrorLogFragment())
                    .commit();
            textView.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_help) {
            fragmentMangager.beginTransaction()
                    .replace(R.id.content_main, new HelpFragment())
                    .commit();
            textView.setVisibility(View.GONE);

        } else if (id == R.id.nav_contactUs) {
            fragmentMangager.beginTransaction()
                    .replace(R.id.content_main, new ContactUsFragment())
                    .commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void startDetect(View view) {
        Intent myintent = new Intent (MainActivity.this, DetectActivity.class);
        MainActivity.this.startActivity(myintent);
    }

   // public void displayErrors(View view) {

       /* try {
            String Message;
            FileInputStream fileInputStream = openFileInput("error_Log");
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
        */
//    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
