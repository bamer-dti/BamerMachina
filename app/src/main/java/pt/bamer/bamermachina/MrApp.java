package pt.bamer.bamermachina;


import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;

import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.utils.Funcoes;
import pt.bamer.bamermachina.utils.ValoresDefeito;

public class MrApp extends Application {
    private static final String TAG = MrApp.class.getSimpleName();
    private static SharedPreferences prefs;
    private static ProgressDialog dialogoInterminavel;
    private static String maquina;
    private static String operador;
    private static boolean online;

    public static String getMaquina() {
        return maquina;
    }

    public static void setMaquina(String maquina) {
        MrApp.maquina = maquina;
    }

    public static String getOperador() {
        return operador;
    }

    public static void setOperador(String operador) {
        MrApp.operador = operador;
    }

    public static void setOnline(boolean online) {
        MrApp.online = online;
    }

    public static String getEstado() {
        return Constantes.ESTADO_CORTE;
    }

    public static SharedPreferences getPrefs() {
        return prefs;
    }

    public static void mostrarAlertToWait(final Activity activity, final String mensagem) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogoInterminavel == null) {
                    dialogoInterminavel = new ProgressDialog(activity);
                    dialogoInterminavel.setMessage(mensagem);
                    dialogoInterminavel.show();
                } else {
                    dialogoInterminavel.setMessage(mensagem);
                }
            }
        });
    }

    public static void esconderAlertToWait(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogoInterminavel != null) {
                    dialogoInterminavel.hide();
                    dialogoInterminavel.dismiss();
                    dialogoInterminavel = null;
                }
            }
        });
    }

    public static String getSeccao() {
        return prefs.getString(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MrApp created!");
        prefs = getSharedPreferences(Constantes.PREFS_NAME, MODE_PRIVATE);
        Funcoes.checkFirebaseOnline();
        AndroidNetworking.initialize(getApplicationContext());
    }

    public boolean firebaseDatabaseOnline() {
        return online;
    }
}
