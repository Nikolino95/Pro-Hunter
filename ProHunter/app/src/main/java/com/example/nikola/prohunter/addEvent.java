package com.example.nikola.prohunter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.usage.UsageEvents;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class addEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Event event;
    Spinner s;
    boolean change;
    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        event = new Event();
        change = false;
        s=findViewById(R.id.spiner);
        ArrayAdapter<CharSequence> aa=ArrayAdapter.createFromResource(this,R.array.lovNa,android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(aa);
        s.setOnItemSelectedListener(this);
        ImageView d = findViewById(R.id.dateImage);

        ImageView slika = findViewById(R.id.slikaEvent);
        slika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(addEvent.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                    Intent i = new Intent();
                    i.setType("image/^");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(i, PICK_IMAGE_REQUEST);

            }
        });
        final Calendar c = Calendar.getInstance();
        final TextView dp = findViewById(R.id.date);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                c.set(Calendar.YEAR,year);
                c.set(Calendar.MONTH,month);
                c.set(Calendar.DAY_OF_MONTH,day);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                dp.setText(sdf.format(c.getTime()));
                event.date = dp.getText().toString();
            }

        };
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(addEvent.this,date,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        final TextView time=findViewById(R.id.timeT);
        final TimePickerDialog.OnTimeSetListener tim=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                c.set(Calendar.HOUR_OF_DAY,i);
                c.set(Calendar.MINUTE,0);
                String formar="HH:mm";
                SimpleDateFormat sdf=new SimpleDateFormat(formar,Locale.getDefault());
                time.setText(sdf.format(c.getTimeInMillis()));
                event.time = time.getText().toString();
            }
        };
        ImageView clo=findViewById(R.id.timeImage);
        clo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(addEvent.this,tim,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
            }
        });

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                event.name = place.getName().toString();
                event.latitude = place.getLatLng().latitude;
                event.longitude = place.getLatLng().longitude;
            }

            @Override
            public void onError(Status status) {

                    // TODO: Handle the error.
                    Log.i("2", "An error occurred: " + status);
            }
        });


        Button dodaj = findViewById(R.id.dodajDogadjaj);
        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isCompleteEvent(event))
                    return;
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseDatabase db=FirebaseDatabase.getInstance();
                DatabaseReference mr=db.getReference();
                String key = db.getReference().push().getKey().toString();
                Toast.makeText(getBaseContext(),key,Toast.LENGTH_LONG).show();
                event.id = key;
                event.user = user.getUid().toString();
                mr.child("events").child(user.getUid()).child(key).setValue(event);
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                StorageReference riversRef = mStorageRef.child("images/events/"+user.getUid()+"/"+key);
                ImageView imageView = findViewById(R.id.slikaEvent);
                Bitmap bmp=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                ByteArrayOutputStream baos1=new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG,100,baos);
                byte[] slika=baos.toByteArray();
                Bitmap bmp1 =Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(slika, 0, slika.length),pxFromDp(150),pxFromDp(100),false);
                bmp1.compress(Bitmap.CompressFormat.PNG,100,baos1);
                byte[] slika1=baos1.toByteArray();

                riversRef.putBytes(slika1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        /*Intent i=new Intent(addEvent.this,viewEvent.class);
                        i.putExtra("kljuc",event.id);
                        i.putExtra("autor",event.user);
                        startActivity(i);*/
                        finish();
                    }
                });


            }
        });


    }

    protected int pxFromDp( final float dp) {
        return (int) (dp * this.getResources().getDisplayMetrics().density);
    }

    protected boolean isCompleteEvent(Event e)
    {


        if(e.date != null && e.time != null && e.name != null  && e.tip != null && change)
            return true;
        return false;
    }

    public void onItemSelected(AdapterView<?> parent, View view,int pos,long id)
    {
        event.tip = parent.getSelectedItem().toString();
    }
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Bitmap bitmap;

            ImageView imageView = findViewById(R.id.slikaEvent);

            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageView.setMaxWidth(bitmap.getWidth());
                imageView.setMaxHeight(bitmap.getHeight());
                imageView.setImageBitmap(bitmap);
                change = true;
            }
            catch (IOException e) {
                e.printStackTrace();
            }


            //imageView.setImageURI(data.getData());
        }
    }
}
