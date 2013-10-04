package cryptflow;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

// This class is providing a wrapper around the encrypt and decrypt functions because Java uses two functions, 
// cipher.init and cipher.doFinal for them with different flags and that is confusing.
// This class works for encryption and decryption for any public key cryptosystem NOT just RSA 
public class AnnaCrypto {
	
	// This class does the encryption. It takes in a cipher object so that the function calling it can specify the algorithm. 
	// Also, it takes in only the public key and not the entire key pair. 
	public static byte[] cipherEncrypt(String original, Cipher cipher, PublicKey key) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		    byte[] cipherText = cipher.doFinal(original.getBytes("UTF8"));
		    return cipherText; 
		}
		catch(Exception e){
	    	System.out.println(e.toString()); 
		}
		return null; 
	}

	// This class does the decryption. It takes in a cipher object so that the function calling it can specify the algorithm.
	// Also, it takes in only the private key and not the entire key pair. 
	public static String cipherDecrypt(byte[] encrypted, Cipher cipher, PrivateKey key) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
		    String plainText = new String(cipher.doFinal(encrypted));
		    return plainText;
		}
		catch(Exception e){
	    	System.out.println(e.toString()); 
		}
		return null; 
	}

	
}
