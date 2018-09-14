package com.example.nikola.prohunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{
    Context context;
    Event[] dogadjaj;
    public interface OnItemClickListener {
        void onItemClick(Event item);
    }
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        public View imageView;
        public MyViewHolder(View i)
        {
            super(i);
            imageView=i;
        }
    }
    //private static LayoutInflater inflater=null;
    public MyAdapter(Event[] e)

    {
        dogadjaj=e;
        //inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    /*@Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        //RecyclerView.ViewHolder holder=null;
        View v=convertView;
        ImageView imageView=null;
        if(v==null) {
            v = inflater.inflate(R.layout.row_home_page, null);
            imageView = v.findViewById(R.id.slikaDogadjajaHome);
            v.setTag(imageView);
        }
        else
            imageView=(ImageView)v.getTag();
        final ImageView im=imageView;
        TextView naziv=v.findViewById(R.id.nazivDogadjajaHome);
        TextView datum=v.findViewById(R.id.datumDogadjajaHome);
        Event e=dogadjaj[position];
        naziv.setText(e.tip);
        datum.setText(e.date);


        final String putanja = "images/events/"+e.user+"/"+e.id;
        final StorageReference imgRef = mStorageRef.child(putanja);
            *//*imgRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"feilure",Toast.LENGTH_SHORT);
                }
            });*//*
           imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
               @Override
               public void onSuccess(Uri uri) {

                   Glide.with(context).load(uri).centerCrop().into(im);
                   Log.e("poruka",position+putanja);
               }
           });
           try {
               wait(2000);
           }
           catch (Exception ex)
           {

           }
        return v;
    }*/

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_home_page,viewGroup,false);
        MyViewHolder m=new MyViewHolder(v);
        return m;

    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder myViewHolder, int i)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Event e=dogadjaj[i];
        final ImageView slika=myViewHolder.imageView.findViewById(R.id.slikaDogadjajaHome);
        final String putanja = "images/events/"+e.user+"/"+e.id;
        final StorageReference imgRef = mStorageRef.child(putanja);
        imgRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    slika.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                //Toast.makeText(context,"feilure",Toast.LENGTH_SHORT);
                }
            });
        TextView naziv=myViewHolder.imageView.findViewById(R.id.nazivDogadjajaHome);
        TextView datum=myViewHolder.imageView.findViewById(R.id.datumDogadjajaHome);
        TextView mesto=myViewHolder.imageView.findViewById(R.id.mestoDogadjajaHome);
        mesto.setText(e.name);
        naziv.setText(e.tip);
        datum.setText(e.date);
        Button b=myViewHolder.imageView.findViewById(R.id.dugmeHome);
        /*if(user.getUid().equals(e.user))
            b.setVisibility(View.INVISIBLE);
        else
            b.setVisibility(View.VISIBLE);*/
    }

    @Override
    public int getItemCount()
    {
        return dogadjaj.length;
    }
}
