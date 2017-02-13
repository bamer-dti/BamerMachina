package pt.bamer.bamermachina.webservices;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import pt.bamer.bamermachina.BancadaTrabalho;
import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.pojos.JSONObjectQtd;
import pt.bamer.bamermachina.pojos.JSONObjectTimer;
import pt.bamer.bamermachina.utils.Funcoes;

public class WebServices {
    public static final String SERVER_WEBSERVICES = "http://192.168.0.1:99/bameros.svc/";
    //    public static final String SERVER_WEBSERVICES = "http://server.bamer.pt:99/bameros.svc/";
    public static final String JSON_URL_REGISTAR_TEMPO = SERVER_WEBSERVICES + "registartempo";
    public static final String JSON_URL_REGISTAR_QTD = SERVER_WEBSERVICES + "registarqtd";
    private static final String TAG = WebServices.class.getSimpleName();
    private static final String JSON_OK = "ok";
    private static final String JSON_MENSAGEM = "mensagem";

    public static void registarTempoemSQL(final Context context, final JSONObjectTimer jsonObjectTimer, final BancadaTrabalho bancadaTrabalho) {
        MrApp.mostrarAlertToWait(context, "A gravar no servidor, aguarde...");

        AndroidNetworking.post(JSON_URL_REGISTAR_TEMPO)
                .addStringBody(jsonObjectTimer.toString())
                .setTag("test")
                .addHeaders("Content-Type", "text/plain")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean resultado = response.getBoolean(JSON_OK);
                            String mensagem = response.getString(JSON_MENSAGEM);
                            Log.i(TAG, "code  = " + resultado + ": " + mensagem);
                            MrApp.esconderAlertToWait(context);
                            if (!resultado) {
                                Log.e(TAG, "Erro ao gravar\n" + jsonObjectTimer.toString());
                                Funcoes.alerta(context, "Erro", "A gravação de tempo no webservice não foi efectuada. O erro é:\n" + mensagem);
                            } else {
                                Toast.makeText(context, "Gravado com sucesso, aguarde um momento para actualizar informação", Toast.LENGTH_LONG).show();
//                                MrApp.mostrarAlertToWait(activity, "A obter dados do servidor, aguarde...");
                                bancadaTrabalho.actualizarDados();
                            }
                        } catch (JSONException e) {
                            MrApp.esconderAlertToWait(context);
                            Funcoes.alerta(context, "Erro", "Ocorreu um erro interno no webservice.\nTente novamente. Se o erro persistir, contacte o DTI: " + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            Log.e(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.e(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.e(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            Log.e(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                        MrApp.esconderAlertToWait(context);
                        Funcoes.alerta(context, "Erro", "Ocorreu um erro em <registarTempoemSQL> ao gravar via webservice.\nTente novamente. Se o erro persistir, contacte o DTI: " + error.getErrorDetail());
                        Log.i(TAG, jsonObjectTimer.toString());
                    }
                });
    }

    public static void registarQtdEmSQL(final Context activity, final Object viewOrigem, final int qtd_total, final int qtd, final JSONObjectQtd jsonObjectQtd) {
        MrApp.mostrarAlertToWait(activity, "A gravar no servidor, aguarde...");
        Log.w(TAG, "JSON OSPROD:\n" + jsonObjectQtd.toString());

        AndroidNetworking.post(JSON_URL_REGISTAR_QTD)
                .addStringBody(jsonObjectQtd.toString())
                .setTag("test")
                .addHeaders("Content-Type", "text/plain")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MrApp.esconderAlertToWait(activity);
                        try {
                            boolean resultado = response.getBoolean(JSON_OK);
                            String mensagem = response.getString(JSON_MENSAGEM);
                            Log.i(TAG, "code  = " + resultado + ": " + mensagem);
                            if (!resultado) {
                                Log.e(TAG, "Erro ao gravar\n" + jsonObjectQtd.toString());
                                Funcoes.alerta(activity, "Erro", "A gravação de tempo no webservice não foi efectuada. O erro é:\n" + mensagem);
                            } else {
                                if (viewOrigem != null) {
                                    if (viewOrigem instanceof Button) {
                                        Button but = (Button) viewOrigem;
                                        but.setText((qtd + qtd_total) + "");
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            MrApp.esconderAlertToWait(activity);
                            Funcoes.alerta(activity, "Erro", "Ocorreu um erro interno no webservice.\nTente novamente. Se o erro persistir, contacte o DTI: " + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            Log.e(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.e(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.e(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            Log.e(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                        MrApp.esconderAlertToWait(activity);
                        Log.w(TAG, "JSON: " + jsonObjectQtd.toString());
                        Funcoes.alerta(activity, "Erro", "Ocorreu um erro em <registarQtdEmSQL> ao gravar via webservice.\nTente novamente. Se o erro persistir, contacte o DTI: " + error.getErrorDetail());
                    }
                });
    }
}
