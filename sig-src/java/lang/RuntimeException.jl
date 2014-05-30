package java.lang;

public class RuntimeException extends Exception {
    public RuntimeException() { this.s = null; }
    public RuntimeException(String s) { this.s = s; }
    private final String s;
}
