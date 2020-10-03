package com.example.covidmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {


    ImageButton listActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get location permissions right off the bat
        getPermissions(Manifest.permission.ACCESS_FINE_LOCATION);


        //Download the data and put it into the database
        getData();

        //Floating action button to go to list view
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListActivity.class));



            }
        });


    }

    private void getPermissions(String permission) {
        if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permissions", "Permission \"" + permission + "\" is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{permission}, 333);
        } else {
            Log.d("Permissions", "Permission \"" + permission + "\" is granted");
        }
    }

    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String  url = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTwXSqlP56q78lZKxc092o6UuIyi7VqOIQj6RM4QmlVPgtJZfbgzv0a3X7wQQkhNu8MFolhVwMy4VnF/pub?gid=0&single=true&output=csv";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Networking", "Downloading data");
                        try {
                            insertData(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Network error", "Volley didn't work");
            }
        });

        queue.add(stringRequest);
    }

    private void insertData(String data) throws IOException {
        DatabaseHelper db = new DatabaseHelper(this);
        db.addBulk(data);
//        db.insertLocationData();
        db.close();
        Log.d("SQL", "Done inserting data");
//        genMarkers(db);
    }



    }



