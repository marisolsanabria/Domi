package com.example.domiapp.includes

import android.provider.ContactsContract
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object content {
    var database: FirebaseDatabase= FirebaseDatabase.getInstance()
    var currentUserUid: String=FirebaseAuth.getInstance().currentUser!!.uid
    var userinfo:Users=Users()


    lateinit var  latlongDestiantion:LatLng
    lateinit var latlongOrigin:LatLng
    lateinit var  Destiantion:String
    lateinit var Origin:String
    lateinit var tiempo:String
    lateinit var distancia:String
    lateinit var mcurrenLatLong:LatLng
    lateinit var comentario:String
    lateinit var postId:String
    lateinit var cityName: String

    public fun content(){
        val usersRef = database.reference.child("users")
        usersRef.child(currentUserUid).get().addOnSuccessListener{
            userinfo= (it.getValue() as Users)

        }
    }


}