package com.example.domiapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.domiapp.R
import com.example.domiapp.includes.Users
import com.example.domiapp.includes.content
import com.example.domiapp.providers.GeofireProvider
import com.example.domiapp.providers.TokenProvider
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.fragment_maps.*
import java.io.IOException
import java.security.AuthProvider
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class MapsFragment : Fragment(), OnMapReadyCallback {

    lateinit var mAuthProvider: com.example.domiapp.providers.AuthProvider
    lateinit var mLocationRequest:LocationRequest
    lateinit var mFusedLocation:FusedLocationProviderClient
    lateinit var mGeofireProvider: GeofireProvider
    var LOCATION_REQUEST_CODE=55
    var SETTINGS_REQUEST_CODE=66
    lateinit var googleMaps:GoogleMap
    lateinit var mCurrentLatLng: LatLng
    var mDriversMarkers: MutableList<Marker> = arrayListOf()
    //variable para asegurarnos de ejecutar una sola vez en el location callback el metodo que agrega los marcadores de los conductores
    var mIsFirstTime:Boolean= true
    private lateinit var geoQuery: GeoQuery

    var mMarker: Marker? = null
    var radius= 8.0


    private var mDatabase: DatabaseReference? = null
    private var mGeofire: GeoFire? = null

    lateinit var mAutoComplete: AutocompleteSupportFragment
    lateinit var mAutoCompleteOrigin: AutocompleteSupportFragment

    var mOrigin:String?=""
    var mOriginLatLng:LatLng? = null

    var mDestination:String?=""
    var mDestinationLatLng:LatLng? = null

    lateinit var tokenProvider: TokenProvider

    //para guardar la ubicación en la que se posicionó
    private lateinit var mCameraListene:GoogleMap.OnCameraIdleListener

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {

            if (locationResult != null) {
                for (location in locationResult.locations){
                    if (activity?.applicationContext  != null){

                        mCurrentLatLng= LatLng(location.latitude, location.longitude)


                        /* if (mMarker != null){
                            mMarker!!.remove()
                        }

                        mMarker= googleMaps.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)).title("tu posision").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_ubicacion_usuario)))

                         */
                        //VAMOS A OBTENER LA LOCALIZACION DEL USUARIO EN TIEMPO REAL
                        val cameraPosition= CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(15f).build()
                        googleMaps.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                        //para asegurarnos que el getActiveDrivers solo se ejecute una vez
                        if (mIsFirstTime){
                            mIsFirstTime=false
                            getActiveDrivers()
                            setRestrictPlacesInCountry(locationResult.lastLocation)
                            limitSearch()
                        }

                    }

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
        googleMaps.uiSettings.isZoomControlsEnabled=false
//        googleMaps.setOnCameraIdleListener (mCameraListene)

        //Para dar la ubicación exacta del conductor
        if (context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED && context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {

            return
        }
        googleMaps.isMyLocationEnabled = true



        //esto es cada cuanto va a actualizar la ubicacion

        mLocationRequest=LocationRequest()
        mLocationRequest.setInterval(1000)
        mLocationRequest.setFastestInterval(1000)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setSmallestDisplacement(5f)

        startLocation()


    }
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {




        return inflater.inflate(R.layout.fragment_maps, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       init()

        mGeofireProvider= GeofireProvider()
        tokenProvider= TokenProvider()
        mAuthProvider=com.example.domiapp.providers.AuthProvider()

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapClientFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // vamos a hacer que aparezca en su ubicacion actual

        // con esta propiedad vamos a detener o iniciar la ubicacion del usuario cada vez que lo creamos conveniente
        mFusedLocation= context?.let { LocationServices.getFusedLocationProviderClient(it) }!!

       // onCameraMove()

        btnSolicitarDomi.setOnClickListener {
            requestDriver()
            content.mcurrenLatLong=mCurrentLatLng
            content.comentario=comentario.text.toString()

        }
        generateToken()
        
    }

    private fun requestDriver() {
        if (mOriginLatLng != null && mDestinationLatLng !=null){
            content.latlongDestiantion=mDestinationLatLng!!
            content.latlongOrigin=mOriginLatLng!!
            content.Destiantion=mDestination!!
            content.Origin=mOrigin!!
            findNavController().navigate(R.id.detailsRequestJavaFragment)
        }else{
                Snackbar.make(requireView(), "Debe seleccionar el lugar de recogida y de destino", Snackbar.LENGTH_LONG).show()
        }
        
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
              // acá añadiremos los marcadores de los conductores que se van conectando

              //vamos a recorrer la lista de marcadores
               for (marker:Marker in mDriversMarkers){
                    if (marker.tag != null){
                        //si se le asignó un identificador a ese marcador preguntaremos el marcador igual al key(que viene de la base de datos de conductores conectados)
                        if (marker.tag!!.equals(key)){
                            //y ejecutamos un return para que no se vuelva a añadir el marcador
                            return;
                        }
                    }
                }

              // voy a crear un propiedad que almacenará la ubicación del conductor que se conectó
              var driverLatLng = location?.let { LatLng(location.latitude, it.latitude) }
              //ahora teniendo la ubicación crearemos un marcador

               var marker= googleMaps.addMarker(driverLatLng?.let { MarkerOptions().position(it).title("conductor disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_moto_domi)) })
              //ahora a este marcador le agregaremos un tag que será el id del conductor
               marker.tag=key
              //vamos a añadir este marcador a la lista de marcadores
               mDriversMarkers.add(marker)
          }

          override fun onKeyExited(key: String?) {
              //acá iremos eliminando los marcadores de los conductores que se desconectan

              for (marker:Marker in mDriversMarkers){
                    if (marker.tag != null){
                        //si se le asignó un identificador a ese marcador preguntaremos el marcador igual al hey(que viene de la base de datos de conductores conectados)
                        if (marker.tag!!.equals(key)){
                            //vamos a remover el marcador
                                marker.remove()
                            //y o removemos de la lista de marcadores
                            mDriversMarkers.remove(marker)

                            //y ejecutamos un return para que no se vuelva a añadir el marcador
                            return;
                        }
                    }
                }

          }

          override fun onKeyMoved(key: String?, location: GeoLocation?) {
              //acá actualizaremos en tiempo real la posición del conductor a medida que se mueve
                for (marker:Marker in mDriversMarkers){
                    if (marker.tag != null){
                        //si se le asignó un identificador a ese marcador preguntaremos el marcador igual al hey(que viene de la base de datos de conductores conectados)
                        if (marker.tag!!.equals(key)){
                         //vamos a decirel al marcador que se establezca en una nueva posición
                            marker.position= location?.latitude?.let { LatLng(it,location.longitude) }
                        }
                    }
                }



          }

          override fun onGeoQueryReady() {
              if (mDriversMarkers.isEmpty()){
                  radius++
                  getActiveDrivers()
              }
          }

          override fun onGeoQueryError(error: DatabaseError?) {
              Toast.makeText(context,"queryError",Toast.LENGTH_SHORT).show()

          }

      })
    }

    //creamos un metodo para encontrar nuestra ubicacion
    fun startLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //si los permisos ya están consedidos entonces
            if (context?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED){
                //vamos a verificar si el GPS está activado
                if (gpsActived()){
                    //si el gps está activado entonces vamos a escuchar nuestra localización
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                    googleMaps.isMyLocationEnabled=true
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
                googleMaps.isMyLocationEnabled=true
            }else{
                showAlertDialogNOGPS()
            }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //para cuando nos de permiso de acceder a la ubicacion

        if (requestCode==LOCATION_REQUEST_CODE){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (context?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED){
                    //verificamos que tenga activado el GPS
                    if (gpsActived()){
                        mFusedLocation.requestLocationUpdates(LocationRequest(), mLocationCallback, Looper.myLooper())
                        googleMaps.isMyLocationEnabled=true
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

        Toast.makeText(context, "checkSelfPermissionWorking", Toast.LENGTH_SHORT).show()

        val positiveButtonClick ={ dialog: DialogInterface, which: Int->
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
        val negativeButtonClick ={ dialog: DialogInterface, which: Int-> Toast.makeText(context, "permisos no aprobados", Toast.LENGTH_SHORT).show()}
        if (context?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)){
                val alertDialog= AlertDialog.Builder(context).setTitle("Por favor proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicación para ser utilizada")

                alertDialog.setPositiveButton("ok", DialogInterface.OnClickListener(positiveButtonClick))
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
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE)
        }
        val builder= AlertDialog.Builder(context)
        builder.setMessage("porfavor Activa tu ubicacion GPS para continuar")
        builder.setPositiveButton("configuraciones", DialogInterface.OnClickListener(positiveButtonGPSClick)).create().show()
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
            googleMaps.isMyLocationEnabled=true
        }else{
            //si no lo aceptó debemos mostrar el alertDialog de nuevo
            showAlertDialogNOGPS()
        }
    }
    fun init(){
        Places.initialize(requireContext(),getString(R.string.google_maps_key))
        instanceAutocompleteDestino()
        instanceAutocompleteOrigin()



    }
    fun  setRestrictPlacesInCountry(location: Location?){
        try {
            val geocoder=Geocoder(requireContext(),Locale.getDefault())
            var addressList:List<Address> = geocoder.getFromLocation(location!!.latitude,location.longitude,1)
            if (addressList.size>0)
                mAutoComplete.setCountry(addressList[0].countryCode)
            val cityName: String = addressList[0].getAddressLine(0)
            content.cityName=cityName

            }catch (e:IOException){
                e.printStackTrace()
        }

        //ahora para el del origen

        try {
            val geocoder=Geocoder(requireContext(),Locale.getDefault())
            var addressList:List<Address> = geocoder.getFromLocation(location!!.latitude,location.longitude,1)
            if (addressList.size>0)
                mAutoCompleteOrigin.setCountry(addressList[0].countryCode)

        }catch (e:IOException){
            e.printStackTrace()
        }

    }

    fun instanceAutocompleteDestino(){
        //acá haré que solo aparezcan direcciones de mi ciudad
        mAutoComplete= childFragmentManager.findFragmentById(R.id.fDestino) as AutocompleteSupportFragment
        mAutoComplete.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID, Place.Field.ADDRESS,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME))
        mAutoComplete.setHint("Destino")
        mAutoComplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                mDestination=p0.name
                mDestinationLatLng=p0.latLng!!
            }

            override fun onError(p0: Status) {
                Snackbar.make(requireView(), p0.statusMessage!!, Snackbar.LENGTH_LONG).show()
            }

        })
    }

    fun instanceAutocompleteOrigin(){

        mAutoCompleteOrigin= childFragmentManager.findFragmentById(R.id.fOrigen) as AutocompleteSupportFragment
        mAutoCompleteOrigin.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID, Place.Field.ADDRESS,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME))
        mAutoCompleteOrigin.setHint("Lugar de recogida")
        mAutoCompleteOrigin.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {

                mOrigin=p0.name
                mOriginLatLng=p0.latLng!!

            }

            override fun onError(p0: Status) {
                Snackbar.make(requireView(), p0.statusMessage!!, Snackbar.LENGTH_LONG).show()
            }

        })
    }

    fun onCameraMove(){
        mCameraListene= GoogleMap.OnCameraIdleListener() {
            try {
                var geocoder=Geocoder(context,Locale.getDefault())
                mOriginLatLng=googleMaps.cameraPosition.target
                if (mOrigin!=null){

                    var adreesList: ArrayList<Address> = geocoder.getFromLocation(mOriginLatLng!!.latitude,mOriginLatLng!!.longitude,1) as ArrayList<Address>
                    //ahora vamos a averigüar la ciudad en la que se encuentra
                    var city= adreesList[0].getAddressLine(1)
                    var country= adreesList[0].getAddressLine(2)
                    var direccion=adreesList[0].getAddressLine(0)
                    mAutoCompleteOrigin.setText(direccion+ "" + city)
                    mOrigin=direccion + "" + city

                }


            }catch (e:IOException){
                Log.d("error", "mensaje error: " + e.message)
            }
        }
    }

    fun limitSearch(){
        //acá haré que solo aparezcan direcciones de mi ciudad
        //para esto añadimos la dependencia de maps utils

        val northside=SphericalUtil.computeOffset(mCurrentLatLng,5700000.0, 0.0)
        val southside=SphericalUtil.computeOffset(mCurrentLatLng,5700000.0, 180.0)
        mAutoComplete.setCountry("COL")
        mAutoComplete.setLocationBias(RectangularBounds.newInstance(southside,northside))

        mAutoCompleteOrigin.setCountry("COL")
        mAutoCompleteOrigin.setLocationBias(RectangularBounds.newInstance(southside,northside))
    }

    fun generateToken(){
        tokenProvider.create(mAuthProvider.id)
    }



}