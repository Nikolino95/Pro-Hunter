package com.example.nikola.prohunter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class viewEvent extends AppCompatActivity {

    DatabaseReference db=FirebaseDatabase.getInstance().getReference();
    StorageReference st=FirebaseStorage.getInstance().getReference();
    ImageView slika;
    String kljuc;
    String autor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Intent i=getIntent();
        kljuc=i.getStringExtra("kljuc");
        autor=i.getStringExtra("autor");

        db.child("events").child(autor).child(kljuc).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e=dataSnapshot.getValue(Event.class);
                updateUI(e);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void updateUI(Event e)
    {
        FirebaseAuth  mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Button b=findViewById(R.id.ucestvujUDogadjajuPrikaz);
        /*if(user.getUid().equals(e.user))
            b.setVisibility(View.INVISIBLE);
        else
            b.setVisibility(View.VISIBLE);*/
        TextView naziv=findViewById(R.id.nazivDogadjajaPrikaz);
        TextView mesto=findViewById(R.id.mestoDogadjajaPrikaz);
        TextView datum=findViewById(R.id.datePrikaz);
        slika=findViewById(R.id.slikaEventPrikaz);
        naziv.setText(e.tip);
        mesto.setText(e.name);
        datum.setText(e.date+"   "+e.time);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        String putanja = "images/events/"+autor+"/"+kljuc;
            StorageReference imgRef = mStorageRef.child(putanja);
            imgRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                    slika.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG);
                }
            });


//gs://prohunter-56057.appspot.com/images/events/QmAaEPCWVZfQAgvVXwNss6sAMJv1/-LJfgyBAPQ5hOvt9_8R9



    }
}
