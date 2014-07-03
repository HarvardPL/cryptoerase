import accrue.cryptoerase.runtime.Condition;

class C {
  int x = 7;
  void m() {
      final Condition{H} c = new Condition();
      int y = ({L /c H}) 42;
      c.set();
      
      this.x = y; // At this point, the level of y should be H, since c has been set.
  }
    public static void main(String[] args) {
	new C().m();
    }
}
