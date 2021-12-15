package com.example.domiapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.domiapp.R
import com.example.domiapp.fragments.*
import com.example.domiapp.includes.Users
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_domiciliarios.*
import kotlinx.android.synthetic.main.fragment_nav_header.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.ivProfilePictureSideMenu
import kotlinx.android.synthetic.main.nav_header_main.name

class
DomiciliariosActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView:NavigationView
    private lateinit var navController:NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var database: FirebaseDatabase
    var firebaseDatabase:FirebaseDatabase= FirebaseDatabase.getInstance()
    var firebaseAuth:FirebaseAuth= FirebaseAuth.getInstance()

    lateinit var mpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var currentUserUid: String
    lateinit var userinfo: Users
    var selectedU:String?=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_domiciliarios)

        userinfo= Users()

        database= FirebaseDatabase.getInstance()
        currentUserUid=FirebaseAuth.getInstance().currentUser!!.uid

        val toolbar: androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbarrDomi)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayoutDomi)
    navView= findViewById(R.id.navigation_view_Domi)
   navController=findNavController(R.id.nav_host_fragment_domi)


        appBarConfiguration= AppBarConfiguration(setOf(R.id.bnvCity,R.id.bnvDeliveries,R.id.bnvSettings,R.id.bnvHelp,R.id.bnvSupport),drawerLayout)
        setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)

        mpref = applicationContext.getSharedPreferences("typeUser", MODE_PRIVATE)
        editor = mpref.edit()
        selectedU = mpref.getString("user", "")

      // init()
        takeUserInformation()



            // profileType.text=selectedU
            Toast.makeText(this, selectedU, Toast.LENGTH_SHORT).show()

    }
    private  fun init(){

       navView.setNavigationItemSelectedListener {

            val negativeButtonClick = { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }
            val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                AuthUI.getInstance().signOut(this)
                goToLogInActivity()
                finish()
            }

            if (it.itemId == R.id.bnvHelp){
                navController.navigate(R.id.bnvHelp)
                drawerLayout.close()
            }
             else if (it.itemId == R.id.bnvCity){
                navController.navigate(R.id.bnvCity)
                drawerLayout.close()
            }
             else if (it.itemId == R.id.bnvDeliveries){
                navController.navigate(R.id.bnvDeliveries)
                drawerLayout.close()
            }
             else if (it.itemId == R.id.bnvSettings){
                navController.navigate(R.id.bnvSettings)
                drawerLayout.close()
            }
             else if (it.itemId == R.id.bnvSupport){
                navController.navigate(R.id.bnvSupport)
                drawerLayout.close()
            }


         /*   if (it.itemId == R.id.bnv) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("CERRAR SESIÓN").setMessage("¿Desea cerrar sesión?")
                    .setNegativeButton("CANCEL", negativeButtonClick)
                    .setPositiveButton("CERRAR SESIÓN", positiveButtonClick).setCancelable(false)

                val dialof=builder.create()
                dialof.setOnShowListener {
                    dialof.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.domiColor))
                    dialof.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.domiColor))
                }



                dialof.show()

            }

          */

            true



        }

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
        menuInflater.inflate(R.menu.menu_domiciliarios,menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController= findNavController(R.id.nav_host_fragment_domi)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun goToLogInActivity() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}