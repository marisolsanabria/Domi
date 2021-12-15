package com.example.domiapp.includes

import com.google.type.LatLng

class Users {

    private var uid:String=""
    private var name:String=""
    private var email:String=""
    private var phone:String=""
    private var photo:String=""
    private var calification:Double=5.0
    private var domiciliario=false
    private var empresa=false
    private var latLongEmpresa:String=""
    private var latitud:Double=0.0
    private var longitud:Double=0.0

    constructor()
    constructor(uid: String, name: String, email: String, phone: String, photo: String,calification:Double, domiciliario:Boolean,empresa:Boolean,latLongEmpresa:String,latitud: Double,longitud:Double){
        this.uid=uid
        this.name=name
        this.email=email
        this.phone=phone
        this.photo=photo
        this.calification=calification
        this.domiciliario=domiciliario
        this.empresa=empresa
        this.latLongEmpresa=latLongEmpresa
        this.latitud=latitud
        this.longitud=longitud
    }

    fun getName():String{
        return name
    }
    fun setName(name: String){
        this.name=name
    }

    fun getemail():String{
        return email
    }
    fun setemail(email: String){
        this.email=email
    }

    fun getphone():String{
        return phone
    }
    fun setphone(phone: String){
        this.phone=phone
    }

    fun getPhoto():String{
        return photo
    }
    fun setPhoto(photo: String){
        this.photo=photo
    }
    fun getCalifcation():Double{
        return calification
    }
    fun setCalification(calification: Double){
        this.calification=calification
    }

    fun getuid():String{
        return uid
    }
    fun setuid(uid: String){
        this.uid=uid
    }
    fun getdomiciliario():Boolean{
        return domiciliario
    }
    fun setdomiciliario(domiciliario: Boolean){
        this.domiciliario=domiciliario
    }
    fun getempresa():Boolean{
        return empresa
    }
    fun setempresa(empresa: Boolean){
        this.empresa=empresa
    }
    fun getLatitud():Double{
        return latitud
    }
    fun setLatitud(latitud: Double){
        this.latitud=latitud
    }
    fun getLongitud():Double{
        return longitud
    }
    fun setLongitud(longitud: Double){
        this.longitud=longitud
    }

    fun getLatLongEmpresa():String{
        return latLongEmpresa
    }
    fun setLatLongEmpresa(latLongEmpresa: String){
        this.latLongEmpresa=latLongEmpresa
    }
}