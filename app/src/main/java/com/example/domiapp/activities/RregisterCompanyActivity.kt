package com.example.domiapp.activities

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.domiapp.MainActivity
import com.example.domiapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_rregister_company.*

class RregisterCompanyActivity : AppCompatActivity() {

    private val mAuth:FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    lateinit var mpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var selectedU:String?=""

    var currentUserUid =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rregister_company)

        currentUserUid= mAuth.currentUser!!.uid

        mpref = applicationContext.getSharedPreferences("typeUser", MODE_PRIVATE)
        editor = mpref.edit()
        selectedU = mpref.getString("user", "")

        btnEnviarSolicitudEmpresa.setOnClickListener {
            envioDeSolicitud()
        }

    }

    private fun envioDeSolicitud() {

        val nombreCompany = etNameCompany.text.toString()
        val telefonoCompany = etNumeroContacto.text.toString()
        val direccionCompany = etDireccion.text.toString()
        val nitCompany = etNIT.text.toString()

        if (etNameCompany.text.isEmpty() || etNumeroContacto.text.isEmpty() || etNIT.text.isEmpty() || etDireccion.text.isEmpty()) {
            showAlertCompleteData()
        } else {
            val progressDialogProcesandoInfo= ProgressDialog(this)
            progressDialogProcesandoInfo.setTitle("VERIFICACIÓN EN PROCESO")
            progressDialogProcesandoInfo.setMessage("Estamos verificando tus datos, pronto nos comunicaremos contigo")
            progressDialogProcesandoInfo.setCanceledOnTouchOutside(false)
            progressDialogProcesandoInfo.show()

            val currentUserId=mAuth.currentUser!!.uid


            val usersRef= FirebaseFirestore.getInstance().collection("solicitudempresas").document(currentUserId)

            // para guardar los datos en el firestore debemos crear el siguiente hashmap con los valores que contendrá
            val empresaMap= HashMap<String,Any>()
            empresaMap["uid"]=currentUserUid.toString()
            empresaMap["name"]=nombreCompany
            empresaMap["email"]=mAuth.currentUser!!.email.toString()
            empresaMap["phone"]=telefonoCompany
            empresaMap["direccion"]=direccionCompany
            empresaMap["nit"]=nitCompany

            //ahora vamos a adicionar ese hashmap llamado en este caso user map al firebasestore
            usersRef.set(empresaMap).
            addOnCompleteListener{ Task ->
                if (Task.isSuccessful) {
                    progressDialogProcesandoInfo.dismiss()
                    showVerificationInProgress()
                }
            }
            /*PRIMER FORMA DE ENVIAR UN CORREO
            val i= Intent(Intent.ACTION_SENDTO)
            i.setData(Uri.parse("mailto:" + "residente.electrou@gmail.com"))
            i.putExtra(Intent.EXTRA_SUBJECT,currentUserId)
            i.putExtra(Intent.EXTRA_TEXT, nombreCompany)

            try{
                startActivity(Intent.createChooser(i, "enviar email usando:"))
            }catch(e: android.content.ActivityNotFoundException){
                android.widget.Toast.makeText(this, "necesita una aplicación de email instalada",android.widget.Toast.LENGTH_LONG).show()

            }

             */

            val i= Intent(Intent.ACTION_SEND)
            i.setType("message/rfc822")
            i.putExtra(Intent.EXTRA_EMAIL,"residente.electrou@gmail.com")
            i.putExtra(Intent.EXTRA_SUBJECT,currentUserId)
            i.putExtra(Intent.EXTRA_TEXT,nombreCompany)

            try {
                startActivity(Intent.createChooser(i,"enviar email...."))
            } catch (e: android.content.ActivityNotFoundException){
                Toast.makeText(this, "necesita una aplicación de email instalada",android.widget.Toast.LENGTH_LONG).show()
            }




            /*  val intent =Intent(Intent.ACTION_VIEW, Uri.parse("mailto: "+ "marisolsanabrianaranjo@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT,currentUserUid)
            intent.putExtra(Intent.EXTRA_TEXT, nombreCompany)
            startActivity(intent)

           */


        }
    }

    private fun showVerificationInProgress() {
        val positiveButton= { dialog: DialogInterface, which: Int ->
            finish()

        }

        val builder= AlertDialog.Builder(this)
        builder.setTitle("VERIFICAIÓN EN PRECESO")
        builder.setMessage("Estamos verificando tus datos, pronto nos comunicaremos contigo")
        builder.setPositiveButton("aceptar",positiveButton)
        builder.setCancelable(false)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    private fun showAlertCompleteData() {
        val builder= AlertDialog.Builder(this)
        builder.setMessage("Porfavor completa toda la información")
        builder.setPositiveButton("aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }
    fun goToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun goToDomiciliarioActivity(){
        val intent = Intent(this, DomiciliariosActivity::class.java)
        startActivity(intent)
        finish()
    }


}