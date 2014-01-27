/**
 * This class models Crypto operations.
 */
class CryptoLibrary {
    // These fields are used to make sure that
    // various info is at the right level. We 
    // could get rid of these by adding additional
    // syntax to indicate constraints on data.
    static private int{PRIVKEY} dummyPRIVKEY;
    static private int{PUBKEY} dummyPUBKEY;
    static private int{H} dummyH;

    public static int encrypt(int publicKey, int plaintext) {
	// make sure things are the right level
	this.dummyPUBKEY = publicKey; // publicKey & PC <= PUBKEY
	this.dummyH = plaintext; // plaintext & PC <= H

 	// here is where the magic encrypting happens. We cast
	// the plaintext to L, since it is now a ciphertext.
	return  ({L}) plaintext;
    } 

    public static int decrypt(int ciphertext, int privateKey) {
	// XXX The following constraint isn't quite right. What if the private key as an erasure policy on it, like PRIVKEY /c TOP?
	this.dummyPRIVKEY = privateKey; // privateKey & PC<= PRIVKEY

	// make sure that this program point is tainted by stuff that can be at level H
	this.dummyH = 0;

	// cast return result to H
	return ({H})ciphertext;
    }
}
