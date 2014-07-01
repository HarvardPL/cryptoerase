import accrue.cryptoerase.runtime.Condition;

class C {
    static final Condition c = new Condition();
    
    public void bar() {
	c.set();
    }

    public void foo() {
	int{L /c T} x = 42;
	bar();
	int z = x;
    }
    
    public static void main(String[] args) {
	C o = new C();
	o.foo();
    }
}
