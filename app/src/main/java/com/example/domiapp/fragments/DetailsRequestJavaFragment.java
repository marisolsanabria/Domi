package com.example.domiapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.domiapp.MainActivity;
import com.example.domiapp.R;
import com.example.domiapp.activities.RequestDriverActivity;
import com.example.domiapp.includes.content;
import com.example.domiapp.providers.GoogleApiProvider;
import com.example.domiapp.utils.DecodePoints;
import com.example.domiapp.utils.SolicitudesDomi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.google.protobuf.Any;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailsRequestJavaFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private com.google.android.gms.maps.model.LatLng destinoLatLong;
    private String destinoName;
    private com.google.android.gms.maps.model.LatLng originLatLong;
    private String originName;
    private TextView mOriginTextView;
    private TextView mDestinoTextView;
    private TextView mTiempoTextView;
    private TextView mDistanciaTextView;
    private Button mRequestDomi;


    private GoogleApiProvider mGoogleApiProvider;
    private PolylineOptions mPolylineOptions;
    private List<com.google.android.gms.maps.model.LatLng> mPolylineList;

    private FirebaseUser currentUser;
    private DocumentReference postDBRef;




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_details_request_java, container, false);
        mMapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapRequestNowJava);

        originLatLong =content.latlongOrigin;
        destinoLatLong = content.latlongDestiantion;
        mGoogleApiProvider= new GoogleApiProvider(getContext());

        mMapFragment.getMapAsync(this);

        mDistanciaTextView= root.findViewById(R.id.tvDistancia);
        mTiempoTextView=root.findViewById(R.id.tvDuracion);
        mOriginTextView=root.findViewById(R.id.tvOrigen);
        mDestinoTextView=root.findViewById(R.id.tvDestino);
        mRequestDomi=root.findViewById(R.id.btnSolicitarAhora);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();

        setUpPostDB();

        return root;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDestinoTextView.setText(content.Destiantion);
        mOriginTextView.setText(content.Origin);
        mRequestDomi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPublicarBoton();
                goToRequestDriver();

            }
        });


    }

    private void setUpPostDB() {

       postDBRef = FirebaseFirestore.getInstance().collection("solicitudesPedidos").document(currentUser.getUid());


    }

    private void setUpPublicarBoton() {

      /* SolicitudesDomi solicitudDomi= new SolicitudesDomi(content.Origin,content.latlongOrigin.toString(),content.Destiantion,content.latlongDestiantion.toString(),currentUser.getUid(),content.comentario,content.tiempo,content.distancia,content.cityName);


        CollectionReference solicitudesPedidosReference = FirebaseFirestore.getInstance().collection("solicitudesPedidos");

        HashMap<String, String> newSolicitud=new HashMap<String, String>();
        newSolicitud.put("origenName",solicitudDomi.getOrigenName());
        newSolicitud.put("origenLatLng", solicitudDomi.getOrigenLatLng());
        newSolicitud.put("destinoName", solicitudDomi.getDestinoName());
        newSolicitud.put("destinoLatLng", solicitudDomi.getDestinoLatLng());
        newSolicitud.put("userID", solicitudDomi.getUserId());
        newSolicitud.put("comentario",solicitudDomi.getComentario());
        newSolicitud.put("tiempo", solicitudDomi.getTiempo());
        newSolicitud.put("distancia",solicitudDomi.getDistancia());
        newSolicitud.put("ciudad",solicitudDomi.getCityName());

//el add es de tipo mutable <string,any>
        //acá agregamos el mensaje a la colección
        postDBRef.set(newSolicitud).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(),"error, intente de nuevo", Toast.LENGTH_LONG).show();
            }
        });
        */

        SolicitudesDomi solicitudDomi= new SolicitudesDomi(content.Origin,content.latlongOrigin.toString(),content.Destiantion,content.latlongDestiantion.toString(),currentUser.getUid(),content.comentario,content.tiempo,content.distancia,content.cityName);


        DatabaseReference solicitudesPedidosReference = FirebaseDatabase.getInstance().getReference().child("solicitudesPedidos");

        HashMap<String, String> newSolicitud=new HashMap<String, String>();
        newSolicitud.put("origenName",solicitudDomi.getOrigenName());
        newSolicitud.put("origenLatLng", solicitudDomi.getOrigenLatLng());
        newSolicitud.put("destinoName", solicitudDomi.getDestinoName());
        newSolicitud.put("destinoLatLng", solicitudDomi.getDestinoLatLng());
        newSolicitud.put("userID", solicitudDomi.getUserId());
        newSolicitud.put("comentario",solicitudDomi.getComentario());
        newSolicitud.put("tiempo", solicitudDomi.getTiempo());
        newSolicitud.put("distancia",solicitudDomi.getDistancia());
        newSolicitud.put("ciudad",solicitudDomi.getCityName());

//el add es de tipo mutable <string,any>
        //acá agregamos el mensaje a la colección
        solicitudesPedidosReference.child(currentUser.getUid()).setValue(newSolicitud).addOnFailureListener(new OnFailureListener(){
       // postDBRef.set(newSolicitud).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(),"error, intente de nuevo", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void goToRequestDriver(){
        Intent intent= new Intent(requireContext(), RequestDriverActivity.class);
        startActivity(intent);

    }
    private void saveInFirebase(){

        SolicitudesDomi solicitudDomi= new SolicitudesDomi(content.Origin,content.latlongOrigin.toString(),content.Destiantion,content.latlongDestiantion.toString(),currentUser.getUid(),content.comentario,content.tiempo,content.distancia,content.cityName);

        DatabaseReference solicitudesPedidosReference = FirebaseDatabase.getInstance().getReference().child("solicitudesPedidos");

        HashMap<String, String> meMap=new HashMap<String, String>();
        meMap.put("origen", solicitudDomi.getOrigenName());
        meMap.put("destino", solicitudDomi.getDestinoName());
        meMap.put("tiempo", solicitudDomi.getTiempo());
        meMap.put("distancia", solicitudDomi.getDistancia());

        solicitudesPedidosReference.push().setValue(meMap);



       /* solicitudesPedidosReference.push().setValue(content.Origin);
        solicitudesPedidosReference.push().setValue(content.Destiantion);
        solicitudesPedidosReference.push().setValue(content.distancia);
        solicitudesPedidosReference.push().setValue(content.tiempo);

        */

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap=googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.addMarker(new MarkerOptions().position(destinoLatLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_map_pin_100)));
        mMap.addMarker(new MarkerOptions().position(originLatLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_ubicacion_usuario)));

        drawRoute();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(originLatLong).zoom(14f).build()
        ));


    }

    private void drawRoute(){

        mGoogleApiProvider.getDirections(originLatLong,destinoLatLong).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject= new JSONObject(response.body().toString());
                    JSONArray jsonArray= jsonObject.getJSONArray("routes");
                    JSONObject route= jsonArray.getJSONObject(0);
                    JSONObject polylines= route.getJSONObject("overview_polyline");
                    String points= polylines.getString("points");

                    mPolylineList= DecodePoints.decodePoly(points);
                    mPolylineOptions= new PolylineOptions();
                    //vamos a establecerle unas propiedades como: color, ancho,
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(13f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);

                    //para obtener la distancia hacemos lo siguiente

                    JSONArray legs=route.getJSONArray("legs");
                    JSONObject leg=legs.getJSONObject(0);
                    JSONObject distance= leg.getJSONObject("distance");
                   // ahora para tomar la duración
                    JSONObject duration= leg.getJSONObject("duration");

                    //Para tomar el texto que es lo que en realidad necesito entonces haremos lo siguiente:

                    String distanceText= distance.getString("text");
                    String durationText= duration.getString("text");
                    mDistanciaTextView.setText(distanceText);
                    mTiempoTextView.setText(durationText);

                    content.distancia=distanceText;
                    content.tiempo=durationText;

                }catch (Exception e){
                    Log.d("Error","Error econtrado: "+e.getMessage());

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}