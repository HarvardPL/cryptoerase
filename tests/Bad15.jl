import accrue.cryptoerase.runtime.Condition;

class C {
  static final Condition{H} c = new Condition();
  int x = 7;
  int{L /c H} y = 42;
  void m() {
    c.set();
    this.x = this.y;
  }
  
  public static void main(String[] args) {
    new C().m();
  }
}
