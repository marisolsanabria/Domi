package com.example.domiapp.activities

import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.domiapp.R
import com.example.domiapp.includes.content
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class RequestDriverActivity : AppCompatActivity() {
    private lateinit var mAnimation: LottieAnimationView
    private var tvLookingFor: TextView? = null
    private var btnCancel: Button? = null

    private lateinit var geoQuery: GeoQuery
    var mDriversMarkers: MutableList<Marker> = arrayListOf()

    lateinit var mCurrentLatLng: LatLng
    var radius= 8.0
    var mdriverFound=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_driver)
        mAnimation = findViewById(R.id.animation)
        tvLookingFor = findViewById(R.id.tvLookingFor)
        btnCancel = findViewById(R.id.btnCancelar)

        mAnimation.playAnimation()
        mCurrentLatLng=content.mcurrenLatLong

        getActiveDrivers()

    }
    fun getActiveDrivers() {

        val driverLocation= FirebaseDatabase.getInstance().reference.child("activeDrivers")


        //le debo pasar la ubicacion del que está buscando conductor y el radio de busqueda

        val geofire = GeoFire(driverLocation)
        geoQuery= geofire.queryAtLocation(GeoLocation(mCurrentLatLng.latitude, mCurrentLatLng.longitude), radius)
        geoQuery.removeAllListeners()

        // mGeofireProvider.getActiveDrivers(mCurrentLatLng).addGeoQueryEventListener(object : GeoQueryEventListener {
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {

            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                if (!mdriverFound){
                    mdriverFound=true
                }
            }

            override fun onKeyExited(key: String?) {
            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {
            }

            override fun onGeoQueryReady() {
            }

            override fun onGeoQueryError(error: DatabaseError?) {

                if (!mdriverFound){

                }

            }

        })
    }

}
