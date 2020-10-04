package com.example.covidmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatabaseHelper  db;
    private List<Postcode>  l;
    private PostcodeAdapter adapter;
    private RecyclerView    listRecyclerView;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db = new DatabaseHelper(this);

        //Spinner things
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        l = db.getAllPostcodes();

        listRecyclerView = (RecyclerView) findViewById(R.id.listRecyclerView);
        rvSetup((ArrayList) l, listRecyclerView);

    }

        private  void   rvSetup(List l, RecyclerView  r){
           adapter  =   new PostcodeAdapter(l);
           r.setAdapter(adapter);
           r.setLayoutManager(new LinearLayoutManager(this));

        }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
