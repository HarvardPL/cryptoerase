class C {
    C() { 
    }
    int xTESTOUTPUT = 7;
    D d = new D();
    void m() {
	int y = ({L /this.d.c H}) 42;

      d.foo();

      this.xTESTOUTPUT = y; // At this point, the level of y should be H, since this.d.c has been set.
      
    }
    
    public static void main(String[] args) {
	new C().m();
    }
}

class D {
    condition{L} c = false;
    void foo() {
	this.c = true;
    }
}