package com.example.nikola.prohunter;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class FilterDistanca extends Filter {

    int distanca;
    LatLng endPoint;
    public FilterDistanca(Filtriranje f,int distanca,LatLng endPoint)
    {
        this.f = f;
        this.distanca = distanca;
        this.endPoint = endPoint;
        this.mapa = new HashMap<String,Marker>();
    }

    @Override
    protected void filteringByType(Map<String, Marker> mapa) {
        float[] result = new float[1];
        String id;
        double startLatitude,startLongiude;
        for(Marker m:mapa.values())
        {
            startLatitude = m.getPosition().latitude;
            startLongiude = m.getPosition().longitude;
            Location.distanceBetween(startLatitude,startLongiude,endPoint.latitude,endPoint.longitude,result);
            if(result[0]<=distanca*1000)
            {
               /* id = m.getTag().toString();
                this.mapa.put(id,m);
                mapa.remove(id);*/
               m.setVisible(true);
            }
        }

    }
}
