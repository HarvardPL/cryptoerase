class C {
  int{L} x = 7;
  void m() {
      int{H} y = 42;
      this.x = ({L}) y;
  }
    public static void main(String[] args) {
	new C().m();
    }
}
