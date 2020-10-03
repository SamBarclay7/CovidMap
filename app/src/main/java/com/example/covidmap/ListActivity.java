package com.example.covidmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private DatabaseHelper  db;
    private List<Postcode>  l;
    private PostcodeAdapter adapter;
    private RecyclerView    listRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        db = new DatabaseHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        l = db.getAllPostcodes();

        listRecyclerView = (RecyclerView) findViewById(R.id.listRecyclerView);
        rvSetup((ArrayList) l, listRecyclerView);

    }

        private  void   rvSetup(List l, RecyclerView  r){
           adapter  =   new PostcodeAdapter(l);
           r.setAdapter(adapter);
           r.setLayoutManager(new LinearLayoutManager(this));

        }

    }
