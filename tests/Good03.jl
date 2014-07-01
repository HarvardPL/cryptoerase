import accrue.cryptoerase.runtime.Condition;

class C {
  int x = 7;
  void m() {
      final Condition c = new Condition();
      this.x = ({(L/c H)/c H})8; // Sets the level of x to H, since x can't contain an erasure policy
  }
    public static void main(String[] args) {
	new C().m();
    }
}
