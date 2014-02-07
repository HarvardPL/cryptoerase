/**
 * This class models a cloud storage API.
 */
class Main {

    static int xTESTOUTPUT = 7;
    static int yTESTOUTPUT = 7;
    static int zTESTOUTPUT = 7;

    public static void main(String[] args) {

	// the following is dangerous data that should be erased sometime.
	condition c = false;

	int dangerous = ({L /c H})args.length;
	
	// get a key pair
	int pub = (PUBKEY(L /c H){L})42;
	int priv = (PRIVKEY(L /c H){L /c H})42;

	// let's encrypt it, and put the result in the cloud.
	
	Cloud cld = new Cloud();
	xTESTOUTPUT = CryptoLibrary.encrypt(pub, dangerous); // Should be L

	cld.put(CryptoLibrary.encrypt(pub, dangerous));

	// set the condition.
	c = true;
	
	yTESTOUTPUT = cld.get(); // should be L, since the ciphertext is still low security.

	// at this point, priv should no longer be accessible at PRIVKEY.
	zTESTOUTPUT = CryptoLibrary.decrypt(priv, cld.get()); // the level should be H, since it can no longer be at L /c H, since c was set.
    }
}
