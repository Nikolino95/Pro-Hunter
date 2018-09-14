package com.example.nikola.prohunter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DodajMarker extends AppCompatActivity  {

    myMarker m;
    boolean change;
    LatLng lokacija;
    final int PICK_IMAGE_REQUEST = 1;
    final int PICK_LOCATION_REQUEST = 2;
    String tip;
    Ulov ulov;
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("markers");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_marker);
        change = false;
        lokacija = null;
        m = new myMarker();
        ulov = new Ulov();
        Spinner s = findViewById(R.id.spinerDodajMarker);
        final TextView tipMarkera = findViewById(R.id.tipMarkera);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b != null) {
            String namena = b.getString("Namena");
            if (namena.equals("Prikaz")) {

                s.setVisibility(View.INVISIBLE);
                String kljuc = b.getString("Marker");
                dr.child(kljuc).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myMarker m = dataSnapshot.getValue(myMarker.class);
                        m.id=dataSnapshot.getKey();
                        tipMarkera.setText(m.tip);
                        EditText opis = findViewById(R.id.opisDodajMarker);
                        opis.setText(m.opis);
                        opis.setEnabled(false);
                        final ImageView slika = findViewById(R.id.slikaDodajMarker);
                        final String nesto=m.id;
                        if(m.slika)
                        {
                            StorageReference sr = FirebaseStorage.getInstance().getReference().child("images/MarkerOptions/"+m.id);
                            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(getBaseContext(),nesto,Toast.LENGTH_SHORT).show();
                                    Glide.with(getBaseContext()).load(uri).centerCrop().into(slika);
                                }

                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Button lokacija = findViewById(R.id.dodajKoordinate);
                lokacija.setVisibility(View.INVISIBLE);
                Button dodaj = findViewById(R.id.dodajMarkerR);
                dodaj.setVisibility(View.INVISIBLE);
                TextView latitude = findViewById(R.id.latitudeDodajMarker);
                latitude.setVisibility(View.INVISIBLE);;
                TextView longitude = findViewById(R.id.longitudeDodajMarker);
                longitude.setVisibility(View.INVISIBLE);
                return;
            }
        }

        tipMarkera.setVisibility(View.INVISIBLE);


        final Spinner spinner=findViewById(R.id.spinneZivotinje);



        ArrayAdapter<CharSequence> aa=ArrayAdapter.createFromResource(this,R.array.markeri,android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(aa);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String tip= parent.getSelectedItem().toString();
                EditText opis = findViewById(R.id.opisDodajMarker);
                ImageView iw = findViewById(R.id.slikaDodajMarker);
                if(tip.equals("Ulov"))
                {
                    spinner.setVisibility(View.VISIBLE);
                    iw.setVisibility(View.INVISIBLE);
                    opis.setVisibility(View.INVISIBLE);
                }
                else
                {
                    m.tip=tip;
                    spinner.setVisibility(View.INVISIBLE);
                    iw.setVisibility(View.VISIBLE);
                    opis.setVisibility(View.VISIBLE);
                }

            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] niz={"Jelen","Divlja svinja","Zec","Fazan","Lisica"};
        ArrayAdapter<CharSequence> aaa=new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,niz);
        aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aaa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ulov.tip=parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ImageView iw = findViewById(R.id.slikaDodajMarker);
        iw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/^");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, PICK_IMAGE_REQUEST);

            }
        });

        Button getLocation = findViewById(R.id.dodajKoordinate);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DodajMarker.this,nesto.class);
                i.putExtra("Namena","UzimanjeLokacije");
                startActivityForResult(i,PICK_LOCATION_REQUEST);
            }
        });

        Button dodaj = findViewById(R.id.dodajMarkerR);
        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(spinner.getVisibility()==View.INVISIBLE) {
                    EditText opis = findViewById(R.id.opisDodajMarker);
                    String op = opis.getText().toString();
                    if (lokacija != null) {
                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                        String key = dr.push().getKey();
                        m.id = key;
                        if (change) {
                            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                            StorageReference riversRef = mStorageRef.child("images/MarkerOptions/" + key);
                            ImageView imageView = findViewById(R.id.slikaDodajMarker);
                            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] slika = baos.toByteArray();
                            riversRef.putBytes(slika);
                            m.slika = true;
                        } else
                            m.slika = false;
                        m.opis = opis.getText().toString();
                        dr.child("markers").child(key).setValue(m);

                    }
                    else
                        Toast.makeText(getBaseContext(), "Dodajte lokaciju", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(lokacija!=null) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        ulov.lovac = user.getUid();
                        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("username")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (lokacija != null) {
                                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                                    String key = dr.push().getKey();
                                    ulov.id = key;
                                    ulov.imeLovca = dataSnapshot.getValue().toString();
                                    dr.child("ulov").child(key).setValue(ulov);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    else
                        Toast.makeText(getBaseContext(), "Dodajte lokaciju", Toast.LENGTH_LONG).show();

                }
                finish();
            }
        });

    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == PICK_LOCATION_REQUEST && resultCode == RESULT_OK)
            {

              lokacija =new LatLng(data.getDoubleExtra("latitude",1),
                                    data.getDoubleExtra("longitude",1));
              TextView longitude = findViewById(R.id.longitudeDodajMarker);
              longitude.setText(longitude.getText().toString() + "    "+lokacija.longitude);
              TextView latitude = findViewById(R.id.latitudeDodajMarker);
              latitude.setText(latitude.getText().toString()+ "    "+lokacija.latitude);
              Spinner s=findViewById(R.id.spinneZivotinje);
              if(s.getVisibility()==View.INVISIBLE) {
                  m.latitude = lokacija.latitude;
                  m.longitude = lokacija.longitude;
              }
              else
              {
                  ulov.latitude=lokacija.latitude;
                  ulov.longitude=lokacija.longitude;
              }
            }
            if(requestCode ==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                Bitmap bitmap;

                ImageView imageView = findViewById(R.id.slikaDodajMarker);

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    imageView.setImageBitmap(bitmap);
                    change = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        /*    if(!Debug.isDebuggerConnected())
            {
                Debug.waitForDebugger();
                Log.d("Debug","started");
            }*/
        }


}
