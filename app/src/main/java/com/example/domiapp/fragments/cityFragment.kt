package com.example.domiapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.domiapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [cityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class cityFragment : Fragment() {
    private lateinit var cityViewModell: cityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cityViewModell =
            ViewModelProvider(this).get(cityViewModel::class.java)
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_city, container, false)
        val textView: TextView = root.findViewById(R.id.tvCity)
        cityViewModell.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

}