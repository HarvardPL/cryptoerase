import accrue.cryptoerase.runtime.Condition;

class C {
    int{L} x = 7;
    int z = 0;
    void m() {
	final Condition c = new Condition();
	int y = ({L /c T}) 42;
	c.set();
	z = y;
    }

    public static void main(String[] args) {
	new C().m();
    }
}
