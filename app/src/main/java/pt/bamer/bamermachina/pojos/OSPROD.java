package pt.bamer.bamermachina.pojos;

public class OSPROD {
    public String bostamp;
    public String bistamp;
    public int qtt;
    public String ref;
    public String design;
    public String dim;
    public String mk;

    @Override
    public String toString() {
        return "bostamp: " + bostamp + ", bistamp: " + bistamp + ", qtt: " + qtt
                + ", ref: " + ref + ", design: " + design + ", dim: " + dim + ", mk: " + mk;

    }
    
}
