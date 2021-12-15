package com.example.domiapp.providers;

import com.example.domiapp.utils.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class TokenProvider {

    DatabaseReference mDatabase;

    public TokenProvider(){
        mDatabase= FirebaseDatabase.getInstance().getReference().child("tokens");
    }

    public void create(String idUser){
        //en caso de que el usuario no tenga un id no va a estar guardando datos nulos en el firebase database
        if (idUser==null) return;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                //esto nos devolvería el nuevo token de usuario
                Token token=new Token(instanceIdResult.getToken());
                mDatabase.child(idUser).setValue(token);
            }
        });
    }
}
