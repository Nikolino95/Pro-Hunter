package com.example.nikola.prohunter;

import com.google.android.gms.maps.model.LatLng;



public class Event
{
    public String id;
    public String name;
    public double longitude;
    public double latitude;
    public String date;
    public String time;
    public String tip;
    public String user;
    public Event()
    {

    }

    public Event(String n, LatLng lokacija,String d,String t,String l)
    {
        name = n;
        longitude = lokacija.longitude;
        latitude = lokacija.latitude;
        tip=l;
        date = d;
        time = t;
    }

}
