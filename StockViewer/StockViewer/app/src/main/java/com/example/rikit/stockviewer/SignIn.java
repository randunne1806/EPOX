package com.example.rikit.stockviewer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    DatabaseReference mDatabase;
    public static String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        TextView clickText = (TextView) findViewById(R.id.clickText);
        clickText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        ValueEventListener messListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.child("currentUser").getValue(String.class);
                if(!currentUser.equals("0")){
                    startActivity(new Intent(SignIn.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(messListener);
    }



    public void onClick(View v){
       //https://stackoverflow.com/questions/37629346/can-i-get-value-without-using-event-listeners-in-firebase-on-android
        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EditText email = (EditText) findViewById(R.id.email);
                String emailStr = email.getText().toString();
                EditText password = (EditText) findViewById(R.id.password);
                String passStr = password.getText().toString();
                boolean noMatch = true;
                for(DataSnapshot ds: dataSnapshot.child("users").getChildren())
                {
                    //https://stackoverflow.com/questions/43696281/datasnapshot-getkey-not-returning-the-actual-push-key=
                    if(ds.getValue(User.class).getUsername().equals(emailStr) &&
                            ds.getValue(User.class).getPassword().equals(passStr))
                    {
                        currentUser = ds.getKey();
                        noMatch = false;
                        startActivity(new Intent(SignIn.this, MainActivity.class));
                        finish();
                    }
                }
                if(noMatch){
                    Toast.makeText(SignIn.this, "Email/Password does not match", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
       mDatabase.addListenerForSingleValueEvent(messageListener);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mDatabase.child("currentUser").setValue(currentUser);
    }
}
