package accrue.cryptoerase.runtime;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.ArrayList;

public final class CryptoLibrary {
    public static byte[] encryptStrings(PublicKey key, ArrayList plaintext)
	throws Exception {
	return new byte[100];
    }

    public static byte[] encrypt(PublicKey key, byte[] plaintext)
	throws Exception {
	return new byte[100];
    }

    public static ArrayList decryptStrings(PrivateKey key, byte[] ciphertext)
	throws Exception {
	return new ArrayList();
    }

    public static byte[] decrypt(PrivateKey key, byte[] ciphertext)
	throws Exception {
	return new byte[100];
    }
}
