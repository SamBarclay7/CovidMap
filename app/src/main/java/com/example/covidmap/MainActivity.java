package com.example.covidmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.libraries.places.api.Places;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    PlacesClient placesClient;
    MapsFragment mapsFragment;
    final String PTAG="Places";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Grab maps fragment
        mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment);

        //Initialise places API
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment for Places
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(PTAG, "Place: " + place.getName() + ", " + place.getId());
                focusMap(place);
            }

            @Override
            public void onError(Status status) {
                Log.i(PTAG, "An error occurred: " + status);
            }
        });


        //Download the data and put it into the database
        getData();

        //Floating action button to go to list view
        FloatingActionButton fab = findViewById(R.id.locationButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });

        //Floating action button to center map on location
        FloatingActionButton gFab = findViewById(R.id.gpsButton);
        gFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsFragment.focusCurrent();
            }
        });
    }

    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String  url = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTwXSqlP56q78lZKxc092o6UuIyi7VqOIQj6RM4QmlVPgtJZfbgzv0a3X7wQQkhNu8MFolhVwMy4VnF/pub?gid=0&single=true&output=csv";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Networking", "Downloading data");
                        insertData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Network error", "Volley didn't work");
            }
        });

        queue.add(stringRequest);
    }

    private void insertData(String data) {
        DatabaseHelper db = new DatabaseHelper(this);
        db.addBulk(data);
        db.close();
        Log.d("SQL", "Done inserting data");
    }

    private void focusMap(Place place) {
        LatLng coords = place.getLatLng();

        Log.d(PTAG, "Focusing on coords " + coords.toString());

        mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment);
        mapsFragment.focusLatLng(coords);
    }
}