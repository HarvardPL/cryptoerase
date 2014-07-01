import accrue.cryptoerase.runtime.Condition;

class C {
    static final Condition c = new Condition();
    int{L /c H} x = 0;
    int{L /c H} y = 0;

    public static void main(String[] args) {
	C o1 = new C();
	C o2 = new C();
	o1.x = 42;
	o2.y = o1.x;
    }
}
