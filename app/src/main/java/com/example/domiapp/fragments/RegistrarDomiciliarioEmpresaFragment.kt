
package com.example.domiapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.domiapp.R
import kotlinx.android.synthetic.main.fragment_registrar_domiciliario_empresa.*

class RegistrarDomiciliarioEmpresaFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registrar_domiciliario_empresa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fbGoToRegistrarDatos()
    }

    private fun fbGoToRegistrarDatos() {
        fbAddDomiciliario.setOnClickListener{
            findNavController().navigate(R.id.registrarDomiciliarioDatosFragment2)
        }
    }


}