package pt.bamer.bamermachina.pojos;

@SuppressWarnings("unused")
public class OSBI {
    public String design;
    public String dim;
    public String familia;
    public String mk;
    public int qtt;
    public String ref;
    public String tipo;
    public String bostamp;
    public String bistamp;
    public String numlinha = "";

    public OSBI() {

    }

    public OSBI(String ref, String design, int qtt, String dim, String mk, String bostamp, String bistamp, String familia, String tipo, String numlinha) {
        this.ref = ref;
        this.design = design;
        this.qtt = qtt;
        this.dim = dim;
        this.mk = mk;
        this.bostamp = bostamp;
        this.bistamp = bistamp;
        this.familia = familia;
        this.tipo = tipo;
        this.numlinha = numlinha;
    }

    @Override
    public String toString() {
        return "design: " + design
                + ", dim: " + dim
                + ", familia: " + familia
                + ", mk: " + mk
                + ", qtt: " + qtt
                + ", ref: " + ref
                + ", tipo: " + tipo
                + ", bostamp: " + bostamp
                + ", bistamp: " + bistamp
                + ", numlinha: " + numlinha
                ;
    }
}
