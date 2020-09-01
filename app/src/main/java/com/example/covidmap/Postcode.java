package com.example.covidmap;

import android.util.Log;

public class Postcode {
    private int postcode;
    private int population;
    private int active;
    private int cases;
    private double rate;
    private int newCases;

    public Postcode(int postcode, int population, int active, int cases, double rate, int newCases) {
        this.postcode = postcode;
        this.population = population;
        this.active = active;
        this.cases = cases;
        this.rate = rate;
        this.newCases = newCases;
    }

    public int getPostcode() {
        return postcode;
    }

    public int getPopulation() {
        return population;
    }

    public int getActive() {
        return active;
    }

    public int getCases() {
        return cases;
    }

    public double getRate() {
        return rate;
    }

    public int getNewCases() {
        return newCases;
    }

    public String toString() {
        return "Postcode{" +
                "postcode=" + postcode +
                ", population=" + population +
                ", active=" + active +
                ", cases=" + cases +
                ", rate=" + rate +
                ", newCases=" + newCases +
                '}';
    }
}
