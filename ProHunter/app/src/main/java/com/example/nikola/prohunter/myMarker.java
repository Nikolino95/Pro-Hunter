package com.example.nikola.prohunter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class myMarker
{
    public myMarker()
    {

    }
    public myMarker(long la, long lo,String t,String o)
    {
        latitude = la;
        longitude = lo;
        tip = t;
        opis = o;
    }
    @Exclude
    public String id;
    public double latitude;
    public double longitude;
    public String tip;
    public String opis;
    public boolean slika;

}
