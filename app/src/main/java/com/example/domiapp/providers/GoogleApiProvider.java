package com.example.domiapp.providers;

import android.content.Context;
import android.util.Log;

import com.example.domiapp.R;
import com.example.domiapp.includes.RetrofitClient;
import com.example.domiapp.includes.iGoogleApi;
import com.google.type.LatLng;

import java.util.Date;

import kotlin.contracts.Returns;
import retrofit2.Call;
import retrofit2.Retrofit;

public class GoogleApiProvider {
    private Context context;
    //creamos el constructor
    public GoogleApiProvider(Context context){
        this.context= context;

    }

    public Call<String> getDirections(com.google.android.gms.maps.model.LatLng originLatLng, com.google.android.gms.maps.model.LatLng destinoLatLng){
        String baseUrl= "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + originLatLng.latitude + "," + originLatLng.longitude + "&"
                + "destination=" + destinoLatLng.latitude + "," + destinoLatLng.longitude + "&"
                + "key=" + context.getResources().getString(R.string.google_maps_key);

        Log.d("URL", baseUrl + query);
         return RetrofitClient.getClient(baseUrl).create(iGoogleApi.class).getDirection(baseUrl+query);
    }
}
