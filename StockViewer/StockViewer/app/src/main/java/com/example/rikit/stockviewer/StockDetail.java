package com.example.rikit.stockviewer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class StockDetail extends AppCompatActivity {
    static String ticker;
    private static String companyName;
    double doubleChangePct, doubleYearHigh, doubleYearLow, today, doubleMarketCap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        ticker = getIntent().getStringExtra("Ticker");

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        realTime();
    }

    public void realTime(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.worldtradingdata.com/api/v1/stock?symbol="
                + ticker
                + "&api_token="
                + AddStock.stockAPI;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            infoDump(response);
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
    public void infoDump(JSONObject json){
        TextView detailName = (TextView) findViewById(R.id.detailName);
        TextView detailTicker = (TextView) findViewById(R.id.detaiTicker);
        TextView detailPrice = (TextView) findViewById(R.id.detailPrice);
        TextView detailChange = (TextView) findViewById(R.id.detailChange);

        TextView dayOpen = (TextView) findViewById(R.id.dayOpen);
        TextView yestClose = (TextView) findViewById(R.id.yestClose);
        TextView dayHigh = (TextView) findViewById(R.id.dayHigh);
        TextView dayLow = (TextView) findViewById(R.id.dayLow);
        TextView yearHigh = (TextView) findViewById(R.id.yearHigh);
        TextView yearLow = (TextView) findViewById(R.id.yearLow);
        try {
            JSONArray data = json.getJSONArray("data");
            JSONObject stock = data.getJSONObject(0);

            detailName.setText(stock.getString("name"));
            companyName = stock.getString("name");
            detailTicker.setText(stock.getString("symbol"));
            detailPrice.setText("$"+stock.getString("price"));
            today = Double.parseDouble(stock.getString("price"));
            String temp = "$"+stock.getString("day_change") + " (" + stock.getString("change_pct") + "%)";
            doubleChangePct = Double.parseDouble(stock.getString("change_pct"));
            detailChange.setText(temp);

            if(!MainActivity.posNeg(stock.getString("day_change"))){
                detailPrice.setTextColor(getResources().getColor(R.color.bloodRed));
                detailChange.setTextColor(getResources().getColor(R.color.bloodRed ));
            }

            dayOpen.setText("Day Open: $"+stock.getString("price_open"));
            yestClose.setText("Close Price: $"+stock.getString("close_yesterday"));
            dayHigh.setText("Day High: $"+stock.getString("day_high"));
            dayLow.setText("Day Low: $"+stock.getString("day_low"));
            yearHigh.setText("Year High: $"+stock.getString("52_week_high"));
            doubleYearHigh = Double.parseDouble(stock.getString("52_week_high"));
            yearLow.setText("Year Low: $"+stock.getString("52_week_low"));
            doubleYearLow = Double.parseDouble(stock.getString("52_week_low"));
            doubleMarketCap = Double.parseDouble(stock.getString("market_cap"));

            TextView rating = (TextView) findViewById(R.id.textView14);
            rating.setText(confidenceRating() + "%");
        }
        catch (Exception e){}
    }

    public static String getCompanyName(){
        return companyName;
    }

    public int confidenceRating(){
        int rating = 50;
        if(doubleChangePct > 0){
            rating += 3;
            if(doubleChangePct > 7){
                rating += 5;
            }
        }
        else{
            rating -= 5;
            if(doubleChangePct < 7){
                rating -= 7;
            }
        }

        if(doubleYearHigh - today > doubleYearLow - today){
            rating += 7;
        }
        else{
            rating -= 7;
        }

        if(doubleMarketCap/10000000 > 5000){
            rating += 6;
            if(doubleMarketCap/10000000 > 10000){
                rating += 7;
            }
            if(doubleMarketCap/10000000 > 100000){
                rating += 10;
            }
        }
        else{
            rating -=3;
        }
        return rating;
    }

    public void toNews(View v){
        startActivity(new Intent(StockDetail.this, News.class));
    }

    public void toGraph(View v){
        startActivity(new Intent(StockDetail.this, GraphActivity.class));
    }
}
