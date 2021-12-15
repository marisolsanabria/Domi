package com.example.domiapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.domiapp.R
import com.example.domiapp.providers.AuthProvider
import com.example.domiapp.providers.GeofireProvider
import com.example.domiapp.providers.TokenProvider
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_maps_domiciliario.*

class MapsDomiciliarioFragment : Fragment(), OnMapReadyCallback{

     lateinit var mAuthProvider:AuthProvider
    lateinit var mLocationRequest:LocationRequest
    lateinit var mFusedLocation:FusedLocationProviderClient
    var LOCATION_REQUEST_CODE=55
    var SETTINGS_REQUEST_CODE=66
    lateinit var googleMaps:GoogleMap
    var mMarker: Marker? = null
    var mIsConnected=false
    lateinit var mCurrentLatLng: LatLng
    lateinit var mGeofireProvider:GeofireProvider

    lateinit var mpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var tokenProvider: TokenProvider


    var mLocationCallback: LocationCallback= object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {

            if (locationResult != null) {
                for (location in locationResult.locations){
                    //guardamos las coordenadas de ubicacion en una variable
                    mCurrentLatLng= LatLng(location.latitude,location.longitude)
                    if (mMarker != null){
                        mMarker!!.remove()
                    }

                   mMarker= googleMaps.addMarker(MarkerOptions().position(LatLng(location.latitude,location.longitude)).title("tu posision").icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_moto_domi)))
                    // instanciamos el marcador en el método locationCallback
                    if (activity?.applicationContext  != null){
                        //VAMOS A OBTENER LA LOCALIZACION DEL USUARIO EN TIEMPO REAL
                        val cameraPosition=CameraPosition.Builder().target(LatLng(location.latitude,location.longitude)).zoom(16f).build()
                        googleMaps.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                        //ahora llamaremos a un metodo que guarde mi ubicacion en firebase
                    }

                    updateLocation()
                }
            }
        }
    }

    /*private val callback = OnMapReadyCallback { googleMap ->

        googleMaps=googleMap
        googleMaps.mapType=GoogleMap.MAP_TYPE_NORMAL
        //para poder manejar el zoom
        googleMaps.uiSettings.isZoomControlsEnabled=true

        //Para dar la ubicación exacta del conductor
        googleMaps.isMyLocationEnabled = true







        //esto es cada cuanto va a actualizar la ubicacion

        mLocationRequest=LocationRequest()
        mLocationRequest.setInterval(1000)
        mLocationRequest.setFastestInterval(1000)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setSmallestDisplacement(5f)

        startLocation()
    }

     */

    override fun onMapReady(p0: GoogleMap?) {

        if (p0 != null) {
            googleMaps=p0
        }
        googleMaps.mapType=GoogleMap.MAP_TYPE_NORMAL
        //para poder manejar el zoom
        googleMaps.uiSettings.isZoomControlsEnabled=true

        //Para dar la ubicación exacta del conductor
        if (context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED && context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {

            return
        }
        //para que no se vea el punto azul
        googleMaps.isMyLocationEnabled = false

        //Voy a establecerle un icono personalizado al marcador del conductor, busqupe el icono en icons 8




        //esto es cada cuanto va a actualizar la ubicacion

        mLocationRequest=LocationRequest()
        mLocationRequest.setInterval(1000)
        mLocationRequest.setFastestInterval(1000)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setSmallestDisplacement(5f)

        //vamos a mover el start location de acá ahora para que se opción del domiciliario conectarse o desconectarse
       // startLocation()



    }
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mGeofireProvider= GeofireProvider()
        mAuthProvider= AuthProvider()
        return inflater.inflate(R.layout.fragment_maps_domiciliario, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mpref= this.requireActivity().getSharedPreferences("connection", AppCompatActivity.MODE_PRIVATE)
        editor  =mpref.edit()
        tokenProvider= TokenProvider()
        mAuthProvider= AuthProvider()

       // editor.putBoolean("isConnect",false)
        //editor.apply()


        val mapFragment = childFragmentManager.findFragmentById(R.id.mapDomiciliario) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // vamos a hacer que aparezca en su ubicacion actual

        // con esta propiedad vamos a detener o iniciar la ubicacion del usuario cada vez que lo creamos conveniente
        mFusedLocation= context?.let { LocationServices.getFusedLocationProviderClient(it) }!!

        //el botón oara conectarse o desconnectarse
        btnConectDriver.setOnClickListener {
            //si está conectado nos vamos a desconectar
            if (mIsConnected){
                disconnect()
                //editor.putBoolean("isConnect",false)
                //editor.apply()
            }else{
                // de lo contrario nos conectamos y vamos a comenzar el start location
                startLocation()
            }

        }

        generateToken()

    }


    //creamos un metodo para encontrar nuestra ubicacion
    fun startLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //si los permisos ya están consedidos entonces
            if (context?.let { ContextCompat.checkSelfPermission(it , android.Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED){
                //vamos a verificar si el GPS está activado
                    if (gpsActived()){
                        btnConectDriver.setText(R.string.disconectDriver)
                        mIsConnected= true
                        //editor.putBoolean("isConnect",true)
                        //editor.apply()
                        //si el gps está activado entonces vamos a escuchar nuestra localización
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                    }else{
                        // si no está aprobado entonces mostrar el alertDialog
                        showAlertDialogNOGPS()
                    }

            }else{
                //si no están consedidos los permisos entonces
                checkLocationPermissions()
            }
        }else
        //si la version no es mayor a la m no se requiere permisos entonces solo verificaremos que si tenga activado el GPS
            if (gpsActived()){
                mFusedLocation.requestLocationUpdates(LocationRequest(), mLocationCallback, Looper.myLooper())
            }else{
                showAlertDialogNOGPS()
            }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //para cuando nos de permiso de acceder a la ubicacion

        if (requestCode==LOCATION_REQUEST_CODE){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (context?.let { ContextCompat.checkSelfPermission(it , android.Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED){
                    //verificamos que tenga activado el GPS
                        if (gpsActived()){
                            mFusedLocation.requestLocationUpdates(LocationRequest(), mLocationCallback, Looper.myLooper())
                        }else{
                            showAlertDialogNOGPS()
                        }

                }
                else{
                    checkLocationPermissions()
                }
            }
            else{
                checkLocationPermissions()
            }
        }
    }
    fun checkLocationPermissions(){

        Toast.makeText(context,"checkSelfPermissionWorking",Toast.LENGTH_SHORT).show()

        val positiveButtonClick ={dialog:DialogInterface, which: Int->
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_REQUEST_CODE)
        }
        val negativeButtonClick ={dialog:DialogInterface, which: Int-> Toast.makeText(context,"permisos no aprobados",Toast.LENGTH_SHORT).show()}
        if (context?.let { ContextCompat.checkSelfPermission(it , android.Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)){
                val alertDialog= AlertDialog.Builder(context).setTitle("Por favor proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicación para ser utilizada")

                alertDialog.setPositiveButton("ok",DialogInterface.OnClickListener(positiveButtonClick))
                //esta linea nos habilita los permisos
                //este botón negativo me parece innecesario
                // alertDialog.setNegativeButton("cancelar",DialogInterface.OnClickListener(negativeButtonClick))
                alertDialog.setCancelable(false)
                alertDialog.create()
                alertDialog.show()

            }else{
                ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_REQUEST_CODE)
            }
        }
    }

    //Ahora crearemos un método para conocer si el usuario tiene el GPS activado y retornará un valor de tipo Boolean
    fun gpsActived():Boolean {
        var isActive = false
        var locationManager: LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //si tiene el gps activado
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //si está activado entonces la variable isActive será igual a true
            isActive = true
        }
        // ahora vamos a devolver esa variable

        return isActive
    }

    //Ahora crearemos un método con un alert dialog que le indique al usuario que vaya a config y active el gps
    fun showAlertDialogNOGPS(){

        //Lo que hará al dar click en el alert dialog, ir a configuraciones y tomar el resultado de si activo el gps
        var positiveButtonGPSClick= { dialog: DialogInterface, which: Int ->
            startActivityForResult(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE)
        }
        val builder=AlertDialog.Builder(context)
        builder.setMessage("porfavor Activa tu ubicacion GPS para continuar")
        builder.setPositiveButton("configuraciones",DialogInterface.OnClickListener(positiveButtonGPSClick)).create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //primero preguntamos si el requestcode es igual al settings request code y el GPS está ACTIVP
        if (requestCode==SETTINGS_REQUEST_CODE && gpsActived()){
            //esto quiere decir que el usuario si activo el gps, entonce vamos a iniciiar el listening de nuestra ubicación actual
            if (context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
                return
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        }else{
            //si no lo aceptó debemos mostrar el alertDialog de nuevo
            showAlertDialogNOGPS()
        }
    }

    fun disconnect(){

        if (mFusedLocation != null){
            btnConectDriver.setText(R.string.conectDriver)
            mIsConnected= false
            //editor.putBoolean("isConnect",false)
            //editor.apply()
            //ahora vamos a desconectarnos
            mFusedLocation.removeLocationUpdates(mLocationCallback)
            mGeofireProvider.removeLocation(mAuthProvider.id)
        }else{
            Toast.makeText(context,"proceso de desconexión fallido", Toast.LENGTH_SHORT).show()
        }
    }


    fun updateLocation(){
        if (mAuthProvider.existSession()){
            mGeofireProvider.safeLocation(mAuthProvider.id, mCurrentLatLng)
        }
    }
    fun generateToken(){
        tokenProvider.create(mAuthProvider.id)
    }


}


