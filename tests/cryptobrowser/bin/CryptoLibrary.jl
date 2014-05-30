/**
 * This class models Crypto operations.
 */
class CryptoLibrary {
    public static byte[] encrypt(int publicKey, byte[] plaintext) {
	byte[] newBytes = new byte[plaintext.length];
	for (int i = 0; i < newBytes.length; i++) {
	    newBytes[i] = ({L})plaintext[i];
	}
	return newBytes;
    } 

    public static byte[] decrypt(int privateKey, byte[] ciphertext) {
	byte[] newBytes = new byte[ciphertext.length];
	for (int i = 0; i < newBytes.length; i++) {
	    newBytes[i] = ciphertext[i];
	}
	return newBytes;
    }
}
