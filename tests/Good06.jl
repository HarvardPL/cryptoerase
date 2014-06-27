import accrue.cryptoerase.runtime.Condition;

class C {
    int xTESTOUTPUT = 7;
    int wTESTOUTPUT = 7;
    void m() {
      Condition c = new Condition();
      int y = ({L /c H}) 42;
      int z = 0;
      if (y > 0) {
	  //c = true;
	  z = 4; // level of z should be {L c/ H}, since the PC should hasn't been raised since c hasn't been set.
      }
      this.xTESTOUTPUT = y; // At this point, the level of y should be {L /c H}, since c hasn't been set.
      this.wTESTOUTPUT = z;
    }
    public static void main(String[] args) {
	new C().m();
    }
}
