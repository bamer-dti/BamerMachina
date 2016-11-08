package pt.bamer.bamermachina.pojos;

import org.json.JSONException;
import org.json.JSONObject;

import pt.bamer.bamermachina.utils.Constantes;

public class JSONObjectQtd extends JSONObject {
    public JSONObjectQtd(String bostamp, String dim, String mk, String ref, String design, int qttEfectuada, String numlinha) throws JSONException {
        put(Constantes.FIELD_DIM, dim);
        put(Constantes.FIELD_MK, mk);
        put(Constantes.FIELD_REF, ref);
        put(Constantes.FIELD_DESIGN, design);
        put(Constantes.FIELD_BOSTAMP, bostamp);
        put(Constantes.FIELD_QTT, qttEfectuada);
        put(Constantes.FIELD_NUMLINHA, numlinha);
    }
}
