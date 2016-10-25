package pt.bamer.bamermachina.webservices;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import pt.bamer.bamermachina.ActivityListaOS;
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
    private static final String JSON_OK = "ok";
    private static final String JSON_MENSAGEM = "mensagem";
    private static final MediaType TEXTO = MediaType.parse("text/plain");

    public static void registarTempoemSQL(final Activity activity, final JSONObjectTimer jsonObjectTimer) {
        final ActivityListaOS activityListaOS;
        if (activity instanceof ActivityListaOS) {
            activityListaOS = (ActivityListaOS) activity;
        } else {
            Funcoes.alerta(activity, "Erro...", "Não pode utilizar o comando registarTempoSQL porque não tem origem na Activity ActivityListaOS");
            return;
        }
        MrApp.mostrarAlertToWait(activityListaOS, "A gravar no servidor, aguarde...");

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
                            MrApp.esconderAlertToWait(activity);
                            if (!resultado) {
                                Log.e(TAG, "Erro ao gravar\n" + jsonObjectTimer.toString());
                                Funcoes.alerta(activity, "Erro", "A gravação de tempo no webservice não foi efectuada. O erro é:\n" + mensagem);
                            } else {
                                Toast.makeText(activity, "Gravado com sucesso, aguarde um momento para actualizar informação", Toast.LENGTH_LONG).show();
//                                MrApp.mostrarAlertToWait(activity, "A obter dados do servidor, aguarde...");
                                activityListaOS.getBancadaTrabalho().actualizarDados();
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
                        Funcoes.alerta(activity, "Erro", "Ocorreu um erro em <registarTempoemSQL> ao gravar via webservice.\nTente novamente. Se o erro persistir, contacte o DTI: " + error.getErrorDetail());
                        Log.i(TAG, jsonObjectTimer.toString());
                    }
                });

//        OkHttpClient client = new OkHttpClient();
//
//        RequestBody body = RequestBody.create(TEXTO, jsonObjectTimer.toString());
//        String url = JSON_URL_REGISTAR_TEMPO;
//        Request request = new Request.Builder()
//                .url(url)
//                .header("Content-Type", "text/plain")
//                .post(body)
//                .build();
//
//        Callback callbackResposta = new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                MrApp.esconderAlertToWait(activityListaOS);
//                e.printStackTrace();
//                final IOException finalE = e;
//                activityListaOS.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Funcoes.alerta(activityListaOS, "Erro", "ONCALL FAILURE: A gravação de tempo no webservice não foi efectuada. O erro é:\n" + finalE.getMessage());
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                // String loginResponseString = response.body().string();
//                MrApp.esconderAlertToWait(activityListaOS);
//                try {
//                    final JSONObject responseObj = new JSONObject(response.body().string());
//                    final String mensagem = responseObj.getString(JSON_MENSAGEM);
//                    final boolean isOK = responseObj.getBoolean(JSON_OK);
//                    Log.i(TAG, response.code() + ": responseObj: " + responseObj);
//                    if (isOK) {
//                        activityListaOS.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(activity, "Gravado com sucesso, aguarde um momento para actualizar informação", Toast.LENGTH_LONG).show();
//                                activityListaOS.getBancadaTrabalho().actualizarDados();
//                            }
//                        });
//                    } else {
//                        activityListaOS.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Funcoes.alerta(activity, "ERRO", "Não foi possivel gravar, tente mais tarde! Mensagem:\n" + mensagem);
//                            }
//                        });
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                // Log.i(TAG, "loginResponseString: " + loginResponseString);
//            }
//        };
//
//        client.newCall(request).enqueue(callbackResposta);


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
////                            ActivityListaOS.ColocarOSemTrabalhoAposWebService((ActivityListaOS) context, jsonObject);
//////                            ActivityListaOS.retirarRegistoDaLista((ActivityListaOS) context, jsonObject);
////                        } else {
////                            ActivityListaOS.pararCronometro((ActivityListaOS) context);
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
//                            boolean resultado = response.getBoolean(JSON_OK);
//                            String mensagem = response.getString(JSON_MENSAGEM);
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
