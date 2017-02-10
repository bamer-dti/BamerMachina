package pt.bamer.bamermachina.utils;

import android.content.Context;
import android.media.ToneGenerator;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.R;
import pt.bamer.bamermachina.pojos.ObjSMS;

@SuppressWarnings("unused")
public class Funcoes {

    private static final String TAG = Funcoes.class.getSimpleName();

    public static String localDateTimeToStrFull(LocalDateTime data) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.print(data);
    }

    public static String localDateTimeToStrFullaZeroHour(LocalDateTime data) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd 00:00:00");
        return formatter.print(data);
    }

    public static String dToC(LocalDateTime data) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        return formatter.print(data);
    }

    public static String currentTimeStringStamp() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        Calendar calendar = GregorianCalendar.getInstance();
        return dateFormatter.format(calendar.getTime());
    }

    public static LocalDateTime cToT(String datastr) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.parseLocalDateTime(datastr);
    }

    public static String dataBonita(String data) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        LocalDateTime local = cToT(data);
        return formatter.print(local);
    }

    public static String milisegundos_em_HH_MM_SS(long milisegundos) {
        PeriodFormatter fmt = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendHours()
                .appendSeparator(":")
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparator(":")
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendSeconds()
                .toFormatter();
        Period period = new Period(milisegundos);
        return fmt.print(period);
    }

    public static void alerta(Context context, String titulo, String mensagem) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        alertBuilder.setMessage(mensagem);
        alertBuilder.setTitle(titulo);
        alertBuilder.setPositiveButton("OK", null);
        alertBuilder.create();
        alertBuilder.show();
    }

    public static long adicionarSecsToUnix(long valor, int sec) {
        long retryDate = valor * 1000L;
        Timestamp original = new Timestamp(retryDate);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(original.getTime());
        cal.add(Calendar.SECOND, sec);
        Timestamp later = new Timestamp(cal.getTime().getTime());
        return later.getTime() / 1000;
    }

    public static String hojeMeiaNoite(String formato) {
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        return sdf.format(new Date());
    }

    public static void checkFirebaseOnline() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            public static final String TAG = "CHECK_FIREBASE";

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                Log.i(TAG, "FIREBAS ONLINE: " + connected);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    public static void beep(int queSom) {
        int tempoSom = 400;
        switch (queSom) {
            case Constantes.SOM_AVISO:
                tempoSom = 200;
                break;
            case Constantes.SOM_ERRO:
                tempoSom = 500;
                break;

            case Constantes.SOM_ERRO_CRITICO:
                tempoSom = 1000;
                break;

            default:
                break;
        }
        try {
            MrApp.toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, tempoSom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gravarSMSFireDataBase(ObjSMS objSMS) {
        Log.i(TAG, objSMS.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constantes.TAG_SMS).child(Constantes.TAG_NAOLIDAS).child(objSMS.getDispositivo()).child(objSMS.getId());
        ref.setValue(objSMS);
    }
}

