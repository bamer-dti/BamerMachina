package pt.bamer.bamermachina;


import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;

import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.pojos.TokenFirebase;
import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.utils.Funcoes;
import pt.bamer.bamermachina.utils.ValoresDefeito;

public class MrApp extends Application {
    private static final String TAG = MrApp.class.getSimpleName();
    public static android.media.ToneGenerator toneG;
    public static TokenFirebase tokenMachine;
    public static TokenFirebase tokenUser;
    public static int contadorSMSMachina = 0;
    public static int contadorSMSUser = 0;
    private static SharedPreferences prefs;
    private static ProgressDialog dialogoInterminavel;
    private static String maquina;
    private static String operadorCodigo;
    private static String operadorNome;

    public static String getMaquina() {
        return maquina;
    }

    public static void setMaquina(String maquina) {
        MrApp.maquina = maquina;

    }

    public static String getOperadorCodigo() {
        return operadorCodigo;
    }

    public static void setOperadorCodigo(String operadorCodigo, Context context) {
        MrApp.operadorCodigo = operadorCodigo;
        MrApp.operadorNome = new DBSQLite(context).getNomeOperador(operadorCodigo);
    }

    public static String getOperadorNome() {
        return operadorNome;
    }

    public static String getEstado() {
        return Constantes.ESTADO_CORTE;
    }

    public static SharedPreferences getPrefs() {
        return prefs;
    }

    public static void mostrarAlertToWait(final Context activity, final String mensagem) {
        ((Activity) activity).runOnUiThread(new Runnable() {
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

    public static void esconderAlertToWait(Context activity) {
        ((Activity) activity).runOnUiThread(new Runnable() {
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

    public static String getTituloBase(Context context) {
        String versao = context.getString(R.string.app_name);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versao += " " + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versao;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MrApp created!");
        prefs = getSharedPreferences(Constantes.PREFS_NAME, MODE_PRIVATE);
        Funcoes.checkFirebaseOnline();
        AndroidNetworking.initialize(getApplicationContext());
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        System.exit(0);
    }
}
