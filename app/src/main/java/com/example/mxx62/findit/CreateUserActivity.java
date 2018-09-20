package com.example.mxx62.findit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText registerEmail;
    private EditText registerPassword;
    private Button registerBtn;
    private ProgressDialog progressDialogss;
    private TextView loginLink;
    private ImageView user_pic;
    final private int CAMERA_FLAG = 0;
    final private int GALLERY_FLAG = 1;
    Uri imageuri;
    DatabaseReference reference;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialogss = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog); //ZXY Aug 18 added

        registerEmail = (EditText)findViewById(R.id.register_email);
        registerPassword = (EditText)findViewById(R.id.register_password);
        registerBtn = (Button)findViewById(R.id.Register);
        loginLink = (TextView) findViewById(R.id.link_login);

        registerBtn.setOnClickListener(this);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LogInActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


    }

    private void registerUser(){
        final String email = registerEmail.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        registerBtn.setEnabled(false); // zxy added
        progressDialogss.setMessage("Registering...");
        progressDialogss.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Register Successful", Toast.LENGTH_LONG).show();
                            progressDialogss.dismiss();
                            finish();
                            startActivity(new Intent(CreateUserActivity.this, LogInActivity.class));
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Could not resiger, please try again", Toast.LENGTH_LONG).show();
                            registerBtn.setEnabled(true); // zxy added
                        }
                    }
                });



    }
    @Override
    public void onClick(View v) {
        if (registerBtn == v){
            registerUser();
        }
    }
}
