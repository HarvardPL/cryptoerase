import java.util.ArrayList;

import accrue.cryptoerase.runtime.Condition;

class C {
    static final Condition c = new Condition();
    void m() {
	ArrayList foo = new ArrayList();
	Object{L /c H} o = new Object();
	foo.add(o);
	Object{L} out = foo.get(0);
    }
    public static void main(String[] args) {
	new C().m();
    }
}
