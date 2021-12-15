

package com.example.domiapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.domiapp.R
import com.example.domiapp.activities.EmpresaActivity
import com.example.domiapp.includes.Users
import com.example.domiapp.utils.SolicitudesDomi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_solicitudes_de_domicilios.view.*
import java.text.SimpleDateFormat

class solicitudesDomiAdapter (val activity: EmpresaActivity, val dataset:List<SolicitudesDomi>, var clickLitener: onItemClickLitener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    // acá en la primera linea nos dará el size de el data y así tendremos el número de items
    override fun getItemCount() = dataset.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // en from(context) puede ir activity o parent.context
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.fragment_solicitudes_de_domicilios, parent, false)

        return viewHolder(layout)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // (holder as viewHolder).bind(dataset[position])

        (holder as viewHolder).initialize(dataset.get(position), clickLitener)
        holder.publisherInfo(dataset.get(position))

    }

    class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser



        //toma los datos de donde los ingresa el usuario y los asigna a la variable de la clase post

        fun bind(post: SolicitudesDomi) = with(itemView) {
            tvOrigen.text = post.getOrigenName()
            tvDestino.text = post.getDestinoName()
            tvComentario.text = post.getComentario()

        }

        fun initialize(item: SolicitudesDomi, action: onItemClickLitener) {

            itemView.tvOrigen.text = item.getOrigenName()
            itemView.tvDestino.text = item.getDestinoName()
            itemView.tvComentario.text = item.getComentario()
            itemView.tvTiempo.text=item.getTiempo()
            itemView.tvDistancia.text=item.getDistancia()



            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }


        }

        fun publisherInfo(item: SolicitudesDomi) {
          /*  val fireDatabase = FirebaseDatabase.getInstance().getReference()
           val reference= fireDatabase.child("users").child(currentUser!!.uid)

            reference.get().addOnSuccessListener {

                if (it.child("name").toString() != "" && it.child("name").toString() != null) {
                    itemView.tvNombre.text = it.child("name").toString()


                }
            }

           */

            val fireDatabase = FirebaseDatabase.getInstance().getReference()
            val reference= fireDatabase.child("users").child(item.getUserId())
            reference.get().addOnSuccessListener {
                val user=it.getValue<Users>()
                itemView.tvNombre.text=user?.getName()
            }


        }
    }



    interface onItemClickLitener {
        fun onItemClick(item: SolicitudesDomi, position: Int) {

        }

    }


}


