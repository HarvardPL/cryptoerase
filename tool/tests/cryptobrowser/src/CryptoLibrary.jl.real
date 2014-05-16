import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import javax.crypto.Cipher;

public class CryptoLibrary {
    public static byte[] encrypt(PublicKey key, byte[] plaintext) throws Exception {
	Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1PADDING");
	cipher.init(Cipher.ENCRYPT_MODE, key);
	return cipher.doFinal(plaintext);
    }
    
    public static byte[] decrypt(PrivateKey key, byte[] ciphertext) throws Exception {
	Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1PADDING");
	cipher.init(Cipher.DECRYPT_MODE, key);
	return cipher.doFinal(ciphertext);
    }
}
