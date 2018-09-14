package com.example.nikola.prohunter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText username;
    private FirebaseAuth mAuth;
    private EditText confirmPass;
    private  EditText pass;
    private EditText email;
    private EditText phone;
    private EditText name;
    private EditText lastname;
    private StorageReference mStorageRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Button btn = findViewById(R.id.btn_signup);
        ImageView slika = findViewById(R.id.picture);
        username = findViewById(R.id.input_username_sign_up);
        mAuth = FirebaseAuth.getInstance();
        confirmPass = findViewById(R.id.input_password_confirm_sign_up);
        pass = findViewById(R.id.input_password_sign_up);
        email = findViewById(R.id.input_email);
        phone = findViewById(R.id.input_phone);
        name = findViewById(R.id.input_name);
        lastname = findViewById(R.id.input_lastname);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        slika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/^");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, PICK_IMAGE_REQUEST);

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String confirm_pass=confirmPass.getText().toString();
                String Pass=pass.getText().toString();
                String Email=email.getText().toString();
                String Phone=phone.getText().toString();
                String Name=name.getText().toString();
                String LastName=lastname.getText().toString();
                String Username=username.getText().toString();

                if(confirm_pass.equals(Pass) && !confirm_pass.equals("") && !Email.equals("") && isEmail(Email)&& !Phone.equals("") && !Name.equals("") && !LastName.equals(""))
                    signUp(Username,Email,Pass,Phone,Name,LastName);


            }
        });
    }

    protected void signUp(final String username,final String email,final String pass,final String phone,final String name,final String lastname)
    {
          mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("1", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase db=FirebaseDatabase.getInstance();
                            DatabaseReference mr=db.getReference();
                            User u =new User(username,email,phone,name,lastname,user.getUid().toString());
                            mr.child("users").child(user.getUid().toString()).setValue(u);
                            StorageReference riversRef = mStorageRef.child("images/"+user.getUid());
                            ImageView imageView = findViewById(R.id.picture);
                            Bitmap bmp=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos=new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG,100,baos);
                            byte[] slika=baos.toByteArray();
                            riversRef.putBytes(slika)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Get a URL to the uploaded content
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle unsuccessful uploads
                                            // ...
                                        }
                                    });
                            Toast.makeText(getBaseContext(),user.getEmail().toString(),Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getBaseContext(),MainActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("2", "createUserWithEmail:failure", task.getException());


                        }

                        // ...
                    }
                });
    }
    protected boolean isEmail(String email)
    {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Bitmap bitmap;

            ImageView imageView = findViewById(R.id.picture);

            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageView.setMaxWidth(bitmap.getWidth());
                imageView.setMaxHeight(bitmap.getHeight());
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }


           //imageView.setImageURI(data.getData());
        }
    }
}
