
package com.example.nikola.prohunter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Interpolator;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class nesto extends FragmentActivity implements OnMapReadyCallback/*LocationListener*/{
    private GoogleMap mMap;
    final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    String namena="nista";
    final int ADD_MARKER_REQUEST = 1;
    DatabaseReference dr;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    HashMap<String,Marker> korisnici;
    HashMap<String,Marker> markeri = new HashMap<String,Marker>();
    HashMap<String,Marker> ulovi = new HashMap<String,Marker>();
    StorageReference sr = FirebaseStorage.getInstance().getReference();
    boolean showUsers=false;
    boolean next;
    LocationDetectionService locationService;
    ValueEventListener inicijalizujKorisnike;
    ChildEventListener azurirajKorisnike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nesto);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b != null)
        {
            namena = b.getString("Namena");
            if(namena.equals("UzimanjeLokacije"))
            {
                findViewById(R.id.fabplus).setVisibility(View.INVISIBLE);
                findViewById(R.id.fablupa).setVisibility(View.INVISIBLE);
                findViewById(R.id.fabfilter).setVisibility(View.INVISIBLE);
                return;
            }
        }
        dr = FirebaseDatabase.getInstance().getReference();

        BroadcastReceiver lr = new LocationReceiver();
        IntentFilter intf = new IntentFilter("LOCATION_DETECTION");

        registerReceiver(lr,intf);

        dr.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(me==null) {
                    User u=dataSnapshot.getValue(User.class);
                    MarkerOptions mop = new MarkerOptions();
                    mop.title("Me");
                    mop.icon(BitmapDescriptorFactory.fromResource(R.drawable.mi));
                    mop.position(new LatLng(Double.parseDouble(u.latitude), Double.parseDouble(u.longitude)));
                    me = mMap.addMarker(mop);
                    //Toast.makeText(getBaseContext(), "prvi put", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dr.child("markers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                myMarker m = dataSnapshot.getValue(myMarker.class);
                m.id = dataSnapshot.getKey();
                MarkerOptions mo = new MarkerOptions();
                mo.title(m.tip);
                switch(m.tip)
                {
                    case "Opasnost":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.danger));
                        break;
                    case "Logor":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.logor));
                        break;
                    case "Medved":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.meda));
                        break;
                    case "Jelen":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.jelence));
                        break;
                    case "Divlja svinja":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.boar));
                        break;
                    case "Zec":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.rabbit_shape));
                        break;
                    case "Fazan":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.fazan));
                        break;
                    case "Lisica":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.fox_sitting));
                        break;
                    case "Vuk":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.wolf));
                        break;
                    case "Izvor":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.waterfall));
                        break;
                    case "Pecina":
                        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.cave));
                        break;
                }
                mo.position(new LatLng(m.latitude,m.longitude));
                Log.e("Marker greska",m.id);
                Marker marker = mMap.addMarker(mo);
                marker.setTag(m.id);
                markeri.put(m.id,marker);

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        dr.child("ulov").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Ulov u = dataSnapshot.getValue(Ulov.class);
                MarkerOptions mo = new MarkerOptions();
                mo.position(new LatLng(u.latitude,u.longitude));
                mo.title("Ulov");
                mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.trofej));
                Marker m = mMap.addMarker(mo);
                m.setTag(dataSnapshot.getKey());
                ulovi.put(dataSnapshot.getKey(),m);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ulovi.get(dataSnapshot.getKey()).remove();
                ulovi.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        inicijalizujKorisnike=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                korisnici=new HashMap<String,Marker>();

                for (DataSnapshot us : dataSnapshot.getChildren())
                {
                    final User pom = us.getValue(User.class);
                    pom.id = us.getKey();
                    if(pom.id.equals(user.getUid()))
                        continue;
                    if (pom.online)
                    {
                        final MarkerOptions mo = new MarkerOptions();
                        mo.title(pom.username);
                        mo.position(new LatLng(Double.parseDouble(pom.latitude), Double.parseDouble(pom.longitude)));
                        if (pom.friends == null || pom.friends.get(user.getUid()) == null) {
                            mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.avatar_icon));
                            Marker mar=mMap.addMarker(mo);
                            korisnici.put(pom.id,mar);
                        } else if (pom.friends.get(user.getUid()) != null) {
                            sr.child("images/" + pom.id).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bmp =Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length),24,24,false);
                                    mo.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                                    Marker mar=mMap.addMarker(mo);
                                    mar.setTag(pom.id);
                                    korisnici.put(pom.id,mar);
                                }
                            });

                        }
                    }
                }
                //dr.child("users").removeEventListener(this);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        azurirajKorisnike=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                final User u=dataSnapshot.getValue(User.class);
                u.id=dataSnapshot.getKey();
                if(u.id.equals(user.getUid()))
                    return;
                Marker m = (Marker)korisnici.get(u.id);
                if(!u.online)
                {
                    if (m!= null) {
                        m.remove();
                        korisnici.remove(u.id);
                    }
                }
                else if(m==null)
                {
                    makeMarkerOptions(u);


                }
                else
                {
                    m.setPosition(new LatLng(Double.parseDouble(u.latitude),Double.parseDouble(u.longitude)));


                    // m.setVisible(false);
                    // moveVechile(m,new LatLng(Double.parseDouble(u.latitude),Double.parseDouble(u.longitude)));

                }


            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        final FloatingActionButton prijatelji=findViewById(R.id.fabfilter);
        prijatelji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showUsers)
                    prijatelji.setImageResource(R.drawable.svi_korisnici);
                else
                    prijatelji.setImageResource(R.drawable.samo_mi);
                showUsers=!showUsers;
                refreshMap();
            }
        });


        FloatingActionButton plus = findViewById(R.id.fabplus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(nesto.this,DodajMarker.class);
                startActivity(i);
            }
        });

        FloatingActionButton lupa=findViewById(R.id.fablupa);
        lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(nesto.this,Filtering.class);
                //i.putExtra("me", me.getPosition());
                startActivityForResult(i,631);
            }
        });

        BottomNavigationView bottomNavigationView=findViewById(R.id.barnesto);
        Menu menu=  bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_profile:
                        Intent i=new Intent(nesto.this,Profile.class);
                        startActivity(i);
                        break;
                    case R.id.navigation_map:
                        break;
                    case R.id.navigation_home:
                        Intent i1=new Intent(nesto.this,Home.class);
                        startActivity(i1);
                        break;
                    case R.id.navigation_bluetooth:
                        Intent i2=new Intent(nesto.this,Friends.class);
                        startActivity(i2);
                        break;
                }
                return true;
            }
        });

        final Button button=findViewById(R.id.fabx);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Marker m:markeri.values())
                    m.setVisible(true);
                button.setVisibility(View.INVISIBLE);
            }
        });
    }


    public class LocationReceiver extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            double latitude=0,longitude=0;
            if(b!= null)
            {
                latitude = b.getDouble("latitude");
                longitude = b.getDouble("longitude");
            }
            if(me == null)
            {
                MarkerOptions mop = new MarkerOptions();
                mop.title("Me");
                mop.icon(BitmapDescriptorFactory.fromResource(R.drawable.wolf));
                mop.position(new LatLng(latitude,longitude));
                me = mMap.addMarker(mop);
                Toast.makeText(getBaseContext(),"prvi put",Toast.LENGTH_SHORT).show();

            }
            else {
                me.setPosition(new LatLng(latitude, longitude));
                Toast.makeText(getBaseContext(), "menjamo lokaciju", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void refreshMap()
    {
        if(showUsers) {
            dr.child("users").addListenerForSingleValueEvent(inicijalizujKorisnike);
            dr.child("users").addChildEventListener(azurirajKorisnike);
        }
        else
        {
            dr.child("users").removeEventListener(azurirajKorisnike);
            for(Marker m:korisnici.values())
                m.remove();
            korisnici.clear();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode==631 && resultCode== RESULT_OK)
        {
            HashMap<String,String> hmp=(HashMap<String,String>)data.getSerializableExtra("mapa");
            Button button=findViewById(R.id.fabx);
            button.setVisibility(View.VISIBLE);
            filterMap(hmp);
        }
    }
    String[] tipovi = new String[]{"Opasnost","Logor","Medved","Jelen","Divlja svinja","Zec","Fazan","Lisica","Vuk","Izvor","Pecina"};

    private void filterMap(HashMap<String, String> hmp) {
        Filtriranje f = new End();
        for(String s:hmp.values())
        {
            switch(s)
            {
                case "Opasnost":
                    f = new FilterTip(f,tipovi[0]);
                    break;
                case "Logor":
                    f = new FilterTip(f,tipovi[2]);
                    break;
                case "Medved":
                    f = new FilterTip(f,tipovi[2]);
                    break;
                case "Jelen":
                    f = new FilterTip(f,tipovi[3]);
                    break;
                case "Divlja svinja":
                    f = new FilterTip(f,tipovi[4]);
                    break;
                case "Zec":
                    f = new FilterTip(f,tipovi[5]);
                    break;
                case "Fazan":
                    f = new FilterTip(f,tipovi[6]);
                    break;
                case "Lisica":
                    f = new FilterTip(f,tipovi[7]);
                    break;
                case "Vuk":
                    f = new FilterTip(f,tipovi[8]);
                    break;
                case "Izvor":
                    f = new FilterTip(f,tipovi[9]);
                    break;
                case "Pecina":
                    f = new FilterTip(f,tipovi[10]);
                    break;
                case "Da":
                    f = new FilterSlika(f,true);
                    break;
                case "Ne":
                    f = new FilterSlika(f,false);
                    break;
                default:
                    f = new FilterDistanca(f,Integer.parseInt(s),me.getPosition());
            }
        }
        for(Marker m:markeri.values())
            m.setVisible(false);
        f.filtering(markeri);
    }

      public boolean isMarker(Marker m)
    {
        for(String tip :tipovi)
            if(m.getTitle().equals(tip))
                return true;
        return false;
    }

    public void makeMarkerOptions(final User u)
    {
        final MarkerOptions mo = new MarkerOptions();
        mo.title(u.username);
        mo.position(new LatLng(Double.parseDouble(u.latitude),Double.parseDouble(u.longitude)));
        if(u.friends == null || u.friends.get(user.getUid()) == null)
        {
            mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.avatar_icon));
            korisnici.put(u.id, mMap.addMarker(mo));
        }
        else if(u.friends.get(user.getUid()) != null)
        {
            sr.child("images/users/"+u.id).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                    mo.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                    korisnici.put(u.id, mMap.addMarker(mo));
                }
            });
        }
    }


    Marker me=null;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(isMarker(marker))
                {
                    Intent i = new Intent(nesto.this,DodajMarker.class);
                    i.putExtra("Namena","Prikaz");
                    i.putExtra("Marker",marker.getTag().toString());
                    startActivity(i);
                }
                else if(marker.getTitle().toString().equals("Ulov"))
                {
                    dr.child("ulov").child(marker.getTag().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            final Ulov u = dataSnapshot.getValue(Ulov.class);
                            u.id = dataSnapshot.getKey();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(nesto.this);
                            builder1.setMessage("Da li zelite da potvrdite ulov korisnika "+u.imeLovca);
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Da",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(u.lovac.equals(user.getUid()))
                                            {
                                                Toast.makeText(nesto.this,"Ne mozete sami sebi da potvrdite ulov!",Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            if(u.potvrde == null)
                                                u.potvrde = new HashMap<String,String>();
                                            if(u.potvrde.get(user.getUid()) == null)
                                            {
                                                u.potvrde.put(user.getUid(), "");
                                                if (u.potvrde.size() > 2) {
                                                    dr.child("ulov").child(dataSnapshot.getKey()).removeValue();
                                                    dr.child("users").child(u.lovac).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            int poeni = (int)dataSnapshot.getValue(User.class).poeni;
                                                            poeni+=dajPoene(u.tip);
                                                            dr.child("users").child(u.lovac).child("poeni").setValue(poeni);
                                                        }



                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                                else
                                                    dr.child("ulov").child(u.id).setValue(u);
                                                Toast.makeText(nesto.this,"Uspesno ste  potvrdili ovaj ulov!",Toast.LENGTH_LONG).show();
                                            }
                                            else
                                                Toast.makeText(nesto.this,"Vec ste potvrdili ovaj ulov.",Toast.LENGTH_LONG).show();
                                            dialog.cancel();
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "Ne",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    if(marker.getTag()!=null)
                    {
                        Intent i = new Intent(nesto.this, Profile.class);
                        i.putExtra("id", marker.getTag().toString());
                        startActivity(i);
                    }
                }
                return false;
            }
        });
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ACCESS_FINE_LOCATION);
        }
        else
        {
            if(namena.equals("UzimanjeLokacije"))
                setOnMapClickListener();
            /*else
                mMap.setMyLocationEnabled(true);*/
        }


    }

    public int dajPoene(String tip)
    {
        switch(tip) {
            case "Jelen":
                return 40;
            case "Divlja svinja":
                return 25;
            case "Zec":
                return 15;
            case "Fazan":
                return 30;
            case "Lisica":
                return 20;
                default:
                    return -1;
        }

    }
    public void moveVechile(final Marker myMarker,final LatLng finalPosition)
    {

        final LatLng startPosition = myMarker.getPosition();
        Toast.makeText(this,startPosition.latitude+"   "+startPosition.longitude,Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
       // final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;
        handler.post(new Runnable() {
            long elapsed;
            float t;
           // float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
               // v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.latitude) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.longitude) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                }
 else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }

            }
        });
    }
    protected void setOnMapClickListener()
    {
        if(mMap != null)
        {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(namena.equals("UzimanjeLokacije"))
                    {
                        Intent i = new Intent();
                        i.putExtra("longitude",latLng.longitude);
                        i.putExtra("latitude",latLng.latitude);
                        setResult(Activity.RESULT_OK,i);
                        finish();
                    }
                }
            });
        }
    }



    protected void beActive()
    {
        SharedPreferences sp = getSharedPreferences("OURINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.commit();

    }
/*    @Override
    protected void onStart() {
        super.onStart();
        beActive();
    }*/

    @Override
    protected void onResume()
    {
        super.onResume();
        beActive();
    }

    protected void beInactive()
    {
        SharedPreferences sp = getSharedPreferences("OURINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.commit();
    }

 /*   @Override
    protected void onStop() {
        super.onStop();
        beInactive();
    }*/

    @Override
    protected void onPause()
    {
        super.onPause();
        beInactive();
    }
}
