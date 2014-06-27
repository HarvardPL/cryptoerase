import accrue.cryptoerase.runtime.Condition;

class C {
    int[] foo = new int[1];
    
    void m() {
	Condition c = new Condition();
	int y = ({L /c T}) 42;
	foo[0] = y;
    }

    public static void main(String[] args) {
	new C().m();
    }
}
