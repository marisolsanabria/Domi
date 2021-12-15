package com.example.domiapp.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.domiapp.MainActivity
import com.example.domiapp.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    lateinit var mpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database:FirebaseDatabase by lazy { FirebaseDatabase.getInstance()}

    // para el on activity result debemos tener una constante que especifique la activity por la que buscamos respuesta
    private val RC_SIGN_IN = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        mpref=applicationContext.getSharedPreferences("typeUser", MODE_PRIVATE)
        editor =mpref.edit()
        //BOTÓN CREAR CUENTA
        btnSingIn.setOnClickListener {
            goToSignUp()
        }
        //botón log in con correo, iniciar sesion
        btnLogInEmail.setOnClickListener {
            logIn()
        }
        //text view forgot password
        tvOlvideContraseña.setOnClickListener {
            goToForgotPassword()
        }
        // botón iniciar sesión con gmail
        btnLogInGmail.setOnClickListener {
            googleLogIn()
        }
        //botón iniciar sesión con facebook
        btnLogInFacebook.setOnClickListener {
            //falta programar esto
        }
    }
    private fun googleLogIn() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        //esta es la pantalla de google que me dará mis cuentas guardadas en el cel
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            //tomamos el valor de la respuesta (la cuenta seleccionada)
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in, si to do salió bien creamos una constante user que será my curent user y vamos al Main activity y guardamos la info en firestore
                saveinfo()
                val user = mAuth.currentUser
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "Ocurrio un error ${response!!.error!!.errorCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveinfo() {
        val currentUserId = mAuth.currentUser!!.uid
        // creamos la colección en el firestore
        val usersRef = database.reference.child("users")
        usersRef.child(currentUserId).get().addOnSuccessListener {
            if (it.exists()){
                editor.putString("user","client")
                editor.apply()
                    goToMainActivity()
                Toast.makeText(this, "go to main activity", Toast.LENGTH_SHORT).show()
                    finish()
            }else{
                val currentUser = mAuth.currentUser!!
                // para guardar los datos en el firestore debemos crear el siguiente hashmap con los valores que contendrá
                val userMap = HashMap<String, Any>()
                userMap["uid"] = currentUserId
                userMap["name"] = currentUser.displayName.toString()
                userMap["email"] = currentUser.email.toString()
                userMap["password"] = ""
                userMap["phone"] = currentUser.phoneNumber.toString()
                userMap["domiciliario"]=false
                userMap["empresa"]=false
                userMap["photo"]=currentUser.photoUrl.toString()
                userMap["calification"]=5.0
                if (currentUser.photoUrl.toString() == "") {
                    userMap["photo"] = "https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/profile%20default.png?alt=media&token=071099bf-47f8-4369-bb2f-e0637e4b3d8b"
                } else {
                    userMap["photo"] = currentUser.photoUrl.toString()
                }
                //ahora vamos a adicionar ese hashmap llamado en este caso user map al firebasestore
                usersRef.child(currentUserId).setValue(userMap).addOnCompleteListener { Task ->
                    if (Task.isSuccessful) {
                        Toast.makeText(this, "ingreso exitoso", Toast.LENGTH_SHORT).show()
                    } else {
                        // si no fue exitoso o hubo un error, mostraremos el error en un toast
                        val message = Task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                        mAuth.signOut()
                    }
                }
                editor.putString("user","client")
                editor.apply()
                goToMainActivity()
                finish()
            }
        }
    }
    private fun logIn() {
        // si los dos edittext no estan vacios entonces continuamos
        if (etLogInEmail.text.isNotEmpty() && etLogInPassword.text.isNotEmpty()) {

            // si la longitud de la contraseña es menor a 8 mostramos el siguiente error en el edit text
            if (etLogInPassword.length() < 8) {
                etLogInPassword.error = "la contraseña debe tener un mínimo de 8 caracteres"
            } else {
                // si la contraseña tiene el numero de caracteres minimos requeridos entonces seguimos haciendo el sign in
                mAuth.signInWithEmailAndPassword(etLogInEmail.text.toString(), etLogInPassword.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // si el sign in se hizo de manera exitosa vamos a ver si el correo ha sido verificado
                        if (mAuth.currentUser!!.isEmailVerified) {
                            // si ha sido verificado vamos al MainActivity
                            Toast.makeText(this, "usuario verificado", Toast.LENGTH_LONG).show()
                            goToMainActivity()
                        } else {
                            // si no ha sido verificado mostrará el siguiente toast para que lo verifique
                            Toast.makeText(this, "Verifique usuario en el correo electronico que ha sido enviado", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // si hay un error en el sign in mostraremos el error del firebase en un toast
                        val message = it.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                        mAuth.signOut()
                    }
                }
            }
        } else {
            // si están vacias las casillas mostraremos el siguiente toast
            Toast.makeText(this, "ingrese email y contraseña", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    private fun goToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    private fun goToSignUp() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}