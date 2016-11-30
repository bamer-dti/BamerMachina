package pt.bamer.bamermachina.pojos;

public class Machina {
    public String seccao;
    public String ref;
    public String funcao;
    public String nome;
    public int ordem;

    public Machina() {
    }

    public Machina(String seccao, String ref, String funcao, String nome, int ordem) {
        this.seccao = seccao;
        this.ref = ref;
        this.funcao = funcao;
        this.nome = nome;
        this.ordem = ordem;
    }
}
