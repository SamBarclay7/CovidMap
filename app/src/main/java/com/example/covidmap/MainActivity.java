package com.example.covidmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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


public class MainActivity extends AppCompatActivity {


    ImageButton listActivityButton;
    Button showDataActivityButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listActivityButton = (ImageButton) findViewById(R.id.listActivityButton);
        showDataActivityButton = findViewById(R.id.showDataActivityButton);
        getData();

        DatabaseHelper db = new DatabaseHelper(this);


        listActivityButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }

        });
        showDataActivityButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ShowDataActivity.class));
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
}