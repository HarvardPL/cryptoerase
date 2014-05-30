/**
 * This class models Crypto operations.
 */
class CryptoLibrary {
    //static int cl1TESTOUTPUT = 7;

    // All the interesting constraints are coded up in CEDataFlow
    public static int encrypt(int publicKey, int plaintext) {
 	// here is where the magic encrypting happens. 
	// the result has no relation to the plaintext.
	return  ({L})plaintext+1;
    } 

    public static int decrypt(int privateKey, int ciphertext) {
	return ciphertext-1;
    }
}
