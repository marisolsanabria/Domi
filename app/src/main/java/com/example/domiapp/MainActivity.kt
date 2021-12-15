package com.example.domiapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.domiapp.activities.LogInActivity
import com.example.domiapp.includes.Users
import com.firebase.ui.auth.data.model.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var navView:NavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var database: FirebaseDatabase
    lateinit var mpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var currentUserUid: String
    lateinit var userinfo:Users
    lateinit var selectedU:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database= FirebaseDatabase.getInstance()
        userinfo= Users()
        currentUserUid=FirebaseAuth.getInstance().currentUser!!.uid
        mpref=applicationContext.getSharedPreferences("typeUser", MODE_PRIVATE)
        editor =mpref.edit()

        selectedU= mpref.getString("user","").toString()
        takeUserInformation()

       // profileType.text=selectedU
        Toast.makeText(this,selectedU,Toast.LENGTH_SHORT).show()


   /*   toggle= ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    */

        val toolbar: androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbarrClient)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView= findViewById(R.id.navigation_view)
        navController=findNavController(R.id.nav_host_fragment_client)



       appBarConfiguration = AppBarConfiguration(setOf(
           R.id.bnvCity,R.id.bnvDeliveries,R.id.bnvSettings,R.id.bnvHelp,R.id.bnvSupport), drawerLayout)
       setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)






/*
       val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
       findViewById<NavigationView>(R.id.navigation_view).setupWithNavController(navController)

 */

       /* if(selectedU=="domiciliario"){

            val intent = Intent(this,DomiciliariosActivity::class.java)
            startActivity(intent)
            finish()

        }

        */

 /* navView.setNavigationItemSelectedListener{
       if (it.itemId == R.id.bnvHelp){
           navController.navigate(R.id.bnvHelp)
           drawerLayout.close()
       }
       if (it.itemId == R.id.bnvCity){
           navController.navigate(R.id.bnvCity)
           drawerLayout.close()
       }
       if (it.itemId == R.id.bnvDeliveries){
           navController.navigate(R.id.bnvDeliveries)
           drawerLayout.close()
       }
       if (it.itemId == R.id.bnvSettings){
           navController.navigate(R.id.bnvSettings)
           drawerLayout.close()
       }
       if (it.itemId == R.id.bnvSupport){
           navController.navigate(R.id.bnvSupport)
           drawerLayout.close()
       }
       true
   }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account_logout,menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController= findNavController(R.id.nav_host_fragment_client)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun goToLogInActivity() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun goToLogIn() {
        val intent = Intent(this,LogInActivity::class.java)
        startActivity(intent)

    }


}