import accrue.cryptoerase.runtime.Condition;

class C {
    int{L} x = 7;
    void m() {
	final Condition c = new Condition();
	int y = ({L /c H}) 42;
	int z = 0;
	if (y > 0) {
	    c.set();
	    z = 4; // level of z should be H, since the PC should have been raised since c has been set.
	}
	this.x = z; // We should reject this assignment since c may have been set
    }

    public static void main(String[] args) {
	new C().m();
    }
}
