package com.example.rikit.stockviewer;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class GraphActivity extends AppCompatActivity {
    private LineChart chart;
    private String ticker;
    private TextView graphTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        graphTitle = findViewById(R.id.graphTitle);
        ticker = StockDetail.ticker;

        historicWeek();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onWeekClick(View v) {
        historicWeek();
        graphTitle.setText("Last Week's Share Prices");
    }

    public void onMonthClick(View v) {
        historicMonth();
        graphTitle.setText("Last Month's Share Prices");
    }

    public void onYearClick(View v) {
        historicYear();
        graphTitle.setText("Last Year 's Share Prices");
    }

    public void historicWeek(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.worldtradingdata.com/api/v1/history?symbol="
                + ticker
                + "&api_token=337fLQryhrSui0V9TztWfokfmzmsondR3bgaEQGaMYeBx5zFnSt4spsQ9EKx";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            createWeekGraph(response);
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

    public void historicMonth(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.worldtradingdata.com/api/v1/history?symbol="
                + ticker
                + "&api_token=337fLQryhrSui0V9TztWfokfmzmsondR3bgaEQGaMYeBx5zFnSt4spsQ9EKx";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            createMonthGraph(response);
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

    public void historicYear(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.worldtradingdata.com/api/v1/history?symbol="
                + ticker
                + "&api_token=337fLQryhrSui0V9TztWfokfmzmsondR3bgaEQGaMYeBx5zFnSt4spsQ9EKx";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            createYearGraph(response);
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

    public void createWeekGraph(JSONObject json)
    {
        try{
            chart = (LineChart)findViewById(R.id.chart);
            Iterator<String> keys = json.getJSONObject("history").keys();
            JSONObject history = json.getJSONObject("history");
            float temp = 0;
            ArrayList<Entry> entries = new ArrayList<>();
            do {
                String date = keys.next();

                float open = (float)history.getJSONObject(date).getDouble("open");

                entries.add(new Entry(temp, open));
                temp++;
            } while (temp < 8);

            LineDataSet dataSet = new LineDataSet(entries, "Stocks");
            dataSet.setDrawValues(false);
            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            LineData data = new LineData();
            data.addDataSet(dataSet);

            chart.setData(data);
            chart.invalidate();

            YAxis rightAxis = chart.getAxisRight();
            YAxis leftAxis = chart.getAxisLeft();
            rightAxis.setEnabled(false);
            leftAxis.removeAllLimitLines();


            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            Legend legend = chart.getLegend();
            legend.setEnabled(false);

            chart.getAxisLeft().setDrawGridLines(false);
            chart.getXAxis().setDrawGridLines(false);

            chart.getDescription().setEnabled(false);
        }
        catch (Exception e){}
    }

    public void createMonthGraph(JSONObject json)
    {
        try{
            chart = (LineChart)findViewById(R.id.chart);
            Iterator<String> keys = json.getJSONObject("history").keys();
            JSONObject history = json.getJSONObject("history");
            float temp = 0;
            ArrayList<Entry> entries = new ArrayList<>();
            do {
                String date = keys.next();

                float open = (float)history.getJSONObject(date).getDouble("open");

                entries.add(new Entry(temp, open));
                temp++;
            } while (temp < 31);

            LineDataSet dataSet = new LineDataSet(entries, "Stocks");
            dataSet.setDrawValues(false);
            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            LineData data = new LineData();
            data.addDataSet(dataSet);

            chart.setData(data);
            chart.invalidate();

            YAxis rightAxis = chart.getAxisRight();
            YAxis leftAxis = chart.getAxisLeft();
            rightAxis.setEnabled(false);
            leftAxis.removeAllLimitLines();


            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            Legend legend = chart.getLegend();
            legend.setEnabled(false);

            chart.getAxisLeft().setDrawGridLines(false);
            chart.getXAxis().setDrawGridLines(false);

            chart.getDescription().setEnabled(false);
        }
        catch (Exception e){}
    }

    public void createYearGraph(JSONObject json)
    {
        try{
            chart = (LineChart)findViewById(R.id.chart);
            Iterator<String> keys = json.getJSONObject("history").keys();
            JSONObject history = json.getJSONObject("history");
            float temp = 0;
            ArrayList<Entry> entries = new ArrayList<>();
            do {
                String date = keys.next();

                float open = (float)history.getJSONObject(date).getDouble("open");

                entries.add(new Entry(temp, open));
                temp++;
            } while (temp < 365);

            LineDataSet dataSet = new LineDataSet(entries, "Stocks");
            dataSet.setDrawValues(false);
            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            LineData data = new LineData();
            data.addDataSet(dataSet);

            chart.setData(data);
            chart.invalidate();

            YAxis rightAxis = chart.getAxisRight();
            YAxis leftAxis = chart.getAxisLeft();
            rightAxis.setEnabled(false);
            leftAxis.removeAllLimitLines();


            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            Legend legend = chart.getLegend();
            legend.setEnabled(false);

            chart.getAxisLeft().setDrawGridLines(false);
            chart.getXAxis().setDrawGridLines(false);

            chart.getDescription().setEnabled(false);
        }
        catch (Exception e){}
    }
}