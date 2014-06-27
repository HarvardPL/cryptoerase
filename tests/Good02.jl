import accrue.cryptoerase.runtime.Condition;

class C {
  int x = 7;
  void m() {
      int y = this.x;
      Condition c = new Condition();
      this.x = ({L/c H})8;
      this.x = ({(L/c H)/c H})8;
  }
    public static void main(String[] args) {
	new C().m();
    }
}
