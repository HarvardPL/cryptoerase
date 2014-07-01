import accrue.cryptoerase.runtime.Condition;

class C {
    static final Condition c = new Condition();
    int{L /c H} x = 0;
}

class D {
    static final Condition c = new Condition();
    int{L /c H} x = 0;

    public static void main(String[] args) {
	C o1 = new C();
	D o2 = new D();
	o1.x = 42;
	o2.x = o1.x;
    }
}
