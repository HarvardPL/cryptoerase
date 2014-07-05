package accrue.cryptoerase.runtime;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoLibrary {
	public static byte[] encryptStrings(PublicKey key, ArrayList<String> plainText)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		try {
		    for (int i = 0; i < plainText.size(); i++) {
		    	ps.println(plainText.get(i));
		    }
		} catch (Throwable e) {}
		ps.flush();
		return encrypt(key, baos.toByteArray());
	}
	
    public static byte[] encrypt(PublicKey key, byte[] plaintext)
            throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecretKey symkey = keyGen.generateKey();
        Cipher sc = Cipher.getInstance("AES");
        sc.init(Cipher.ENCRYPT_MODE, symkey);
        byte[] ciphertext = sc.doFinal(plaintext);

        byte[] keyBytes = symkey.getEncoded();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encKey = cipher.doFinal(keyBytes);
        byte[] encoded = new byte[encKey.length + ciphertext.length];
        for (int i = 0; i < encKey.length; i++) {
            encoded[i] = encKey[i];
        }
        for (int i = 0; i < ciphertext.length; i++) {
            encoded[i + encKey.length] = ciphertext[i];
        }
        return encoded;
    }
    
    public static ArrayList<String> decryptStrings(PrivateKey key, byte[] ciphertext) throws Exception {
    	byte[] decrypt = decrypt(key, ciphertext);
    	BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(decrypt)));
        ArrayList<String> plaintext = new ArrayList<String>();
        String line = null;
        while ((line = br.readLine()) != null) {
        	plaintext.add(line);
        }
        return plaintext;
    }

    public static byte[] decrypt(PrivateKey key, byte[] ciphertext)
            throws Exception {
        byte[] encKey = new byte[256];
        byte[] ctext = new byte[ciphertext.length - 256];
        for (int i = 0; i < ciphertext.length; i++) {
            if (i < encKey.length) {
                encKey[i] = ciphertext[i];
            }
            else {
                ctext[i - encKey.length] = ciphertext[i];
            }
        }

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] symkeybytes = cipher.doFinal(encKey);

        SecretKey symkey = new SecretKeySpec(symkeybytes, "AES");
        Cipher sc = Cipher.getInstance("AES");
        sc.init(Cipher.DECRYPT_MODE, symkey);
        return sc.doFinal(ctext);
    }
}
