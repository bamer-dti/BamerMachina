package pt.bamer.bamermachina.pojos;

import org.json.JSONException;
import org.json.JSONObject;

import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.utils.Constantes;

public class JSONObjectTimer extends JSONObject {
    private String bostamp;

    public JSONObjectTimer(String bostamp, String bistamp, String estado, int posicao) throws JSONException {
        long unixTime = System.currentTimeMillis() / 1000L;

        put(Constantes.FIELD_BOSTAMP, bostamp);
        put(Constantes.FIELD_BISTAMP, bistamp);
        put(Constantes.FIELD_MAQUINA, MrApp.getMaquina());
        put(Constantes.FIELD_OPERADOR, MrApp.getOperadorCodigo());
        put(Constantes.FIELD_ESTADO, estado);
        put(Constantes.FIELD_POSICAO, "" + posicao);
        put(Constantes.FIELD_TIPO, "ostimer");
        put(Constantes.FIELD_UNIXTIME, "" + unixTime);
        put(Constantes.FIELD_LASTTIME, "" + unixTime);
        put(Constantes.FIELD_SECCAO, MrApp.getSeccao());

        this.bostamp = bostamp;
    }

    public String getBostamp() {
        return bostamp;
    }

//    public int getPosicaoNoArray() {
//        return posicaoNoArray;
//    }
}
