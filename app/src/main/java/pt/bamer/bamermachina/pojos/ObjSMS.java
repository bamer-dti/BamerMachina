package pt.bamer.bamermachina.pojos;

/**
 * Criado por miguel.silva on 06-02-2017.
 */
@SuppressWarnings("unused")
public class ObjSMS {
    private String dispositivo;
    private String de;
    private String para;
    private String titulo;
    private String mensagem;
    private String id;
    private Long tempostamp;
    private Long lidastamp;
    private boolean lida;
    private String lidaQuem;

    public ObjSMS() {

    }

    public ObjSMS(String colid, String titulo, String mensagem, String bostamp, int lida, long tempoStamp, String de, String para, String dispositivo) {
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.id = colid;
        this.tempostamp = tempoStamp;
        this.lidastamp = 0L;
        this.lida = lida == 1;
        this.lidaQuem = "";
        this.de = de;
        this.para = para;
        this.dispositivo = dispositivo;
    }

    @Override
    public String toString() {
        return "Titulo: " + titulo
                + "; Mensagem: " + mensagem
                + "; id: " + id
                + "; tempoStamp: " + tempostamp
                + ";lidaStamp: " + lidastamp
                + "; lida: " + lida
                + "; lidaQuem: " + lidaQuem
                + "; de: " + de
                + "; para: " + para
                + "; dispositivo: " + dispositivo
                ;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getId() {
        return id;
    }

    public Long getTempostamp() {
        return tempostamp;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public Long getLidastamp() {
        return lidastamp;
    }

    public void setLidastamp(Long lidastamp) {
        this.lidastamp = lidastamp;
    }

    public String getLidaQuem() {
        return lidaQuem;
    }

    public void setLidaQuem(String lidaQuem) {
        this.lidaQuem = lidaQuem;
    }

    public String getDe() {
        return de;
    }

    public String getPara() {
        return para;
    }

    public String getDispositivo() {
        return dispositivo;
    }
}
