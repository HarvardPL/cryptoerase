import accrue.cryptoerase.runtime.Condition;

class C {
    int[] foo = new int[] { m() };
    Condition c = new Condition();
    
    int m() {
	int y = ({L /c T}) 42;
	return y;
    }

    public static void main(String[] args) {
	new C().m();
    }
}
