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

    public static ArrayList compressStrings(ArrayList strings) {
	String result = "";
	for (int i = 0; i < strings.size(); i++) {
	    if ((String)strings.get(i) == "foo") {
		result += "a";
	    } else {
		result += "b";
	    }
	}
	ArrayList resultList = new ArrayList();
	resultList.add(result);
	return resultList;
    }

    public static ArrayList decompressStrings(ArrayList compressed) {
	ArrayList strings = new ArrayList();
	strings.add(compressed.get(0));
	return strings;
    }
}
