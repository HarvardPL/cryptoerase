import accrue.cryptoerase.runtime.Condition;

class Foo {
    public Foo() {}
}

class C {
  void m() {
      int{H} x = 42;
      if (x > 0) {
	  Foo{L} f = new Foo();
      }
  }

  public static void main(String[] args) {
    new C().m();
  }
}
