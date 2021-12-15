package com.example.domiapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domiapp.Adapters.solicitudesDomiAdapter
import com.example.domiapp.R
import com.example.domiapp.activities.EmpresaActivity
import com.example.domiapp.includes.Users
import com.example.domiapp.includes.content
import com.example.domiapp.providers.GeofireProvider
import com.example.domiapp.utils.SolicitudesDomi
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_visualizacion_de_solicitudes_empresa.view.*


class VisualizacionDeSolicitudesEmpresaFragment :DialogFragment(),solicitudesDomiAdapter.onItemClickLitener{

    private lateinit var _view:View

    lateinit var postDBRef:CollectionReference
    private lateinit var adapter: solicitudesDomiAdapter

    lateinit var mGeofireProvider: GeofireProvider
    private lateinit var geoQuery: GeoQuery

    var radius= 20.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
       _view= inflater.inflate(R.layout.fragment_visualizacion_de_solicitudes_empresa, container, false)

        getPedidos()

        setUpPostDB()
        //setUpRecyclerView()


        view?.setBackground(null)





        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mGeofireProvider= GeofireProvider()
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
                    val layoutManayer = LinearLayoutManager(context)

                    _view.rvPublicaciones.setHasFixedSize(true)
                    _view.rvPublicaciones.layoutManager = layoutManayer
                    _view.rvPublicaciones.itemAnimator = DefaultItemAnimator()
                    _view.rvPublicaciones.adapter = adapter

                }
            }
        })

    }
  fun  getPedidos(){
      val pedidosCerca= FirebaseDatabase.getInstance().reference.child("solicitudesPedidos")

      //revisar que creo que el Geofire solo recibe database real time, si es así crear un archivo en database y mandar los uid
      //para tomar los valores del firestore de la card de las solicitudes


      //le debo pasar la ubicacion del que está buscando conductor y el radio de busqueda

      val geofire = GeoFire(pedidosCerca)
      //FirebaseFirestore.getInstance().collection("datosDeEmpresas").document(FirebaseAuth.getInstance().currentUser.uid).get().addOnSuccessListener {
         // val ubicacionEmpresa=it.get("latlong") as com.google.type.LatLng

   FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser.uid).get().addOnSuccessListener {
       val userinfo= it.getValue<Users>()!!

       val latitud=userinfo.getLatitud()
       val longitud= userinfo.getLongitud()
       Toast.makeText(context,latitud.toString() + longitud.toString(),Toast.LENGTH_LONG).show()

       if (latitud !=null ){
           geoQuery= geofire.queryAtLocation(GeoLocation(latitud,longitud), 20.0)
           //geoQuery.removeAllListeners()

           //mGeofireProvider.getActiveDrivers(mCurrentLatLng).addGeoQueryEventListener(object : GeoQueryEventListener {
          geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
               override fun onGeoQueryReady() {
                   setUpRecyclerView()
               }

               override fun onKeyEntered(key: String?, location: GeoLocation?) {
                  FirebaseDatabase.getInstance().reference.child("posts").child("key").addListenerForSingleValueEvent(object: ValueEventListener{
                      override fun onCancelled(error: DatabaseError) {
                          TODO("Not yet implemented")
                      }

                      override fun onDataChange(snapshot: DataSnapshot) {
                          val p=snapshot.getValue(SolicitudesDomi::class.java)
                          val postValues: Map<String, Any> = p as Map<String, Any>
                          val childUpdates: Map<String, Any> = HashMap()
                          childUpdates.get("/posts_locations_by_user/"+ FirebaseAuth.getInstance().currentUser.uid +"/"+ key+postValues)
                         //verificar habia un error en el postValues dentro de los parentesis

                      }

                  })
               }

               override fun onKeyMoved(key: String?, location: GeoLocation?) {
                   TODO("Not yet implemented")
               }

               override fun onKeyExited(key: String?) {
                   TODO("Not yet implemented")
               }

               override fun onGeoQueryError(error: DatabaseError?) {
                   TODO("Not yet implemented")
               }


           })


       }


   }







      }



    override fun onItemClick(item: SolicitudesDomi, position: Int) {

        content.postId = item.getUserId()

        //findNavController().navigate(R.id.publicacionDialogFragment)
        // Toast.makeText(context, item.getTitle(),Toast.LENGTH_SHORT).show()

    }


}