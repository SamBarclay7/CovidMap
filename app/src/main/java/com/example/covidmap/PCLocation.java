package com.example.covidmap;

public class PCLocation
{
    private int pc;
    private double lat;
    private double lng;

    public PCLocation (int pc, double lat, double lng)
    {
        this.pc = pc;
        this.lat = lat;
        this.lng = lng;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
