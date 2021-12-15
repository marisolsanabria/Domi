package com.example.domiapp.fragments

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.domiapp.R
import com.example.domiapp.includes.content
import com.example.domiapp.providers.GoogleApiProvider
import com.example.domiapp.utils.DecodePoints
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import kotlinx.coroutines.newFixedThreadPoolContext
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.lang.StringBuilder
import javax.security.auth.callback.Callback


class DetailRequestFragment : Fragment(), OnMapReadyCallback {

    var mOrigin:String?=""
    lateinit var mOriginLatLng: LatLng

    var mDestination:String?=""
    lateinit var mDestinationLatLng: LatLng
    lateinit var googleMaps:GoogleMap

    //para las rutas
    lateinit var mGoogleApiProvider: GoogleApiProvider
    lateinit var mPolyline:MutableList<LatLng>
    lateinit var mPolylineOptions: PolylineOptions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_detail_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mGoogleApiProvider= GoogleApiProvider(context)
        mOriginLatLng=content.latlongOrigin
        mDestinationLatLng=content.latlongDestiantion

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapRequestNow) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

    }

    fun drawRoute(){
        mGoogleApiProvider.getDirections(mOriginLatLng,mDestinationLatLng).enqueue(object : retrofit2.Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                try {

                    var jsonObject:JSONObject= JSONObject(response.body()!!)
                    //ahora crearemos un jsonArray

                    var jsonArray:JSONArray=jsonObject.getJSONArray("routes")

                        var route:JSONObject=jsonArray.getJSONObject(0)
                        //en la siguiente variable obtendremos los polígonos necesarios para trazasr la ruta
                        var polyLines:JSONObject=route.getJSONObject("overview_polyline")
                        //vamos a obtener el string de cada punto de navegación
                        var points:String= polyLines.getString("points")
                        mPolyline= DecodePoints.decodePoly(points) as MutableList<LatLng>
                        mPolylineOptions= PolylineOptions()
                        //vamos a establecerle unas propiedades como: color, ancho,
                        mPolylineOptions.color(Color.DKGRAY)
                        mPolylineOptions.width(8f)
                        mPolylineOptions.startCap(SquareCap())
                        mPolylineOptions.jointType(JointType.ROUND)
                        mPolylineOptions.addAll(mPolyline)
                        googleMaps.addPolyline(mPolylineOptions)


                }catch(exception: IOException) {
                    Log.d("Error", "error encontrado: " + exception)

                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
                //acá resiviremos la respuesta de nuestro servidor
             /*   try {

                    var jsonObject= JSONObject(response.body())
                    //ahora crearemos un jsonArray

                    var jsonArray=jsonObject.getJSONArray("routes")



                    for(i in 0..jsonArray.length()){
                        var route=jsonArray.getJSONObject(0)
                        //en la siguiente variable obtendremos los polígonos necesarios para trazasr la ruta
                        var polyLines=route.getJSONObject("overview_polyline")
                        //vamos a obtener el string de cada punto de navegación
                        var points:String= polyLines.getString("points")
                        mPolyline= DecodePoints.decodePoly(points) as MutableList<LatLng>
                        mPolylineOptions= PolylineOptions()
                        //vamos a establecerle unas propiedades como: color, ancho,
                        mPolylineOptions.color(Color.DKGRAY)
                        mPolylineOptions.width(8f)
                        mPolylineOptions.startCap(SquareCap())
                        mPolylineOptions.jointType(JointType.ROUND)
                        mPolylineOptions.addAll(mPolyline)
                        googleMaps.addPolyline(mPolylineOptions)
                    }


                }catch(exception: IOException) {
                    Log.d("Error", "error encontrado: " + exception)

                }

              */






    }

    override fun onMapReady(p0: GoogleMap?) {

        if (p0 != null) {
            googleMaps=p0
        }
        googleMaps.mapType=GoogleMap.MAP_TYPE_NORMAL
        //para poder manejar el zoom
        googleMaps.uiSettings.isZoomControlsEnabled=false

     googleMaps.addMarker(MarkerOptions().position(mOriginLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_ubicacion_usuario)))
      googleMaps.addMarker(MarkerOptions().position(mDestinationLatLng).title("destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_map_pin_100)))

        val cameraPosition= CameraPosition.Builder().target(LatLng(mOriginLatLng.latitude, mOriginLatLng.longitude)).zoom(14f).build()
        googleMaps.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        drawRoute()

    }


}