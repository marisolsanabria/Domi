package com.example.domiapp.services;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class myFirebaseMessagingClient extends FirebaseMessagingService {

    //en este método es donde vamos a estar recibiendo las notificaiones
    //debemos registrar este servicio en el android manifest

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    //esto nos ayuda a generar un token de usuario con el fin de enviar notificaciones de dispositivo a dispositivo


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //este método nos servirá para generar un token de usuario para enviar notificaciones de dispositivo a dispositivo

    }
}
