import accrue.cryptoerase.runtime.Condition;

class C {
    static final Condition c = new Condition();
    int{L /c H} x = 0;

    void put(int v) {
	this.x = v;
    }

    int get() {
	return this.x;
    }

    public static void main(String[] args) {
	C o1 = new C();
	C o2 = new C();
	o1.put(42);
	o2.put(o1.get());
    }
}
