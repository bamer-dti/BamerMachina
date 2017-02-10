package pt.bamer.bamermachina.pojos;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Criado por miguel.silva on 06-02-2017.
 */
@SuppressWarnings({"unused"})
public class TokenFirebase {
    private boolean online;
    private String data;
    private long timestamp;
    private String token;
    private String machina = "";
    private String operador;

    public TokenFirebase() {
    }

    public TokenFirebase(String refreshedToken, boolean online) {
        this.token = refreshedToken;
        this.timestamp = System.currentTimeMillis();
        this.online = online;

        DateTime someDate = new DateTime(timestamp);
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        this.data = formatter.format(someDate.getMillis());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getMachina() {
        return machina;
    }

    public void setMachina(String machina) {
        this.machina = machina;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }
}
