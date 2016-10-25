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

    public OSTIMER() {

    }

//    public OSTIMER(String bostamp, String bistamp, long lasttime, long unixtime, int posicao, String maquina, String operador, String seccao, String estado) {
//        this.bostamp = bostamp;
//        this.bistamp = bistamp;
//        this.lasttime = lasttime;
//        this.unixtime = unixtime;
//        this.posicao = posicao;
//        this.maquina = maquina;
//        this.operador = operador;
//        this.seccao = seccao;
//        this.estado = estado;
//    }

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
                + ",estado: " + estado;
    }
}
