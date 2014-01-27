/**
 * This class models a cloud storage API.
 */
class Main {
    public static void main(String[] args) {

	// the following is dangerous data that should be erased sometime.
	condition c = false;

	int dangerous = ({L /c H})args.length;
	
	// get a key pair
	int pub = ({PUBKEY})42;
	int priv = ({PRIVKEY /c ERASEDPRIVKEY})42;

	// let's encrypt it, and put the result in the cloud.
	
	Cloud cld = new Cloud();
	cld.put(CryptoLibrary.encrypt(pub, dangerous));

	// set the condition.
	c = true;

	// at this point, priv should no longer be accessible at PRIVKEY.
	// CryptLibrary.decrypt(cld.get(), priv); // should fail
    }
}
