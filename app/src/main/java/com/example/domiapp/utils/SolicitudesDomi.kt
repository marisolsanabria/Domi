package com.example.domiapp.utils

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import java.util.*
import kotlin.collections.ArrayList

class SolicitudesDomi {

    private var origenName:String=""
    private var origenLatLng:String=""
    private var destinoName:String=""
    private var destinoLatLng:String=""
    private var userID:String=""
    private var comentario:String=""
    private var tiempo:String=""
    private var distancia:String=""
    private var cityName:String=""

        //vamos a ponerle el id
        //para decirle a firebase que no quiero que me incluya esto porque cuando creemos un nuevo post intentará crear un nuevo uid si no ponemos lo siguiente

        @Exclude
        @set:Exclude
        @get:Exclude
        var uid:String?=null


        //un constructor vacio que requiere firebase para darnos la info

        constructor()
        constructor(origenName:String,origenLatLng: String, destinoName: String, destinoLatLng:String, userID:String, comentario:String, tiempo:String, distancia:String,cityName:String){
            this.origenName=origenName
            this.origenLatLng=origenLatLng
            this.destinoName=destinoName
            this.destinoLatLng=destinoLatLng
            this.userID=userID
            this.comentario=comentario
            this.tiempo=tiempo
            this.distancia=distancia
            this.cityName=cityName
        }

        fun getOrigenName():String{
            return origenName
        }
        fun setOriginName(originName: String) {
            this.origenName=originName
        }


    fun getOrigenLatLng():String{
        return origenLatLng
    }
    fun setOriginLatLng(originLatLng: String) {
        this.origenLatLng=originLatLng
    }


    fun getDestinoName():String{
        return destinoName
    }
    fun setDestinoName(destinoName: String) {
        this.destinoName=destinoName
    }

    fun getDestinoLatLng():String{
        return destinoLatLng
    }
    fun setDestinoLatLng(destinoLatLng: String) {
        this.destinoLatLng=destinoLatLng
    }
    fun getUserId():String{
        return userID
    }
    fun setUserId(userId: String) {
        this.userID=userId
    }

    fun getComentario():String{
        return comentario
    }
    fun setComentario(comentario: String) {
        this.comentario=comentario
    }
    fun getTiempo():String{
        return tiempo
    }
    fun setTiempo(tiempo: String) {
        this.tiempo=tiempo
    }
    fun getDistancia():String{
        return distancia
    }
    fun setDistancia(distancia: String) {
        this.distancia=distancia
    }

    fun getCityName():String{
        return cityName
    }
    fun setCityName(cityName: String) {
        this.cityName=cityName
    }









}