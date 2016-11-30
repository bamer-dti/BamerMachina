package pt.bamer.bamermachina.pojos;

public class OSBO {
    public int cor;
    public String dtcortef;
    public String dtcliente;
    public String dtembala;
    public String dtexpedi;
    public String dttransf;
    public String estado;
    public String fref;
    public String nmfref;
    public int obrano;
    public String obs;
    public int ordem;
    public String seccao;
    public String bostamp;
    public int pecas;

    public OSBO(int cor, String dtcortef, String dtcliente, String dtembala, String dtexpedi, String dttransf
            , String estado, String fref, String nmfref, int obrano, String obs, int ordem, String seccao, String bostamp, int pecas) {
        this.cor = cor;
        this.dtcortef = dtcortef;
        this.dtcliente = dtcliente;
        this.dtembala = dtembala;
        this.dtexpedi = dtexpedi;
        this.dttransf = dttransf;
        this.estado = estado;
        this.fref = fref;
        this.nmfref = nmfref;
        this.obrano = obrano;
        this.obs = obs;
        this.ordem = ordem;
        this.seccao = seccao;
        this.bostamp = bostamp;
        this.pecas = pecas;
    }

    public OSBO() {

    }
}
