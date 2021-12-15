
package com.example.domiapp.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.domiapp.R
import com.example.domiapp.includes.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.fragment_registrar_domiciliario_datos2.*
import java.util.regex.Pattern


class RegistrarDomiciliarioDatosFragment2 : Fragment() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore= FirebaseFirestore.getInstance()
    var empresaUsuario=""
    lateinit var empresaUsuarioRef:DocumentReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registrar_domiciliario_datos2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        empresaUsuario=mAuth.currentUser!!.uid
        empresaUsuarioRef=firestore.collection("datosDeEmpresas").document(empresaUsuario)

        btnRegistrarDomiciliario.setOnClickListener {
            activarComoDomiciliario()
            saveUserInfo()


        }

    }

    fun activarComoDomiciliario() {

        val email = etEmail.text.toString()

        val user = FirebaseDatabase.getInstance().reference.child("users").orderByChild("email").equalTo(email)

        if (user != null) {



          user.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    //                   val DomiData = snapshot.children.iterator().next().getValue(Users::class.java)
                    val domidata=snapshot.children.iterator().next().child("uid").getValue().toString()
                    val userDatabaseReference = FirebaseDatabase.getInstance().reference.child("users").child(domidata)

                    val userMap = HashMap<String, Any>()
                    userMap.put("domiciliario", true)

                    userDatabaseReference.updateChildren(userMap).addOnSuccessListener {

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Domiciliario creado con exito")
                        builder.setMessage("Recuerde que etsa cuenta ahora está ligada a su empresa")
                        builder.setPositiveButton("aceptar", null)
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }
            })


        } else{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("La cuenta no existe")
            builder.setMessage("verifique que el correo es correcto, en caso de que lo sea recuerde que su domiciliario debe tener una cuenta de cliente creada")
            builder.setPositiveButton("aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            
        }
    }



    private fun saveUserInfo() {

        val email = etEmail.text.toString()
        val user = FirebaseDatabase.getInstance().reference.child("users").orderByChild("email").equalTo(email)
        if (user != null) {

            user.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val DomiData= snapshot.children.iterator().next().child("uid").getValue().toString()

                    if (DomiData != null) {


                       // val usersRef = FirebaseFirestore.getInstance().collection("datosDeEmpresas").document(empresaUsuario).collection("domiciliarios").document(DomiData)

                     var domiciliariosRef=   empresaUsuarioRef.collection("domiciliario").document(DomiData)

                        // para guardar los datos en el firestore debemos crear el siguiente hashmap con los valores que contendrá
                        val userMap = HashMap<String, Any>()
                        userMap["uid"] = DomiData
                        userMap["name"] = etNombres.text.toString()
                        userMap["apellidos"]=etApellidos.text.toString()
                        userMap["email"] = email
                        userMap["phone"] = etTelefono.text.toString()
                        userMap["id"]=etCédula.text.toString()
                        userMap["domiciliario"] = true
                        userMap["empresa"] = false
                        userMap["photo"] = ""
                        userMap["calification"] = 5.0

                        domiciliariosRef.update(userMap).addOnSuccessListener {

                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("Domiciliario creado con exito")
                            builder.setMessage("Recuerde que etsa cuenta ahora está ligada a su empresa")
                            builder.setPositiveButton("aceptar", null)
                            val dialog: AlertDialog = builder.create()
                            dialog.show()

                        }
                    }

                }
            })

        } else {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("La cuenta no existe")
            builder.setMessage("verifique que el correo es correcto, en caso de que lo sea recuerde que su domiciliario debe tener una cuenta de cliente creada")
            builder.setPositiveButton("aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }

    }



}