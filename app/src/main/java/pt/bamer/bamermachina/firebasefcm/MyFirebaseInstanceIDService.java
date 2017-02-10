package pt.bamer.bamermachina.firebasefcm;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.pojos.TokenFirebase;

/**
 * Criado por miguel.silva on 06-02-2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    public static void sendRegistrationToServer(String refreshedToken) {
        Log.w(TAG, "TokenPDA: " + refreshedToken);
        MrApp.tokenMachine = new TokenFirebase(refreshedToken, true);
        MrApp.tokenMachine.setOperador(MrApp.getOperadorNome());

        MrApp.tokenUser = MrApp.tokenMachine;
        MrApp.tokenUser.setMachina(MrApp.getMaquina());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("tokens").child("maquinas").child(MrApp.getMaquina()).setValue(MrApp.tokenMachine);
        ref.child("tokens").child("operadores").child(MrApp.getOperadorCodigo()).setValue(MrApp.tokenUser);
    }

    public static void unRegisterUtilizador() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        MrApp.tokenUser.setOnline(false);
        MrApp.tokenMachine.setOnline(false);
        ref.child("tokens").child("maquinas").child(MrApp.getMaquina()).setValue(MrApp.tokenMachine);
        ref.child("tokens").child("operadores").child(MrApp.getOperadorCodigo()).setValue(MrApp.tokenUser);
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
}
