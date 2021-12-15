package com.example.domiapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domiapp.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.concurrent.fixedRateTimer

class busquedaFragment : Fragment() {

   // lateinit var mAutoComplete:AutocompleteSupportFragment
   lateinit var mAutoComplete:AutocompleteSupportFragment

    lateinit var mPlaces: PlacesClient
    var mOrigin=""
   lateinit var mOriginLatLng:LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_busqueda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       // init()
        /*
        if (!Places.isInitialized()){
            context?.let { Places.initialize(it, resources.getString(R.string.google_maps_key)) }
        }

       mPlaces=Places.createClient(requireContext())
        //ahora vamos a instanciar el autocomplete
        mAutoComplete= requireActivity().fragmentManager.findFragmentById(R.id.fDestino) as PlaceAutocompleteFragment
       // mAutoComplete= activity?.supportFragmentManager?.findFragmentById(R.id.fDestino) as AutocompleteSupportFragment
        // vamos a pasarle la lista de los lugares
       // mAutoComplete.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME))
        // vamos a establecerle un listener a este autoComplete cuando el usuario presione sobre el

        // en el xml "com.google.android.libraries.places.widget.AutocompleteSupportFragment"
        //mAutoComplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
        mAutoComplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {
                mOrigin=place.name.toString()
                mOriginLatLng= place.latLng!!
                Log.d("PLACE","name: "+ mOrigin)
                Log.d("PLACE","lat: "+ mOriginLatLng.latitude)
                Log.d("PLACE","lat: "+ mOriginLatLng.longitude)

            }

            override fun onError(p0: Status) {
                TODO("Not yet implemented")
            }

        })
        
 */
    }



    // //AIzaSyCUVc7l4oTnZvapzqFZDnYrapqEQBv5TB8
    // para prueba gratis   AIzaSyC_jEJj9ezDk77vmbXl1mIwSpw595QrTSE
    //  android:name="com.google.android.libraries.places.widget.AutocompleteSupportFragment"


   fun init(){
        Places.initialize(requireContext(),getString(R.string.google_maps_key))
        mAutoComplete= childFragmentManager.findFragmentById(R.id.fDestino) as AutocompleteSupportFragment
        mAutoComplete.setPlaceFields(
            Arrays.asList(
                Place.Field.ID, Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.NAME))
        mAutoComplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                Snackbar.make(requireView(), p0.latLng!!.toString(), Snackbar.LENGTH_LONG).show()
            }

            override fun onError(p0: Status) {
                Snackbar.make(requireView(), p0.statusMessage!!, Snackbar.LENGTH_LONG).show()
            }

        })
    }



}