package com.ks.myportfolio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_btn_spotify:
                Toast.makeText(MainActivity.this, String.format(getString(R.string.toast_msg), getString(R.string.app_spotify)), Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_main_btn_score:
                Toast.makeText(MainActivity.this, String.format(getString(R.string.toast_msg), getString(R.string.app_score)), Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_main_btn_library:
                Toast.makeText(MainActivity.this, String.format(getString(R.string.toast_msg), getString(R.string.app_library)), Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_main_btn_build:
                Toast.makeText(MainActivity.this, String.format(getString(R.string.toast_msg), getString(R.string.app_builditbigger)), Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_main_btn_reader:
                Toast.makeText(MainActivity.this, String.format(getString(R.string.toast_msg), getString(R.string.app_reader)), Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_main_btn_capstone:
                Toast.makeText(MainActivity.this, String.format(getString(R.string.toast_msg), getString(R.string.app_capstone)), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
