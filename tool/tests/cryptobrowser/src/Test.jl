import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;

public class Test {
    public static void main(String args[]) throws Exception {
	KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	kpg.initialize(512); // 512 is the keysize
	KeyPair kp = kpg.generateKeyPair();

	PrivateKey PRIVKEY(H){L} privkey = (PRIVKEY(H){L})kp.getPrivate();
	PublicKey PUBKEY(H){L} pubkey = (PUBKEY(H){L})kp.getPublic();

	byte[]{L} bytes = new byte[10];
	bytes[0] = ({H})((byte)42);
	try {
	    byte[] out = CryptoLibrary.encrypt(pubkey, bytes);
	} catch (Exception e) {}
    }
}
