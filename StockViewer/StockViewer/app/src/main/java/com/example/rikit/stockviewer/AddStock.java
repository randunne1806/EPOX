package com.example.rikit.stockviewer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AddStock extends AppCompatActivity {
    DatabaseReference mDatabase;
    //other api key: wYgCPx1qPuzBsP9ocrDfxW6G0nOwtXEAa8NAQMM45hISULMlk5iOdw9nWQMI
    public int callsReturned;
    public static final String stockAPI = "337fLQryhrSui0V9TztWfokfmzmsondR3bgaEQGaMYeBx5zFnSt4spsQ9EKx";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.addStockToolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void searchCall(View v){
        //I Used this entire guide for HTTP listener
        //https://developer.android.com/training/volley/
        RequestQueue queue = Volley.newRequestQueue(this);
        EditText editText = (EditText) findViewById(R.id.search_bar);
        String searchTerm = editText.getText().toString();
        String url = "https://www.worldtradingdata.com/api/v1/stock_search?search_term="
                + searchTerm
                + "&search_by=symbol,name&limit=5&page=1&api_token="
                + AddStock.stockAPI;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            addStockCard(response);
                            callsReturned = Integer.parseInt(response.getString("total_returned"));
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

        Button search = (Button) findViewById(R.id.button);
        search.setText(R.string.done);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneFunction(v);
            }
        });

    }

    public void addStockCard(JSONObject json){
        try{
            ViewGroup layout = (ViewGroup) findViewById(R.id.linlayout);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for(int i = 0; i < 5; i++) {
                String name = json.getJSONArray("data").getJSONObject(i).getString("name");
                String symbol = json.getJSONArray("data").getJSONObject(i).getString("symbol");
                ViewGroup custom = (ViewGroup) inflater.inflate(R.layout.add_stock_card, null);
                layout.addView(custom, i);

                TextView nameView = (TextView) custom.getChildAt(0);
                TextView symbolView = (TextView) custom.getChildAt(1);
                ImageButton checkPlus = (ImageButton) custom.getChildAt(2);

                nameView.setText(name);
                symbolView.setText(symbol);
                checkPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgClick(v);
                    }
                });
            }
        }
        catch (Exception E){}
    }

    public void imgClick(View v){
        ImageButton button = (ImageButton)v;
        button.setImageResource(R.drawable.check);
        //https://stackoverflow.com/questions/5195321/remove-an-onclick-listener
        button.setOnClickListener(null);
        ViewGroup viewGroup = (ViewGroup) button.getParent();
        TextView tv = (TextView) viewGroup.getChildAt(1);
        //https://firebase.google.com/docs/database/web/lists-of-data
        mDatabase.child("users").child(SignIn.currentUser).child("stocks").push().setValue(tv.getText().toString());
    }

    public void doneFunction(View v){
        ViewGroup layout = (ViewGroup) findViewById(R.id.linlayout);
        for(int i = 0; i < callsReturned; i++){
            layout.getChildAt(i).setVisibility(View.GONE);
        }
        Button button = (Button)v;
        button.setText(R.string.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCall(v);
            }
        });
    }
}
