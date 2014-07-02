import accrue.cryptoerase.runtime.Condition;

class C {
    public static final Condition c = new Condition();
    int{L /c H} x = 0;

    public static void main(String[] args) {
	H.c.x = 42;
	H.y = H.c.x;
    }
}

abstract class H {
    public static final C c = new C();
    public static int{L /c.c H} y; 
}
