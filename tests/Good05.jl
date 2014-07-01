import accrue.cryptoerase.runtime.Condition;

class C {
    int xTESTOUTPUT = 7;
    int wTESTOUTPUT = 7;
    void m() {
      final Condition c = new Condition();
      int y = ({L /c H}) 42;
      int z = 0;
      if (y > 0) {
	  c.set();
	  z = 4; // level of z should be H, since the PC should have been raised since c has been set.
      }
      this.xTESTOUTPUT = y; // At this point, the level of y should be H, since c has been set.
      this.wTESTOUTPUT = z;
    }
    public static void main(String[] args) {
	new C().m();
    }
}
