package com.example.windowsv8.haversine;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String url = "https://irfanpi.000webhostapp.com/haverupload.php?lat=";
    SwipeRefreshLayout swipeRefreshLayout;
    Double latitude, longitude;
    CardAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<Jarak> listJarak = new ArrayList<>();
    RequestQueue requestQueue;
    LocationManager locationManager;
    Location location;
    Criteria criteria;
    String provider;
    private PermissionHelper permissionHelper;

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionHelper = new PermissionHelper(this);

        //membuat recycleview dan adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CardAdapter(listJarak, this);
        recyclerView.setAdapter(adapter);
        checkAndRequestPermissions();

        //swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipe);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        provider = locationManager.getBestProvider(criteria, false);

        swipeRefreshLayout.setOnRefreshListener(this);

//        listJarak.clear();
//        adapter.notifyDataSetChanged();

        lokasi();

//        swipeRefreshLayout.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        lokasi();
//                                    }
//                                }
//        );
    }

    @Override
    public void onRefresh() {
        lokasi();
        swipeRefreshLayout.setRefreshing(false);
//        listJarak.clear();
    }

    private boolean checkAndRequestPermissions() {
        permissionHelper.permissionListener(new PermissionHelper.PermissionListener() {
            @Override
            public void onPermissionCheckDone() {

            }
        });
        permissionHelper.checkAndRequestPermissions();
        return true;
    }

    private void lokasi() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
        Log.v(TAG, "location : " +location);
        locationManager.requestLocationUpdates(provider,1000,1,this);
        if (location != null){
            onLocationChanged(location);
        }else {
//            callVoley(-6.712312,103.1238723);
        }

    }

    private void callVoley(Double lat, Double lng) {
//        swipeRefreshLayout.setRefreshing(true);
//        adapter.notifyDataSetChanged();
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url + lat + "&lng=" + lng,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, response.toString());
                        parseData(response);

//                        for (int i = 0; i < response.length(); i++) {
//
//                            try{
//                                JSONObject obj = response.getJSONObject(i);
//                                Jarak j = new Jarak();
//                                j.setNama(obj.getString("nama"));
//                                double jarak = Double.parseDouble(obj.getString("jarak"));
//                                j.setJarak("" + round(jarak,2));
//                                listJarak.add(j);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);

            }

        });
        RequestQueue requestQueue =  Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void parseData(JSONArray jsonArray) {
        listJarak.clear();
        for (int i = 0; i < jsonArray.length(); i++) {

            try{
                JSONObject obj = jsonArray.getJSONObject(i);
                Jarak j = new Jarak();
                j.setNama(obj.getString("nama"));
                double jarak = Double.parseDouble(obj.getString("jarak"));
                j.setJarak("" + round(jarak,2) +"KM");
                listJarak.add(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private double round(double value, int i) {
        if (i < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, i);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.e(TAG,"user location : latitude = " + latitude +" longitude = " +longitude);
        callVoley(latitude,longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestCallBack(requestCode,permissions,grantResults);
    }
}
