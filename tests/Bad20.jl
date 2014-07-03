import accrue.cryptoerase.runtime.Condition;

class C {
  void m() {
      int{H} x = 37;
      if (x > 0) {
	  final Condition{L} c = new Condition();
	  int{L /c H} y = 32;
	  c.set();
      }
  }

  public static void main(String[] args) {
    new C().m();
  }
}
