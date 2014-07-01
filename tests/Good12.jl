import accrue.cryptoerase.runtime.Condition;

class H {
    public C c;
    public H(C c) {
	this.c = c;
    }
}

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
	C c = new C();
	H h = new H(c);
	c.put(42);
	c.put(h.c.get());
    }
}
