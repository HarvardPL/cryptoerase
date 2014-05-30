import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import javax.crypto.Cipher;

public class CryptoLibrary {
    public static byte[] encrypt(PublicKey key, byte[] plaintext) throws Exception {
	return new byte[plaintext.length];
    }
    
    public static byte[] decrypt(PrivateKey key, byte[] ciphertext) throws Exception {
	return new byte[ciphertext.length];
    }
}
