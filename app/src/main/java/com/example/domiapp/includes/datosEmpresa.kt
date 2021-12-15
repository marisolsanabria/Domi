package com.example.domiapp.includes

import com.google.android.gms.maps.model.LatLng

class datosEmpresa {

    private var domiciliarios:List<String> = emptyList()
    private var latlngg: LatLng= LatLng(0.0,0.0)

    constructor()
    constructor(domiciliarios:List<String>,latLngg: LatLng){
        this.domiciliarios=domiciliarios
        this.latlngg=latLngg
    }
    fun getDomiciliarios():List<String>{
        return domiciliarios
    }
    fun setDomiciliarios(domiciliarios: List<String>){
        this.domiciliarios=domiciliarios
    }

    fun getLatLng():LatLng{
        return latlngg
    }
    fun setName(latLngg: LatLng){
        this.latlngg=latLngg
    }


}