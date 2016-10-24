package pt.bamer.bamermachina.webservices;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.pojos.JSONObjectQtd;
import pt.bamer.bamermachina.pojos.JSONObjectTimer;
import pt.bamer.bamermachina.utils.Funcoes;

public class WebServices {
//    public static final String SERVER_WEBSERVICES = "http://192.168.0.1:99/bameros.svc/";
        public static final String SERVER_WEBSERVICES = "http://server.bamer.pt:99/bameros.svc/";
    public static final String JSON_URL_REGISTAR_TEMPO = SERVER_WEBSERVICES + "registartempo";
    public static final String JSON_URL_REGISTAR_QTD = SERVER_WEBSERVICES + "registarqtd";
    private static final String TAG = WebServices.class.getSimpleName();
    private static final String HTTP_OK = "ok";
    private static final String HTTP_MENSAGEM = "mensagem";
    private static final MediaType TEXTO = MediaType.parse("text/plain");

    public static void registarTempoemSQL(final Activity activity, final JSONObjectTimer jsonObjectTimer) {
        MrApp.mostrarAlertToWait(activity, "A gravar no servidor, aguarde...");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(TEXTO, jsonObjectTimer.toString());
        String url = JSON_URL_REGISTAR_TEMPO;
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "text/plain")
                .post(body)
                .build();

        Callback callbackResposta = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MrApp.esconderAlertToWait(activity);
                e.printStackTrace();
                Funcoes.alerta(activity, "Erro", "A gravação de tempo no webservice não foi efectuada. O erro é:\n" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // String loginResponseString = response.body().string();
                MrApp.esconderAlertToWait(activity);
                try {
                    final JSONObject responseObj = new JSONObject(response.body().string());
                    final String mensagem = responseObj.getString(HTTP_MENSAGEM);
                    final boolean ok = responseObj.getBoolean(HTTP_OK);
                    Log.i(TAG, response.code() + ": responseObj: " + responseObj);
                    if (ok) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Gravado com sucesso, aguarde um momento para actualizar informação", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Funcoes.alerta(activity, "ERRO", "Não foi possivel gravar:\n" + mensagem);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Log.i(TAG, "loginResponseString: " + loginResponseString);
            }
        };

        client.newCall(request).enqueue(callbackResposta);
    }

    @SuppressWarnings("unused")
    private static void lancarTempoEmCouch(final Context context, final JSONObjectTimer jsonObject, final String idcouch, final ProgressDialog dialog) {
        Log.i(TAG, "Json\n" + jsonObject.toString());
//        AndroidNetworking.put(ServicoCouchBase.COUCH_SERVER_AND_DB_URL + idcouch)
//                .addJSONObjectBody(jsonObject)
//                .setTag("test")
//                .addHeaders("Content-Type", "application/json")
//                .addHeaders("Authorization", "Basic c3luY3VzZXI6U3luY1VzZXIjMTAh")
//                .addHeaders("If-Match", "")
//                .addHeaders("Content-Length", "" + jsonObject.toString().length())
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            Log.i(TAG, "resposta  = " + response.getBoolean("ok"));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        dismissDialog(dialog);
////                        if (jsonObject.getPosicao() == Constantes.MODO_STARTED) {
////                            ListaOS.ColocarOSemTrabalhoAposWebService((ListaOS) context, jsonObject);
//////                            ListaOS.retirarRegistoDaLista((ListaOS) context, jsonObject);
////                        } else {
////                            ListaOS.pararCronometro((ListaOS) context);
////                        }
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        if (error.getErrorCode() != 0) {
//                            Log.e(TAG, "onError errorCode : " + error.getErrorCode());
//                            Log.e(TAG, "onError errorBody : " + error.getErrorBody());
//                            Log.e(TAG, "onError errorDetail : " + error.getErrorDetail());
//                        } else {
//                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
//                            Log.e(TAG, "onError errorDetail : " + error.getErrorDetail());
//                        }
//                        dismissDialog(dialog);
//                        Funcoes.alerta(context, "Erro", "Ocorreu um erro ao gravar via webservice.\nTente novamente. Se o erro persistir, contacte o DTI: " + error.getErrorDetail());
//                    }
//                });
    }

    public static void registarQtdEmSQL(final Activity activity, final Object viewOrigem, final int qtd_total, final int qtd, final JSONObjectQtd jsonObjectQtd) {
        MrApp.mostrarAlertToWait(activity, "A gravar no servidor, aguarde...");
//        Log.w(TAG, "JSON OSPROD:\n" + jsonObjectQtd.toString());
//
//        AndroidNetworking.post(JSON_URL_REGISTAR_QTD)
//                .addStringBody(jsonObjectQtd.toString())
//                .setTag("test")
//                .addHeaders("Content-Type", "text/plain")
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            boolean resultado = response.getBoolean(HTTP_OK);
//                            String mensagem = response.getString(HTTP_MENSAGEM);
//                            Log.i(TAG, "code  = " + resultado + ": " + mensagem);
//                            if (!resultado) {
//                                Log.e(TAG, "Erro ao gravar\n" + jsonObjectQtd.toString());
//                                Funcoes.alerta(activity, "Erro", "A gravação de tempo no webservice não foi efectuada. O erro é:\n" + mensagem);
//                            } else {
//                                if (viewOrigem != null) {
//                                    if (viewOrigem instanceof Button) {
//                                        Button but = (Button) viewOrigem;
//                                        but.setText((qtd + qtd_total) + "");
//                                        MrApp.esconderAlertToWait(activity);
//                                    }
//                                    if (viewOrigem instanceof TarefaRecyclerAdapter.ViewHolder) {
//                                        MrApp.mostrarAlertToWait(activity, "A obter dados do servidor, aguarde...");
//                                    }
//                                }
//                            }
//                        } catch (JSONException e) {
//                            MrApp.esconderAlertToWait(activity);
//                            Funcoes.alerta(activity, "Erro", "Ocorreu um erro interno no webservice.\nTente novamente. Se o erro persistir, contacte o DTI: " + e.getLocalizedMessage());
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        if (error.getErrorCode() != 0) {
//                            Log.e(TAG, "onError errorCode : " + error.getErrorCode());
//                            Log.e(TAG, "onError errorBody : " + error.getErrorBody());
//                            Log.e(TAG, "onError errorDetail : " + error.getErrorDetail());
//                        } else {
//                            Log.e(TAG, "onError errorDetail : " + error.getErrorDetail());
//                        }
//                        MrApp.esconderAlertToWait(activity);
//                        Log.w(TAG, "JSON: " + jsonObjectQtd.toString());
//                        Funcoes.alerta(activity, "Erro", "Ocorreu um erro em <registarQtdEmSQL> ao gravar via webservice.\nTente novamente. Se o erro persistir, contacte o DTI: " + error.getErrorDetail());
//                    }
//                });
    }
}
