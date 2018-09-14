package com.example.nikola.prohunter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.EventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
//        final EditText confirmPass = findViewById(R.id.input_passwod_confirm_sign_up);
        final Button btn = findViewById(R.id.btn_confirm);
        final EditText email = findViewById(R.id.input_email);
//        final  EditText pass = findViewById(R.id.input_passwod_sign_up);
        mAuth = FirebaseAuth.getInstance();

       /* email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(confirmPass.getText().toString().equals(pass.getText().toString()) && !confirmPass.getText().toString().equals("")
                        && !email.getText().toString().equals(""))
                    btn.setEnabled(true);
                else
                    btn.setEnabled(false);
            }
        });
        btn.setEnabled(false);
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {




                if(!confirmPass.getText().toString().equals(pass.getText().toString()))
                {

                    btn.setEnabled(false);
                }
                else
                {
                    if(!email.getText().toString().equals(""))
                        btn.setEnabled(true);
                }
            }
        };
        confirmPass.addTextChangedListener(tw);
        pass.addTextChangedListener(tw);*/
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                        setResult(Activity.RESULT_OK);
                                else
                                        setResult(Activity.RESULT_CANCELED);
                                finish();

                            }
                        });
            }
        });
    }
}
