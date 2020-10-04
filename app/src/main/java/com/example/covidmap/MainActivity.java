package com.example.covidmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.ui.BubbleIconFactory;
import com.google.maps.android.ui.IconGenerator;


public class MainActivity extends AppCompatActivity {

    private GoogleMap mMap;


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
        genMarkers2();
    }

    private void genMarkers2()
    {
        MapsFragment mapsFragment = new MapsFragment();
        mMap =  MapsFragment.getMap();
        Log.d("TEST", "made to genMarkers");
        DatabaseHelper db = new DatabaseHelper(App.getmContext());
        double lat;
        double lng;
        int size = db.getLocationSize();
        LatLng ll;
//        Log.d("TEST23", "checkId0");
        boolean y = db.checkId(1000);

        List<PCLocation> PCLList2 = db.getAllPostcodeLocations();
        List<Postcode> allPCs = db.getAllPostcodes();

        Log.d("TEST23", "lat " + PCLList2.get(0).getLat());
        Log.d("TEST23", "lng " + PCLList2.get(0).getLng());
        Log.d("TEST23", "checkId022");
        mMap.clear();
                for (int i = 0; i < allPCs.size(); ++i) {

                    IconGenerator mIconGenerator = new IconGenerator(this);
//                    mIconGenerator.setContentView(mImageView);


                    Bitmap iconBitmap = mIconGenerator.makeIcon(Integer.toString(PCLList2.get(i).getPc()) + "\nActive: " + allPCs.get(i).getActive() + "\nTotal: " + allPCs.get(i).getCases() + "\nNew: " + allPCs.get(i).getNewCases());


                    TextView title = new TextView(this);

                    lat = PCLList2.get(i).getLat();
                    lng = PCLList2.get(i).getLng();
                    ll = new LatLng(lat, lng);
                    Log.d("TEST23", "lat " + PCLList2.get(i).getLat());
                    Log.d("TEST23", "lng " + PCLList2.get(i).getLng());

                    Marker marker = this.mMap.addMarker(new MarkerOptions()
                            .position(ll)
                            .title(Integer.toString(PCLList2.get(i).getPc()) + "\nActive: " + allPCs.get(i).getActive() + "\nTotal: " + allPCs.get(i).getCases()).icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)));

//                    Log.d("TEST", "made to genMarkers2");

                }

    }





    }



