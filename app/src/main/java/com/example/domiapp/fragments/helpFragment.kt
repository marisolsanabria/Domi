
package com.example.domiapp.fragments

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.domiapp.MainActivity
import com.example.domiapp.R
import com.example.domiapp.activities.DomiciliariosActivity
import com.example.domiapp.activities.EmpresaActivity
import com.example.domiapp.activities.RregisterCompanyActivity
import com.example.domiapp.includes.Users
import com.example.domiapp.includes.content
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.fragment_help.*
import kotlin.contracts.contract

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [helpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class helpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var mpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var selectedU:String?=""

    private lateinit var database: FirebaseDatabase
    lateinit var currentUserUid: String
    lateinit var userinfo:Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        database= FirebaseDatabase.getInstance()

        userinfo= Users()

        currentUserUid= FirebaseAuth.getInstance().currentUser!!.uid


        mpref= this.requireActivity().getSharedPreferences("typeUser", AppCompatActivity.MODE_PRIVATE)

        editor  =mpref.edit()

        selectedU=mpref.getString("user","")



        return inflater.inflate(R.layout.fragment_help, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner()


    }
    fun spinner(){
        var profileOptions= arrayOf("escoge el tipo de perfil","cliente","domiciliario","empresa")
        // spinnerProfileType.adapter= context?.
        //let { ArrayAdapter<String>(it,android.R.layout.simple_list_item_1,profileOptions) }
        spinnerHelp.adapter= context?.let { ArrayAdapter<String>(it,android.R.layout.simple_list_item_1,profileOptions) }
        spinnerHelp.setSelection(0,true)



        spinnerHelp.onItemSelectedListener =object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val usersRef = database.reference.child("users")
                if ( profileOptions.get(position)=="domiciliario"){

                    usersRef.child(currentUserUid).get().addOnSuccessListener {
                        userinfo = it.getValue<Users>()!!

                        if (userinfo.getdomiciliario()==true){

                            Toast.makeText(context, "domiciliario", Toast.LENGTH_SHORT).show()
                            editor.putString("user", "domiciliario")
                            goToDomiciliarioActivity()
                            activity?.finish()
                            editor.apply()
                        }else{
                            spinnerHelp.setSelection(0,true)
                            alertDialogDomiciliario()
                        }
                    }
                }
                if (profileOptions.get(position)=="cliente"){
                    Toast.makeText(context,"cliente", Toast.LENGTH_SHORT).show()
                    editor.putString("user","cliente")
                    goToMainActivity()
                    activity?.finish()

                    editor.apply()
                }
                if (profileOptions.get(position)=="empresa"){



                    usersRef.child(currentUserUid).get().addOnSuccessListener {
                        userinfo = it.getValue<Users>()!!

                        if (userinfo.getempresa() == true) {
                            Toast.makeText(context, "empresa", Toast.LENGTH_SHORT).show()
                            editor.putString("user", "empresa")
                            editor.apply()
                            goToEmpresaActivy()
                            activity?.finish()
                        } else{
                            spinnerHelp.setSelection(0,true)
                            tvhelp.setText("empresa false"+userinfo.getempresa().toString())
                            alertDialogEmpresa()


                        }
                    }.addOnFailureListener {
                        Toast.makeText(context,"Error if empresa"+it,Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }
    }

    fun alertDialogDomiciliario(){

        val builder=AlertDialog.Builder(requireContext())
        builder.setTitle("Perfil inactivo")
        builder.setMessage("El perfil de domiciliario solo puede ser activado por la empresa, si eres domiciliario de una empresa activa, comunicate con ella")
        builder.setPositiveButton("aceptar",null)
        val dialog:AlertDialog=builder.create()
        dialog.show()

    }

    fun alertDialogEmpresa(){

  val negativeButton= { dialog: DialogInterface, which: Int ->
      val intent = Intent(activity, RregisterCompanyActivity::class.java)
      startActivity(intent)
  }
        val builder=AlertDialog.Builder(requireContext())
        builder.setTitle("Perfil inactivo")
        builder.setMessage("Si quieres utilizar nuestros servicios solicita tu Registro")
        builder.setPositiveButton("aceptar",null)
        builder.setNegativeButton("REGISTRAR MI EMPRESA",negativeButton)
        val dialog:AlertDialog=builder.create()
        dialog.show()

    }

    fun goToDomiciliarioActivity(){
        val intent = Intent(activity, DomiciliariosActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    fun goToMainActivity(){
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
    fun goToEmpresaActivy(){
        val intent = Intent(activity, EmpresaActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment helpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            helpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}