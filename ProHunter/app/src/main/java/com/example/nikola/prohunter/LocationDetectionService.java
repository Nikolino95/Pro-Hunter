package com.example.nikola.prohunter;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class LocationDetectionService extends Service {
    final static long minTime = 5000;
    final static float minDInstance = 10;
    LocationManager lm;
    String provider;
    boolean started=false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent i,int flags,int id)
    {
        Bundle b = i.getExtras();
        if(b!=null)
            provider = b.getString("provider");
        if(!started) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Looper.prepare();
                        MyLocationListener listener = new MyLocationListener();
                        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                        lm.requestLocationUpdates(provider, minTime, minDInstance, listener);
                        Looper.loop();
                    } catch (SecurityException ex) {
                        Log.e("Lokaciona greska", ex.getMessage().toString());
                    }
                }
            });
            thread.start();
            started = true;
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onCreate()
    {
       super.onCreate();

    }

    private class MyLocationListener implements LocationListener {
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList<myMarker> markeri;
        public MyLocationListener()
        {
            markeri = new ArrayList<myMarker>();
            dr.child("markers").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    markeri.add(dataSnapshot.getValue(myMarker.class));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                        markeri.remove(dataSnapshot.getValue(myMarker.class));
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        @Override
        public void onLocationChanged(Location location) {
            Intent i = new Intent("LOCATION_DETECTION");
            i.putExtra("longitude",location.getLongitude());
            i.putExtra("latitude",location.getLatitude());
            sendBroadcast(i);
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("latitude",""+location.getLatitude());
            map.put("longitude",""+location.getLongitude());
            dr.child("users").child(user.getUid()).updateChildren(map);
            checkDistance(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        public void checkDistance(Location location)
        {
            float[] result = new float[1];
            Iterator i = markeri.iterator();
            while(i.hasNext())
            {
                myMarker m = (myMarker)i.next();
                Location.distanceBetween(location.getLatitude(),
                        location.getLongitude(),m.latitude,m.longitude,result);
                if(result[0]<=200000)
                    pushNotification(m);
            }
        }

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        public void pushNotification(myMarker m)
        {
            if(!isActive()) {
                String title = m.tip + " u blizini!";
                String massage = "Na nekih 20 metara vam se nalazi " + m.tip;
                Notification.Builder notification = new Notification.Builder(getApplicationContext()).setContentTitle(title)
                        .setContentText(massage).setSmallIcon(R.drawable.avatar_icon);

                Intent notificationIntent = new Intent(getApplicationContext(), nesto.class);
                PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                        notificationIntent, 0);
                notification.setContentIntent(intent);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel nc = new NotificationChannel("channel1", "nesto", NotificationManager.IMPORTANCE_HIGH);
                    nm.createNotificationChannel(nc);
                    notification.setChannelId("channel1");
                }
                Notification not = notification.build();
                not.flags |= Notification.FLAG_AUTO_CANCEL;
                nm.notify(1, not);
            }
        }

        protected boolean isActive()
        {
            SharedPreferences sp = getSharedPreferences("OURINFO", MODE_PRIVATE);
            return sp.getBoolean("active",false);
        }
    }

}
