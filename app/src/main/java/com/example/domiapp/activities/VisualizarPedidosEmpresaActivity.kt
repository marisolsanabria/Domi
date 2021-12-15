package com.example.domiapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domiapp.Adapters.solicitudesDomiAdapter
import com.example.domiapp.R
import com.example.domiapp.includes.content
import com.example.domiapp.utils.SolicitudesDomi
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_visualizar_pedidos_empresa.*
import kotlinx.android.synthetic.main.fragment_visualizacion_de_solicitudes_empresa.view.*

class VisualizarPedidosEmpresaActivity : AppCompatActivity() , solicitudesDomiAdapter.onItemClickLitener{

    private lateinit var _view: View

    lateinit var postDBRef: CollectionReference
    private lateinit var adapter: solicitudesDomiAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_pedidos_empresa)

        setUpPostDB()
        setUpRecyclerView()

        window.setBackgroundDrawable(null)



    }

    fun setUpPostDB(){
        postDBRef= FirebaseFirestore.getInstance().collection("solicitudesPedidos")
    }

    fun setUpRecyclerView(){

        postDBRef.addSnapshotListener(EventListener<QuerySnapshot> { value, error ->
//vamos a darle un uid a cada post del firebase para esto lo hacemos con el index

            if (error != null || value == null) {
                // Toast.makeText(context,"error",Toast.LENGTH_LONG).show()
            } else {
                // 7
                val posts = ArrayList<SolicitudesDomi>()
                // 8
                for (doc in value) {
                    // 9
                    val post = doc.toObject(SolicitudesDomi::class.java)
                    posts.add(post)
                }
                posts.forEachIndexed { index, post ->
                    post.uid = value.documents[index].id

                    //ahora cargamos el adapter en el main activity con una cantidad de items igual al tamaño de posts que nos dara la variable posts
                    adapter = solicitudesDomiAdapter(EmpresaActivity(), posts, this)


                    //acá coloco que quiero mostrar y como lo quiero mostrar

                    //tipo de layout en que lo quiero mostrar
                    val layoutManayer = LinearLayoutManager(applicationContext)

                    rvPublicaciones.setHasFixedSize(true)
                    rvPublicaciones.layoutManager = layoutManayer
                    rvPublicaciones.itemAnimator = DefaultItemAnimator()
                    rvPublicaciones.adapter = adapter

                }
            }
        })

    }

    override fun onItemClick(item: SolicitudesDomi, position: Int) {

        content.postId=item.getUserId()

        //findNavController().navigate(R.id.publicacionDialogFragment)
        // Toast.makeText(context, item.getTitle(),Toast.LENGTH_SHORT).show()

    }

}