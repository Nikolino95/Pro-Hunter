package com.example.nikola.prohunter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RangLIstaAdapter extends BaseAdapter{
    private static LayoutInflater inflater=null;
    Context context;
    ArrayList<User> korisnici;
    public RangLIstaAdapter(Context c, ArrayList<User> a)
    {
        context=c;
        korisnici=a;
        inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return korisnici.size();
    }

    @Override
    public Object getItem(int position) {
       return korisnici.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=convertView;
        if(v==null)
            v=inflater.inflate(R.layout.row_rang_lista,null);
        TextView username=v.findViewById(R.id.username_rang_lista);
        TextView rang=v.findViewById(R.id.redni_broj_rang_lista);
        TextView poeni=v.findViewById(R.id.poeni_rang_lista);
        User u=korisnici.get(position);
        username.setText(u.username);
        rang.setText(""+(position+1)+".   ");
        poeni.setText((""+ u.poeni));

        return v;
    }
}
