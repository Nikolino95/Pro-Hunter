package com.example.nikola.prohunter;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class Friends extends AppCompatActivity {

    BroadcastReceiver br;
    BluetoothAdapter ba;
    ListView lw;
    ArrayList<String> uredjaji;
    ArrayList<String> adrese_uredjaja;
    AcceptThread acceptThread;
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int SOCKET_CONECTED=3;
    public static UUID APP_UUID;
    public static Handler handler;
    boolean serverMode=true;
    public static ConnectionThread ct;
    int REQUEST_ENABLE_BT;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference meReference =FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
    User me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        REQUEST_ENABLE_BT = 100;
        meReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me=dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FloatingActionButton but=findViewById(R.id.refresh);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ba.isDiscovering())
                    ba.startDiscovery();
            }
        });
        uredjaji = new ArrayList<String>();
        adrese_uredjaja=new ArrayList<String>();
        lw=findViewById(R.id.listaBluettoth);
        int i1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        ba = BluetoothAdapter.getDefaultAdapter();
        if(ba == null)
            finish();
        APP_UUID=(UUID.fromString("00000000-0000-1000-8000-00805F9B34FB"));
        handler=new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case SOCKET_CONECTED:
                        ConnectionThread ct=(ConnectionThread) msg.obj;
                        ct.write(user.getUid().getBytes());
                        break;
                    case MESSAGE_READ:
                        String data=(String)msg.obj;
                        final DatabaseReference dr= FirebaseDatabase.getInstance().getReference().child("users").child(data);
                        dr.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                final User u=dataSnapshot.getValue(User.class);
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(Friends.this);
                                builder1.setMessage("Korisnik "+u.username+" vam salje zahtev za prijateljstvo");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Prihvatam",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if(u.friends == null)
                                                    u.friends = new HashMap<String,String>();
                                                u.friends.put(user.getUid(),me.username);
                                                dr.setValue(u);
                                                if(me.friends == null)
                                                    me.friends = new HashMap<String,String>();
                                                me.friends.put(dataSnapshot.getKey().toString(),u.username);
                                                meReference.setValue(me);
                                            }
                                        });

                                builder1.setNegativeButton(
                                        "Odbijam",
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
                        break;
                    /*case MESSAGE_WRITE:
                        ConnectionThread cth=(ConnectionThread) msg.obj;
                        cth.write("Zahtev za prijateljstvo".getBytes());*/
                }
            }
        };


        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Friends.this);
                builder1.setMessage("Da li zelite da posaljete zathev za orijateljstvo korisniku "+uredjaji.get(position));
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Da",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                serverMode=false;
                                new ConnectThread(adrese_uredjaja.get(position),handler).start();
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
        });
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(!isContains(d.getAddress())) {
                        uredjaji.add(d.getName());
                        adrese_uredjaja.add(d.getAddress());
                    }
                    lw.setAdapter(new ArrayAdapter<String>(Friends.this,android.R.layout.simple_list_item_1,uredjaji));
                    Toast.makeText(getBaseContext(),d.getName(),Toast.LENGTH_LONG).show();
                }
                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {
                        Toast.makeText(getBaseContext(), "Pretraga gotova", Toast.LENGTH_SHORT).show();
                }

            }


        };

        BottomNavigationView bottomNavigationView=findViewById(R.id.barbluetooth);
        Menu menu=  bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_home:
                        Intent i=new Intent(Friends.this,Home.class);
                        startActivity(i);
                        break;
                    case R.id.navigation_map:
                        Intent i2=new Intent(Friends.this,nesto.class);
                        startActivity(i2);
                        break;
                    case R.id.navigation_bluetooth:
                        break;
                    case R.id.navigation_profile:
                        Intent i3=new Intent(Friends.this,Profile.class);
                        startActivity(i3);
                        break;
                }
                return true;
            }
        });
        /*Set<BluetoothDevice> set=ba.getBondedDevices();
        if(set.size()>0)
        {
            for(BluetoothDevice b:set)
                Toast.makeText(getBaseContext(),b.getName(),Toast.LENGTH_LONG).show();
        }*/

        /*int pc = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        pc += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if(pc !=0)
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION},1001);*/


        IntentFilter f = new IntentFilter();

        f.addAction(BluetoothDevice.ACTION_FOUND);
        f.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(br,f);

        if(!ba.isEnabled())
        {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,REQUEST_ENABLE_BT);
        }

        else
        {
            acceptThread = new AcceptThread();
            acceptThread.start();
            ba.startDiscovery();
        }
    }

    private boolean isContains(String name) {
        Iterator i = uredjaji.iterator();
        while(i.hasNext())
        {
            String s = i.next().toString();
            if(s.equals(name))
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_ENABLE_BT ) {
            if (resultCode == RESULT_CANCELED)
                return;

            if (resultCode == RESULT_OK) {
                acceptThread = new AcceptThread();
                acceptThread.start();
            }
        }
    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("JanaiNikola", APP_UUID);
            } catch (IOException e) {
                Log.e("1", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("2", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);

                    try {
                        mmServerSocket.close();
                    }
                    catch (IOException ioexception)
                    {

                    }
                    break;
                }

            }
        }


        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("3", "Could not close the connect socket", e);
            }
        }
    }
    public class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(String s, Handler handler) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = ba.getRemoteDevice(s);

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = mmDevice.createRfcommSocketToServiceRecord(APP_UUID);
            } catch (IOException e) {
                Log.e("1", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            ba.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("1", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("1", "Could not close the client socket", e);
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket mmSocket) {
        if(!serverMode) {
            ConnectionThread ct = new ConnectionThread(mmSocket, handler);
            handler.obtainMessage(SOCKET_CONECTED, ct).sendToTarget();
        }
        else
        {
            ct=new ConnectionThread(mmSocket,handler);
            ct.start();
        }
    }




    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(br);
    }
}
