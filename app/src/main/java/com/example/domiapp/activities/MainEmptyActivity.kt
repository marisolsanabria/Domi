package com.example.domiapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.domiapp.MainActivity
import com.google.firebase.auth.FirebaseAuth

class MainEmptyActivity : AppCompatActivity() {

    private val mAuth= FirebaseAuth.getInstance()
    lateinit var mpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mpref=applicationContext.getSharedPreferences("typeUser", MODE_PRIVATE)
        editor =mpref.edit()

        var selectedU=mpref.getString("user","")

        if (mAuth.currentUser != null){
            if (mpref!=null){
                if (selectedU=="cliente"){
                    goToMainActivity()
                } else if (selectedU=="domiciliario"){
                    goToDomiciliariosActivity()
                }else if ( selectedU == "empresa"){
                    goToEmpresaActivity()
                }
                Toast.makeText(this,mAuth.currentUser.toString(),Toast.LENGTH_LONG).show()

            }
        }else{
            goToLogInActivity()
        }
        finish()
    }


    private fun goToLogInActivity() {
        val intent= Intent(this,LogInActivity::class.java)
        startActivity(intent)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun goToEmpresaActivity() {
        val intent= Intent(this,EmpresaActivity::class.java)
        startActivity(intent)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun goToDomiciliariosActivity() {
        val intent= Intent(this,DomiciliariosActivity::class.java)
        startActivity(intent)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    fun goToMainActivity(){
        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

}