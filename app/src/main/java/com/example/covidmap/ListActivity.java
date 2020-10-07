package com.example.covidmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatabaseHelper  db;
    private List<Postcode>  list;
    private PostcodeAdapter adapter;
    private RecyclerView    listRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Spinner spinner;
    private Button button;
    private Order order;
    boolean descending;

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
        spinner.setOnItemSelectedListener(this);

        //Hook up button
        descending = false;
        button = (Button) findViewById(R.id.buttonDescending);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descending = !descending;
                if(descending) {
                    button.setText("↓");
                } else {
                    button.setText("↑");
                }
                redoList();
            }
        });

        //Set default order
        order = Order.POSTCODE;
        list = db.getAllPostcodes(order);

        listRecyclerView = (RecyclerView) findViewById(R.id.listRecyclerView);

        adapter = new PostcodeAdapter(list);
        linearLayoutManager = new LinearLayoutManager(this);
        listRecyclerView.setAdapter(adapter);
        listRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void redoList() {
        list.clear();
        list.addAll(db.getAllPostcodes(order, descending));
        listRecyclerView.setAdapter(null);
        listRecyclerView.setLayoutManager(null);
        listRecyclerView.setAdapter(adapter);
        listRecyclerView.setLayoutManager(linearLayoutManager);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = String.valueOf(adapterView.getItemAtPosition(i));

        switch (item) {
            case "Postcode":
                order = order.POSTCODE;
                break;
            case "Population":
                order = order.POPULATION;
                break;
            case "Cases":
                order = order.CASES;
                break;
            case "New Cases":
                order = order.NEW;
                break;
            case "Active Cases":
                order = order.ACTIVE;
                break;
            case "Growth Rate":
                order = order.RATE;
                break;
        }
        Log.d("Spinner", "Order chosen is " + order.toString());

        redoList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
