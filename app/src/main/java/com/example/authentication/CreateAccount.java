package com.example.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText firstName;
    private EditText lastName;
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

        firstName = findViewById(R.id.createFirstName);
        lastName = findViewById(R.id.createLastName);
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
        final String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        String retypePasswordString = retypePassword.getText().toString();
        final String firstString = firstName.getText().toString();
        final String lastString = lastName.getText().toString();

        if (TextUtils.isEmpty(emailString) || TextUtils.isEmpty(passwordString)|| TextUtils.isEmpty(retypePasswordString) || TextUtils.isEmpty(firstString) || TextUtils.isEmpty(lastString))
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
            TextView lessThan = findViewById(R.id.textView4);
            lessThan.setText(R.string.stringMessage);
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
                        String fullName = firstString + " " + lastString;

                        //user id from auth
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        //put info into hashmap
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("fullName", fullName);
                        hashMap.put("uid", uid);

                        //firebase data instance
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        //path stored data to users
                        DatabaseReference reference = database.getReference("Users");
                        //put hashmap data in database
                        reference.child(uid).setValue(hashMap);

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build();
                        user.updateProfile(profileUpdates);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", fullName);
                        Fragment frag = new KunektProfile();
                        frag.setArguments(bundle);

                        Intent passInfo = new Intent(CreateAccount.this, MainActivity.class);
                        passInfo.putExtra("name", fullName);
                        startActivity(passInfo);
                    }
                }
            });
        }
        else {
            Toast.makeText(CreateAccount.this, "ERROR", Toast.LENGTH_LONG).show();
        }
    }
}
