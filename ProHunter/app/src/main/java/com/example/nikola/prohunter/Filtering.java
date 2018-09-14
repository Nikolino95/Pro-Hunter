package com.example.nikola.prohunter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class Filtering extends AppCompatActivity {
    HashMap<String,String> hmp = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);
        Intent in=getIntent();
        Bundle bu=in.getExtras();
        //final LatLng me=bu.getParcelable("me");
        String[] tipovi = new String[]{"Opasnost","Logor","Medved","Jelen","Divlja svinja","Zec","Fazan","Lisica","Vuk","Izvor","Pecina"};
        CompoundButton.OnCheckedChangeListener check=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String pom = buttonView.getText().toString();
                if(isChecked)
                {
                    hmp.put(pom,pom);
                }
                else
                {
                   hmp.remove(pom);

                }
            }
        };
        CompoundButton.OnCheckedChangeListener check1=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String pom = buttonView.getText().toString();
                if(isChecked)
                {
                    hmp.put("Slika",pom);
                }
                else
                {
                    hmp.remove("Slika");
                }
            }
        };
        LinearLayout ll=findViewById(R.id.filtering);
        LinearLayout ll1=null;
        TextView tv=new TextView(this);
        tv.setText("Izaberite tip");
        tv.setGravity(Gravity.CENTER);
        ll.addView(tv);
        for(int i=0;i<11;i++)
        {
            if(i%2==0) {
                ll1 = new LinearLayout(this);
                ll1.setOrientation(LinearLayout.HORIZONTAL);
                ll.addView(ll1);
            }
            CheckBox cb=new CheckBox(this);
            cb.setText(tipovi[i]);
            cb.setOnCheckedChangeListener(check);
            ll1.addView(cb);
        }
        tv=new TextView(this);
        tv.setText("Sa slikom");
        tv.setGravity(Gravity.CENTER);
        ll.addView(tv);


        ll1 = new LinearLayout(this);
        ll1.setOrientation(LinearLayout.HORIZONTAL);
        ll.addView(ll1);


        CheckBox cb=new CheckBox(this);
        cb.setText("Da");
        cb.setOnCheckedChangeListener(check1);
        ll1.addView(cb);


        cb=new CheckBox(this);
        cb.setText("Ne");
        cb.setOnCheckedChangeListener(check1);
        ll1.addView(cb);

        tv=new TextView(this);
        tv.setText("Izaberite radijus");
        tv.setGravity(Gravity.CENTER);
        ll.addView(tv);

        final EditText et=new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setGravity(Gravity.CENTER);
        ll.addView(et);

        Button b=new Button(this);
        b.setText("OK");
        b.setGravity(Gravity.CENTER);
        ll.addView(b);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=et.getText().toString();
                if(!s.equals(""))
                {
                    int i=Integer.parseInt(s);
                    if(i>0)
                        hmp.put("Distanca",""+i);
                }
                //HashMap<String,Marker> n=f.filtering(hmp);
                Intent inte=new Intent();
                inte.putExtra("mapa",hmp);
                setResult(Activity.RESULT_OK,inte);
                finish();
            }
        });





    }

}
