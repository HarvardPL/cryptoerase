package java.security;

import java.security.Key;

public class Cipher {
    static int DECRYPT_MODE = 0;
    static int ENCRYPT_MODE = 1;
    static int PRIVATE_KEY = 2;
    static int PUBLIC_KEY = 3;
    static int SECRET_KEY = 4;
    static int UNWRAP_MODE = 5;
    static int WRAP_MODE = 6;

    private Cipher() {}

    public static Cipher getInstance(String s) {
	return new Cipher();
    }

    public void init(int mode, Key k) {}

    public byte[] doFinal(byte[] input) {
	return new byte[1];
    }
}
