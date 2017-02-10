package pt.bamer.bamermachina.firebasefcm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import pt.bamer.bamermachina.ActivityListaOS;
import pt.bamer.bamermachina.Entrada;
import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.R;
import pt.bamer.bamermachina.pojos.ObjSMS;
import pt.bamer.bamermachina.utils.Funcoes;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

/**
 * Criado por miguel.silva on 06-02-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i(TAG, "From: " + remoteMessage.getFrom());
        Log.v(TAG, "getCollapseKey: " + remoteMessage.getCollapseKey());
        Log.i(TAG, "getMessageId: " + remoteMessage.getMessageId());
        Log.v(TAG, "getMessageType: " + remoteMessage.getMessageType());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Payload: " + remoteMessage.getData());
            Map<String, String> map = remoteMessage.getData();
            String de = map.get("de");
            String dispositivo = map.get("dispositivo");
            String para = map.get("para");
            String titulo = map.get("titulo");
            String mensagem = map.get("mensagem");
            Log.i(TAG, "PAYLOAD DE: " + de);
            Log.i(TAG, "PAYLOAD PARA: " + para);

            ObjSMS objSMS = new ObjSMS(remoteMessage.getMessageId(), titulo, mensagem, "", 0, System.currentTimeMillis(), de, para, dispositivo);
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Funcoes.gravarSMSFireDataBase(objSMS);
            if (!isAppOpened()) {
                sendNotification(titulo, mensagem);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Mensagem: " + remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            RemoteMessage.Notification notificacao = remoteMessage.getNotification();
            String titulo = notificacao.getTitle() == null ? "(vazio)" : notificacao.getTitle();
            String corpo = notificacao.getBody() == null ? "(vazio)" : notificacao.getBody();


            //Define Notification Manager
//            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//
////Define sound URI
//            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.ic_stat_contact_mail)
//                    .setContentTitle(titulo)
//                    .setContentText(corpo)
//                    .setAutoCancel(true)
//                    .setSound(soundUri); //This sets the sound to play
//
////Display notification
//            mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
//            notificationManager.notify(0, mBuilder.build());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private boolean isAppOpened() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String titulo, String messageBody) {
        Intent intent;
        if (MrApp.getMaquina() != null) {
            intent = new Intent(this, ActivityListaOS.class);
        } else {
            intent = new Intent(this, Entrada.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_contact_mail)
                .setContentTitle(titulo)
                .setContentText(messageBody)
                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
