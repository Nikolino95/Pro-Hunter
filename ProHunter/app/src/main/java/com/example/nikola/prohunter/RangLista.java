package com.example.nikola.prohunter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class RangLista extends AppCompatActivity {

    ArrayList<User> lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rang_lista);
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("users");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lista = new ArrayList<User>();
                for(DataSnapshot u : dataSnapshot.getChildren())
                    lista.add(u.getValue(User.class));
                Collections.sort(lista);
                UpdateUI(lista);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void UpdateUI(ArrayList<User> lista) {
        ListView lv=findViewById(R.id.listviewRangLista);
        lv.setAdapter(new RangLIstaAdapter(getBaseContext(),lista));
    }
}
