package com.example.nikola.prohunter;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class Ulov
{
    public Ulov()
    {

    }
    public Ulov(long la, long lo,String t,String l,String il)
    {
        latitude = la;
        longitude = lo;
        tip = t;
        lovac=l;
        imeLovca = il;
        potvrde=new HashMap<String, String>();
    }
    @Exclude
    public String id;
    public double latitude;
    public double longitude;
    public String tip;
    public String lovac;
    public String imeLovca;
    public HashMap<String,String> potvrde;
}


