package com.example.domiapp.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.annotation.Target;
import java.util.Objects;

public class AuthProvider {
    FirebaseAuth mAuth;

    public AuthProvider() {mAuth=FirebaseAuth.getInstance();}

    public Task<AuthResult> register(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email,password);
    }

    public void logOut(){ mAuth.signOut();}

    public String getId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Boolean existSession(){
        boolean exist= false;
        if (mAuth.getCurrentUser()!= null){
            exist= true;
        }
        return  exist;
    }
}
