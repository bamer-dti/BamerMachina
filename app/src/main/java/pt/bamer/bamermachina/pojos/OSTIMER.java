package pt.bamer.bamermachina.pojos;

public class OSTIMER {
    public String bostamp;
    public String bistamp;
    public long lasttime;
    public long unixtime;
    public int posicao;
    public String maquina;
    public String operador;
    public String seccao;
    public String estado;
    public String fref;
    public int obrano;

    public OSTIMER() {

    }

    @Override
    public String toString() {
        return "bostamp: " + bostamp
                + ", bistamp: " + bistamp
                + ", lasttime: " + lasttime
                + ", unixtime: " + unixtime
                + ", posicao: " + posicao
                + ", maquina: " + maquina
                + ", operador: " + operador
                + ", seccao: " + seccao
                + ", estado: " + estado
                + ", obrano: " + obrano
                + ", fref: " + fref
                ;

    }
}
