class C {
  int{L} x = 7;
  void m() {
      int y = ({H})this.x;
      this.x = y;
  }
    public static void main(String[] args) {
	new C().m();
    }
}
