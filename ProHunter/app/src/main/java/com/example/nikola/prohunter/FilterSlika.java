package com.example.nikola.prohunter;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FilterSlika extends Filter {

    boolean slika;
    public FilterSlika(Filtriranje f,boolean slika)
    {
        this.f = f;
        this.slika=slika;
        this.mapa = new HashMap<String,Marker>();
    }


    protected void filteringByType(final Map<String,Marker> map)
    {
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("markers");
        String id;
        for(final Marker m:map.values())
        {
            dr.child(m.getTag().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myMarker marker = dataSnapshot.getValue(myMarker.class);
                    if(slika==marker.slika)
                    {
                        /*mapa.put(dataSnapshot.getKey(),m);
                        map.remove(dataSnapshot.getKey());*/
                        m.setVisible(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
