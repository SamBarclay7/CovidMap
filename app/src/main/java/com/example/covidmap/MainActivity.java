package com.example.covidmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    PlacesClient placesClient;
    MapsFragment mapsFragment;
    final String PTAG = "Places";
    final String STAG = "SQL";
    private GoogleMap mMap;


    ImageButton listActivityButton;

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
        String url = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTwXSqlP56q78lZKxc092o6UuIyi7VqOIQj6RM4QmlVPgtJZfbgzv0a3X7wQQkhNu8MFolhVwMy4VnF/pub?gid=0&single=true&output=csv";

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
        db.close();
        Log.d(STAG, "Done inserting data");
        genMarkers();
    }

    private void focusMap(Place place) {
        LatLng coords = place.getLatLng();

        Log.d(PTAG, "Focusing on coords " + coords.toString());

        mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment);
        mapsFragment.focusLatLng(coords);
    }

    private void genMarkers() {
        MapsFragment mapsFragment = new MapsFragment();
        mMap = MapsFragment.getMap();
        DatabaseHelper db = new DatabaseHelper(App.getmContext());
        double lat;
        double lng;
        int size = db.getLocationSize();
        LatLng ll;
        boolean y = db.checkId(1000);

        List<PCLocation> PCLList2 = db.getAllPostcodeLocations();
        List<Postcode> allPCs = db.getAllPostcodes();

        mMap.clear();
        for (int i = 0; i < allPCs.size(); ++i) {

            IconGenerator mIconGenerator = new IconGenerator(this);

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
        }
    }
}
