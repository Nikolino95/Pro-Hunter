package com.example.nikola.prohunter;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class User implements Comparable {

    public User(String user,String mail,String ph, String n,String ln,String i)
    {
        id=i;
        username=user;
        email = mail;
        phone = ph;
        name = n;
        lastname = ln;
        poeni = 0;
        online=false;
        friends = new HashMap<String, String>();
    }
    public User ()
    {

    }
    @Exclude
    public String id;
    public String username;
    public String email;
    public String phone;
    public String name;
    public String lastname;
    public String longitude;
    public String latitude;
    public boolean online;
    public HashMap<String,String> friends;
    public long poeni;

    @Override
    public int compareTo(@NonNull Object o) {
        User u = (User)o;
        if(this.poeni == u.poeni)
            return 0;
        else if(this.poeni > u.poeni)
            return -1;
        else
            return 1;
    }
}
