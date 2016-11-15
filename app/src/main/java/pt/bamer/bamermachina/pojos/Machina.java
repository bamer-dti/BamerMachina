package pt.bamer.bamermachina.pojos;

public class Machina {
    public String seccao;
    public String ref;
    public String funcao;
    public String nome;

    public Machina() {
    }

    public Machina(String seccao, String ref, String funcao, String nome) {
        this.seccao = seccao;
        this.ref = ref;
        this.funcao = funcao;
        this.nome = nome;
    }
}
