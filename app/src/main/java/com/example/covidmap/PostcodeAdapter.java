package com.example.covidmap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class PostcodeAdapter    extends RecyclerView.Adapter<PostcodeAdapter.ViewHolder> {


    private List<Postcode>  postcodeList;

    public PostcodeAdapter(List<Postcode> l){
        postcodeList    =   l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View postcodeView = inflater.inflate(R.layout.postcoderecyclerlayout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(postcodeView);
        return viewHolder;
    }


    public  void onBindViewHolder(ViewHolder    holder, int position){

        Postcode    postcode    =   postcodeList.get(position);

        holder.postcode.setText(String.valueOf(postcode.getPostcode()));
        holder.population.setText(String.valueOf(postcode.getPopulation()));
        holder.active.setText(String.valueOf(postcode.getActive()));
        holder.cases.setText(String.valueOf(postcode.getCases()));
        holder.rate.setText(String.valueOf(postcode.getRate()));
    }


    @Override
    public int getItemCount() {
        return postcodeList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView postcode;
        public TextView population;
        public TextView active;
        public TextView cases;
        public TextView rate;
        public TextView newCases;


        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            postcode = (TextView) itemView.findViewById(R.id.postcodeTV);
            population = (TextView) itemView.findViewById(R.id.populationTV);
            active = (TextView) itemView.findViewById(R.id.activeTV);
            cases = (TextView) itemView.findViewById(R.id.casesTV);
            rate = (TextView) itemView.findViewById(R.id.rateTV);
            newCases = (TextView) itemView.findViewById(R.id.newCasesTV);

        }
    }
}
