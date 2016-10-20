package pt.bamer.bamermachina.utils;


public class Singleton {
    private static Singleton instancia;

    public static Singleton getInstancia() {
        if (instancia == null) {
            instancia = new Singleton();
        }
        return instancia;
    }
}
