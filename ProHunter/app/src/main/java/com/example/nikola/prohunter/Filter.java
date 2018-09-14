package com.example.nikola.prohunter;

import android.os.Parcelable;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public abstract class Filter implements Filtriranje {
    HashMap<String,Marker> mapa;
    Filtriranje f;
    public HashMap<String, Marker> filtering(Map<String, Marker> mapa) {
        filteringByType(mapa);
        HashMap<String,Marker> pom = f.filtering(mapa);
        if(pom != null)
            this.mapa.putAll(pom);
        return this.mapa;
    }

    protected abstract void filteringByType(Map<String,Marker> mapa);

    public Filtriranje getF() {
        return f;
    }

  @Override
  public void setF(Filtriranje f)
  {
      this.f = f;
  }
}
