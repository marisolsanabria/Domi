package com.example.domiapp.providers

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GeofireProvider {
    private val mDatabase: DatabaseReference
    private val mGeofire: GeoFire
    private lateinit var geoQuery: GeoQuery

    //para guardar nuestra localizacion
    fun safeLocation(idDriver: String?, latLng: LatLng) {
        mGeofire.setLocation(idDriver, GeoLocation(latLng.latitude, latLng.longitude))
    }

    //ahora un metodo para eliminar la localización
    fun removeLocation(idDriver: String?) {
        mGeofire.removeLocation(idDriver)
    }

    //método para obtener los conductores disponibles
    fun getActiveDrivers(latLng: LatLng, radius:Double): GeoQuery {
        val driverLocation= FirebaseDatabase.getInstance().reference.child("activeDrivers")
        //le debo pasar la ubicacion del que está buscando conductor y el radio de busqueda
        val geofire = GeoFire(driverLocation)
        geoQuery= geofire.queryAtLocation(GeoLocation(latLng.latitude, latLng.longitude), radius)
        geoQuery.removeAllListeners()
        return geoQuery
    }

    init {
        mDatabase = FirebaseDatabase.getInstance().reference.child("activeDrivers")
        mGeofire = GeoFire(mDatabase)
    }
}