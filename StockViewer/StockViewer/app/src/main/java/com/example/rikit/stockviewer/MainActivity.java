package com.example.rikit.stockviewer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private DatabaseReference mDatabase;
    final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deletePortfolio();
                portfolioCardAdd(dataSnapshot);
                TextView name = (TextView) findViewById(R.id.usernameInfo);
                name.setText(dataSnapshot.child("users").child(SignIn.currentUser).child("username").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(messageListener);

    }

    public void portfolioCardAdd(DataSnapshot dataSnapshot){
        long size = dataSnapshot.child("users").child(SignIn.currentUser).child("stocks").getChildrenCount();
        int i = 0;
        String temp = "";
        for(DataSnapshot ds: dataSnapshot.child("users").child(SignIn.currentUser).child("stocks").getChildren()){
            temp += ds.getValue(String.class);
            if(i < size-1){
                temp += ",";
            }
            i++;
        }
        String url = "https://www.worldtradingdata.com/api/v1/stock?symbol="
                + temp + "&api_token=" + AddStock.stockAPI;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            addPortfolioCard(response);
                        }
                        catch (Exception E){}
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        queue.add(jsonObjectRequest);
    }
    public void addPortfolioCard(JSONObject json){
        ViewGroup layout = (ViewGroup) findViewById(R.id.portfolioLayout);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JSONArray data;
        try {
            data =  json.getJSONArray("data");
            JSONObject stock;
            int symbol = json.getInt("symbols_returned");
            for (int i = 0; i < json.getInt("symbols_returned"); i++) {
                stock = data.getJSONObject(i);
                ViewGroup custom = (ViewGroup) inflater.inflate(R.layout.portfolio_card, null);
                custom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewGroup viewGroup = (ViewGroup)v;
                        TextView ticker = (TextView) viewGroup.getChildAt(0);
                        Intent intent = new Intent(MainActivity.this, StockDetail.class);
                        intent.putExtra("Ticker", ticker.getText().toString());
                        startActivity(intent);
                    }
                });
                layout.addView(custom, i);

                TextView ticker = (TextView) custom.getChildAt(0);
                TextView name = (TextView) custom.getChildAt(1);
                TextView price = (TextView) custom.getChildAt(2);
                TextView currentPct = (TextView) custom.getChildAt(3);
                TextView afterPct = (TextView) custom.getChildAt(5);

                ticker.setText(stock.getString("symbol"));
                name.setText(stock.getString("name"));
                price.setText("$" + stock.getString("price"));

                if(!posNeg(stock.getString("change_pct"))){
                    currentPct.setBackgroundResource(R.drawable.red_box);
                }
                if(!posNeg(stock.getString("day_change"))){
                    afterPct.setTextColor(getResources().getColor(R.color.bloodRed ));
                }
                currentPct.setText(stock.getString("change_pct") + "%");
                afterPct.setText("$" + stock.getString("day_change"));
            }
        }
        catch (Exception E){}
    }

    public void deletePortfolio(){
        ViewGroup layout = (ViewGroup) findViewById(R.id.portfolioLayout);
        for(int i = 0; i < layout.getChildCount(); i++){
            layout.getChildAt(i).setVisibility(View.GONE);
        }
    }
    public static boolean posNeg(String str){
        //https://stackoverflow.com/questions/5034580/comparing-chars-in-java
        if(str.charAt(0) != '-'){
            return true;
        }
        else {
            return false;
        }
    }
    public void toAddStock(View v){
        startActivity(new Intent(MainActivity.this, AddStock.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                //https://stackoverflow.com/questions/22474584/remove-old-fragment-from-fragment-manager
                if(getSupportFragmentManager().findFragmentByTag(TAG) != null){
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(TAG)).commit();
                }
                break;
            case R.id.nav_about:
                //https://stackoverflow.com/questions/30062129/how-to-set-a-tag-to-a-fragment-in-android
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ChatFragment(), TAG).commit();
                break;
            case R.id.nav_logout:
                mDatabase.child("currentUser").setValue("0");
                startActivity(new Intent(MainActivity.this,SignIn.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

}
