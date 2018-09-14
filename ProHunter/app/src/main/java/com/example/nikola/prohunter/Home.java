package com.example.nikola.prohunter;

import android.Manifest;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.Image;
import android.media.effect.Effect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.Inflater;


public class Home extends AppCompatActivity {

    ArrayList<Event> dogadjaji;
    private RecyclerView rv;
    DatabaseReference dr;
    private RecyclerView.Adapter rva;
    private RecyclerView.LayoutManager rvlm;
    LocationManager lm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        Toast.makeText(getBaseContext(),"ovo je home activity",Toast.LENGTH_SHORT).show();


        checkRequestAndUpdateLocation();
;
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        //StorageReference imgref = storage.child("images/"+user.getUid());
        rv=findViewById(R.id.listaDogadjajaHome);
        rv.hasFixedSize();
        rvlm=new LinearLayoutManager(this);
        rv.setLayoutManager(rvlm);
        dr = FirebaseDatabase.getInstance().getReference().child("events");
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               dogadjaji = new ArrayList<>();

                for(DataSnapshot userEvent : dataSnapshot.getChildren())
                for (DataSnapshot childSnapshot : userEvent.getChildren())
                {
                    Event event=childSnapshot.getValue(Event.class);
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
                    String d2 = df.format(c);
                    try {
                        Date d = df.parse(event.date);
                         Date d1=df.parse(d2);
                        if(d.getTime()>=d1.getTime())
                            dogadjaji.add(event);
                        else
                            dr.child(userEvent.getKey()).child(childSnapshot.getKey()).removeValue();
                    }
                    catch (Exception e)
                    {

                    }

                }
                rva=new MyAdapter(dogadjaji.toArray(new Event[dogadjaji.size()]));
                //lw.setAdapter(new MyAdapter(getBaseContext(),dogadjaji.toArray(new Event[dogadjaji.size()])));
                rv.setAdapter(rva);
                //dr.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        FloatingActionButton b=findViewById(R.id.fab);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getBaseContext(),addEvent.class);
                startActivity(i);
            }
        });
        BottomNavigationView bottomNavigationView=findViewById(R.id.barHome);
        Menu menu=  bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_profile:
                        Intent i=new Intent(Home.this,Profile.class);
                        startActivity(i);
                        break;
                    case R.id.navigation_map:
                        Intent i1=new Intent(Home.this,nesto.class);
                        startActivity(i1);
                        break;
                    case R.id.navigation_bluetooth:
                        Intent i2=new Intent(Home.this,Friends.class);
                        startActivity(i2);
                        break;
                    case R.id.navigation_home:
                        break;
                }
                return true;
            }
        });
    }

    boolean gps,net;
    private static final int gps_turn_on=1255;
    public void checkRequestAndUpdateLocation()
    {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(gps)
            checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        else if(net)
            checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        else
        //Toast.makeText(this, "Providers for location detection is unavilable", Toast.LENGTH_LONG).show();
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Dozvolite ukljucivanje GPS-a").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(i,gps_turn_on);
                    //checkRequestAndUpdateLocation();
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }


    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public void checkPermission(final String permission)
    {
        if(ActivityCompat.checkSelfPermission(this, permission)== PackageManager.PERMISSION_GRANTED)
            setUpdateLocations();
        else if(ActivityCompat.shouldShowRequestPermissionRationale(this,permission))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(Home.this,
                                    new String[]{permission},
                                    MY_PERMISSIONS_REQUEST_LOCATION );
                        }
                    })
                    .create()
                    .show();
        }
        else
        {
            ActivityCompat.requestPermissions(Home.this,new String[]{permission},MY_PERMISSIONS_REQUEST_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                        setUpdateLocations();
                        /*mMap.setMyLocationEnabled(true);*/
                    }

                } else
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();


            }
        }
    }
    final static long minTime = 5000;
    final static float minDIstance = 10;
    public void setUpdateLocations()
    {
        try {
            Intent i = new Intent(Home.this,LocationDetectionService.class);
            if (gps)
                //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDIstance, this);
                i.putExtra("provider",LocationManager.GPS_PROVIDER);
            else if (net)
                //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDIstance, this);
                i.putExtra("provider",LocationManager.NETWORK_PROVIDER);
            ComponentName service=startService(i);

        }
        catch (SecurityException ex)
        {
            Log.e("Location provider error",ex.getMessage());
        }
    }

    public static final int REQUEST_ENABLE_BT=100;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==gps_turn_on)
            checkRequestAndUpdateLocation();

    }

    protected int pxFromDp( final float dp) {
        return (int) (dp * this.getResources().getDisplayMetrics().density);
    }





}
