package com.example.nikola.prohunter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class Profile extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toast.makeText(getBaseContext(),"ovo je profil activity",Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String id;
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!= null)
            id = b.getString("id");
        else
            id = user.getUid().toString();
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference mr=db.getReference();
        mr.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                u.id = dataSnapshot.getKey();
                updateUI(u);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        BottomNavigationView bottomNavigationView=findViewById(R.id.bar);
        Menu menu=  bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_home:
                        Intent i=new Intent(Profile.this,Home.class);
                        startActivity(i);
                        break;
                    case R.id.navigation_map:
                        Intent i2=new Intent(Profile.this,nesto.class);
                        startActivity(i2);
                        break;
                    case R.id.navigation_bluetooth:
                        Intent i3=new Intent(Profile.this,Friends.class);
                        startActivity(i3);
                        break;
                    case R.id.navigation_profile:
                        break;
                }
                return true;
            }
        });

    }

    TextView username,prijatelji,ime,prezime,telefon,poeni;
    ImageView slika;
    protected void updateUI(final User user)
    {
        username = findViewById(R.id.usernameProfile);
        String un =  username.getText().toString();
        un+=" "+user.username;
        username.setText(un);

        prijatelji = findViewById(R.id.prijateljiProfile);
        prijatelji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this,ListaPrijatelja.class);
                i.putExtra("friends",user.friends);
                startActivity(i);
            }
        });

        ime = findViewById(R.id.imeProfile);
        un = ime.getText().toString();
        un+=" "+user.name;
        ime.setText(un);

        prezime = findViewById(R.id.prezimeProfile);
        un = prezime.getText().toString();
        un+=" "+user.lastname;
        prezime.setText(un);

        telefon = findViewById(R.id.telefonProfile);
        un = telefon.getText().toString();
        un+=" "+user.phone;
        telefon.setText(un);

        poeni = findViewById(R.id.poeniProfile);
        un = poeni.getText().toString();
        un+=" "+user.poeni;
        poeni.setText(un);

        Button rang = findViewById(R.id.DugmeRangLista);
        rang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this,RangLista.class);
                i.putExtra("korisnik",user.id);
                startActivity(i);
            }
        });

        slika = findViewById(R.id.pictureProfile);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imgRef = mStorageRef.child("images/"+user.id);
                //"images/"+mAuth.getCurrentUser().getUid().toString());
        //"images/ATMSAxdn18T16abEEtt4rZwTJJA3"
        //gs://prohunter-56057.appspot.com/images/QmAaEPCWVZfQAgvVXwNss6sAMJv1.png
        //gs://prohunter-56057.appspot.com/images/events/QmAaEPCWVZfQAgvVXwNss6sAMJv1/-LJf_Xzot9UbKUXsUazG



        imgRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                slika.setImageBitmap(bm);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
