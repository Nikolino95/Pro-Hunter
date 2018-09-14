package com.example.nikola.prohunter;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class FilterTip extends Filter {
    public String tip;
    public FilterTip(Filtriranje f,String tip)
    {
        this.f = f;
        this.tip = tip;
        mapa = new HashMap<String, Marker>();
    }


    protected void filteringByType(Map<String,Marker> mapa)
    {
        String id;
        for(Marker m:mapa.values())
        {
            if(m.getTitle().equals(this.tip))
            {
                /*id = m.getTag().toString();
                this.mapa.put(id,m);
                mapa.remove(id);*/
                m.setVisible(true);
            }
        }
    }
}
