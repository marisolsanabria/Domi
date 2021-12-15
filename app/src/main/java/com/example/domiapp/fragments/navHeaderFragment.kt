package com.example.domiapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.domiapp.MainActivity
import com.example.domiapp.R
import com.example.domiapp.includes.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_nav_header.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [navHeaderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class navHeaderFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    lateinit var mpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    lateinit var currentUserUid: String
    lateinit var userinfo:Users
    lateinit var selectedU:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_nav_header, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        database= FirebaseDatabase.getInstance()
        userinfo= Users()
        currentUserUid= FirebaseAuth.getInstance().currentUser!!.uid
        mpref=requireContext().getSharedPreferences("typeUser", AppCompatActivity.MODE_PRIVATE)
        editor =mpref.edit()

        selectedU= mpref.getString("user","").toString()
       // takeUserInformation()
    }





    private fun takeUserInformation() {
        val usersRef = database.reference.child("users")
        usersRef.child(currentUserUid).get().addOnSuccessListener {
            userinfo= it.getValue<Users>()!!


            name.setText(userinfo.getName())
            profileTipe.setText(selectedU)
            puntuacion.setText(userinfo.getCalifcation().toString())
            Picasso.get().load(userinfo.getPhoto()).into(ivProfilePictureSideMenu)
        }

    }


}