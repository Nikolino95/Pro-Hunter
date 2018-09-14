package com.example.nikola.prohunter;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn=(Button)findViewById(R.id.btn_sign_up);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getBaseContext(),SignUpActivity.class);
                startActivity(i);
            }
        });
        TextView password = findViewById(R.id.forgot_pass);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(),ForgotPasswordActivity.class);
                startActivityForResult(i,1);
            }
        });

        mAuth=FirebaseAuth.getInstance();

//        final String email="nikola1@gmail.com";
//        final String password1="volimjanu11";

        /*mAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("1", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("2", "createUserWithEmail:failure", task.getException());


                        }

                        // ...
                    }
                });*/




        Button btn1=(Button)findViewById(R.id.btn_login);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*FirebaseDatabase db=FirebaseDatabase.getInstance();
                DatabaseReference mr=db.getReference();
                String nesto=mr.push().getKey();
                mr.child("na").child(nesto).setValue("radiiii");*/



                EditText emailt = findViewById(R.id.input_username);
                if(emailt.getText().toString().equals(""))
                {
                    Toast.makeText(getBaseContext(),"Unesite email",Toast.LENGTH_LONG).show();
                    return;
                }
                EditText pass = findViewById(R.id.input_passwod);
                if(pass.getText().toString().equals(""))
                {
                    Toast.makeText(getBaseContext(),"Unesite password",Toast.LENGTH_LONG).show();
                    return;
                }

               SignIn(emailt.getText().toString(),pass.getText().toString());



            }
        });
    }

    public void SignIn(String email,String password)
    {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("1", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("users");
                            dr.child(user.getUid()).child("online").setValue(true);
                            dr.child(user.getUid()).child("online").onDisconnect().setValue(false);
/*                            FirebaseDatabase db=FirebaseDatabase.getInstance();
                            final DatabaseReference mr=db.getReference();
                            User u1=new User("beba","mala","555","bebica","slatkica","jedina");
                            mr.child("users").child("proba2").setValue(u1);
                            mr.child("users").child("proba2").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User u = dataSnapshot.getValue(User.class);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });*/
                            //Toast.makeText(getBaseContext(),user.getEmail(),Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getBaseContext(),Home.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException invalidEmail)
                            {
                                Toast.makeText(getBaseContext(),"los email",Toast.LENGTH_LONG).show();
                            }
                            catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                            {
                                Toast.makeText(getBaseContext(),"los pass",Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            /*Log.w("2", "createUserWithEmail:failure", task.getException());
                            FirebaseUser user = mAuth.getCurrentUser();*/


                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK)
            Toast.makeText(this,"Poslat vam je mejl za promenu sifre!",Toast.LENGTH_LONG).show();
        else if(resultCode==Activity.RESULT_CANCELED)
            Toast.makeText(this,"Nevalidan mejl",Toast.LENGTH_LONG).show();
    }
}
