package com.example.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private EditText retypePassword;
    private Button signUp;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth= FirebaseAuth.getInstance();

        email = findViewById(R.id.createUsername);
        password = findViewById(R.id.createPassword);
        retypePassword = findViewById(R.id.retypePassword);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null)
                {
                    startActivity(new Intent(CreateAccount.this, MainActivity.class));
                }
                else {
                    Toast.makeText(CreateAccount.this, "Please Log in.", Toast.LENGTH_LONG).show();
                }
            }
        };

        signUp = findViewById(R.id.createAccountButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpAccount();
            }
        });

        LoginPage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    public void LoginPage() {
        TextView returnHome = findViewById(R.id.ReturnHome);
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccount.this, MainActivity.class));
            }
        });
    }

    public void SignUpAccount () {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        String retypePasswordString = retypePassword.getText().toString();

        if (TextUtils.isEmpty(emailString) || TextUtils.isEmpty(passwordString)|| TextUtils.isEmpty(retypePasswordString))
        {
            Toast.makeText(CreateAccount.this, "Some fields are empty.", Toast.LENGTH_SHORT).show();
        }
        else if (!passwordString.equals(retypePasswordString))
        {
            Toast.makeText(CreateAccount.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }
        else if (passwordString.length() < 6)
        {
            Toast.makeText(CreateAccount.this, "Passwords must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
        }
        else if (!emailString.isEmpty() && !passwordString.isEmpty())
        {
            mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful())
                    {
                        Toast.makeText(CreateAccount.this, "Log in Error. Please try again", Toast.LENGTH_LONG).show();
                    }
                    else {
                        startActivity(new Intent(CreateAccount.this, MainActivity.class));
                    }
                }
            });
        }
        else {
            Toast.makeText(CreateAccount.this, "ERROR", Toast.LENGTH_LONG).show();
        }
    }
}
