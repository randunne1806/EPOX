package com.example.rikit.stockviewer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONObject;

public class SignUp extends AppCompatActivity {
    private DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.signUpToolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView clickable = (TextView) findViewById(R.id.signUpClick);
        clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

        mDataBase = FirebaseDatabase.getInstance().getReference();
    }
    public void signUpClick(View v){
        EditText email = (EditText) findViewById(R.id.signUpEmail);
        EditText pass = (EditText) findViewById(R.id.signUpPass);
        EditText pass2 = (EditText) findViewById(R.id.signUpPass2);
        if(pass.getText().toString().equals(pass2.getText().toString()) &&
                !email.getText().toString().equals("") && !pass.getText().toString().equals("")){
            User newUser = new User(email.getText().toString(), pass.getText().toString());
            String temp = mDataBase.push().getKey();
            mDataBase.child("users").child(temp).setValue(newUser);
            mDataBase.child("currentUser").setValue(temp);
            SignIn.currentUser = temp;
            startActivity(new Intent(SignUp.this, MainActivity.class));
        }
        else {
            Toast.makeText(SignUp.this, "Password does not match", Toast.LENGTH_SHORT).show();
        }
    }
}
