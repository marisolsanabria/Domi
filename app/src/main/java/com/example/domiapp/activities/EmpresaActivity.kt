package com.example.domiapp.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.domiapp.R
import com.example.domiapp.includes.Users
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_nav_header.*

class EmpresaActivity : AppCompatActivity() {

    private lateinit var navView:NavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var database: FirebaseDatabase
    lateinit var mpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    lateinit var currentUserUid: String
    lateinit var userinfo:Users
    lateinit var selectedU:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa)

        val toolbar: androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbarrEmpresa)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayoutEmpresa)
        navView= findViewById(R.id.navigation_view_Empresa)
        navController=findNavController(R.id.nav_host_fragment_empresa)

        database= FirebaseDatabase.getInstance()
        userinfo= Users()
        currentUserUid= FirebaseAuth.getInstance().currentUser!!.uid

        mpref=applicationContext.getSharedPreferences("typeUser", MODE_PRIVATE)
        editor =mpref.edit()

        var selectedU=mpref.getString("user","")












        appBarConfiguration= AppBarConfiguration(setOf(R.id.bnvCity,R.id.bnvDeliveries,R.id.bnvSettings,R.id.bnvHelp,R.id.bnvSupport),drawerLayout)
        setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)

       /* val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<NavigationView>(R.id.navigation_view_Empresa).setupWithNavController(navController)

        */




    }


    private fun takeUserInformation() {
       val usersRef = database.reference.child("users")
        usersRef.child(currentUserUid).get().addOnSuccessListener {
            userinfo= it.getValue<Users>()!!

            val headerView= navView.getHeaderView(0)
            val txt_name=headerView.findViewById<View>(R.id.name) as TextView
            val txt_profileType=headerView.findViewById<View>(R.id.profileTipe) as TextView
            val txt_puntuacion=headerView.findViewById<View>(R.id.puntuacion) as TextView
            val ivProfilePic=headerView.findViewById<View>(R.id.ivProfilePictureSideMenu) as ImageView

            txt_name.setText(userinfo.getName())
            txt_profileType.setText(selectedU)
            txt_puntuacion.setText(userinfo.getCalifcation().toString())
            Picasso.get().load(userinfo.getPhoto()).into(ivProfilePic)
        }





    }

}