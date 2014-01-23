class C {
  int x = 7;
  void m() {
      condition c = false;
      int y = ({L /c H}) 42;
      c = true;
      
      this.x = y; // At this point, the level of y should be H, since c has been set.
  }
    public static void main(String[] args) {
	new C().m();
    }
}
