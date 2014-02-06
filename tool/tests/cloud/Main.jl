/**
 * This class models a cloud storage API.
 */
class Main {

    static int xTESTOUTPUT = 7;
    static int yTESTOUTPUT = 7;

    public static void main(String[] args) {

	// the following is dangerous data that should be erased sometime.
	condition c = false;

	int dangerous = ({L /c H})args.length;
	
	// get a key pair
	int pub = (PUBKEY(L /c H){L})42;
	int priv = (PRIVKEY(L /c H){H})42;

	// let's encrypt it, and put the result in the cloud.
	
	Cloud cld = new Cloud();
	cld.put(CryptoLibrary.encrypt(pub, dangerous));

	// set the condition.
	c = true;
	
	xTESTOUTPUT = cld.get();
	// at this point, priv should no longer be accessible at PRIVKEY.
	yTESTOUTPUT = CryptoLibrary.decrypt(cld.get(), priv); // should fail
    }
}
