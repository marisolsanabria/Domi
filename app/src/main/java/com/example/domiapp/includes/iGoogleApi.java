package com.example.domiapp.includes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface iGoogleApi {

    //acá haremos una petición get para el servicio de direcciones de googleMaps
     @GET
    Call<String> getDirection(@Url String url);
}
