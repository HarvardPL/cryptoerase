class C {
    condition{L} c = false;

    int xTESTOUTPUT = 7;
    int yTESTOUTPUT = 7;

    void m() {
	int pubK = (PUBKEY(H){L /this.c H}) 42;
	int privK = (PRIVKEY(H){L /this.c H}) 42;

	this.xTESTOUTPUT = pubK; 
	this.yTESTOUTPUT = privK; 
      
    }
    
    public static void main(String[] args) {
	new C().m();
    }
}
