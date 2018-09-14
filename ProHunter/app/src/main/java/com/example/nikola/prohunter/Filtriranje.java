package com.example.nikola.prohunter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public interface Filtriranje {
    public HashMap<String, Marker> filtering(Map<String, Marker> mapa);
    public Filtriranje getF();
    public void setF(Filtriranje f);
}
