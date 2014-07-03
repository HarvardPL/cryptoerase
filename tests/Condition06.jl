import accrue.cryptoerase.runtime.Condition;

class C {
    static void foo(Object c) {}

    public static void main(String[] args) {
	final Condition c = new Condition();
	foo(c);
    }
}
