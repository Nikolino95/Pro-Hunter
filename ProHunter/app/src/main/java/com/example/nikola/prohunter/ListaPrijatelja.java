package com.example.nikola.prohunter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ListaPrijatelja extends AppCompatActivity {

    ArrayList<String> lista;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_prijatelja);
        listView=findViewById(R.id.ListaPrijatelja);
        lista = new ArrayList<String>();
        HashMap<String,String> mapa = (HashMap<String, String>)getIntent().getSerializableExtra("friends");
        if(mapa!=null)
            for(String user:mapa.values())
                lista.add(user);
        listView.setAdapter(new ArrayAdapter<String>(ListaPrijatelja.this,android.R.layout.simple_list_item_1,lista));



    }

}
