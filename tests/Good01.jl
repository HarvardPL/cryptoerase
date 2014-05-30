class C {
  int{L} x = 7;
  void m() {
      int y = this.x;
      this.x = ({L})8;
  }
    public static void main(String[] args) {
	new C().m();
    }
}
