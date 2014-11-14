import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import accrue.cryptoerase.runtime.CryptoLibrary;

public class Log {
    private final SimpleIRCClient parent;
    private static final OpenOption[] defaultOpenOptions = { StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE };
    private PrivateKey PRIVKEY(L /parent.clearHistory T){L} privkey = null;
    private PublicKey PUBKEY(L /parent.clearHistory T){L} pubkey = null;
    
    private ArrayList{L /parent.clearHistory T} buffer;
    private int{L} historyLength;

    public Log(SimpleIRCClient parent) {
    	this.parent = parent;
    	KeyPair kp = null;
    	try {
    		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    		kpg.initialize(2048); // 2048 is the keysize
    		kp = kpg.generateKeyPair();
    	} catch (Exception e) {}
    	
    	privkey = (PRIVKEY(L /parent.clearHistory T){L}) kp.getPrivate();
    	pubkey = (PUBKEY(L /parent.clearHistory T){L}) kp.getPublic();
    	
    	buffer = new ArrayList();
    	historyLength = 0;
    }

    public int historyLength() {
    	return (this.historyLength < 1) ? 0 : this.historyLength - 1;
    }
    
    public ArrayList getScreen(int num) {
	int{L} getScreenLevel = 0;
	try {
	    if (num < 0 || num >= historyLength - 1) {
    		return null;
	    }
	    
	    int{L} beforeLevel = 0;
	    Path file = Paths.get("irclog." + num, new String[0]);
	    int{L} hereLevel = 0;
	    byte[] bytes = Files.readAllBytes(file);
	    ArrayList compressed = CryptoLibrary.decryptStrings(privkey, bytes);
	    return CryptoLibrary.decompressStrings(compressed);
	} catch (Throwable e) {}
	return null;
    }

    public void writeLine(String line) {
	int{L} writeLinePC = 0;
	try {
	    int bufferSize = 0;
	    try {
		buffer.add(line);
		bufferSize = buffer.size();
	    } catch (Throwable e) {}
	    // Declassify whether we've received more than 20 messages since the last write
	    boolean{L} writeOut = ({L}) bufferSize >= 20;
	    if (writeOut) {
		ArrayList compressed = null;
		try { compressed = CryptoLibrary.compressStrings(buffer); } catch (Throwable e) {}
		byte[] bytes = CryptoLibrary.encryptStrings(pubkey, compressed);
		/* 
		   // INJECTABLE FAULT: attempt to write high data to disk
		   byte[] bytes = new byte[100];
		   bytes[0] = ({H}) ((byte)42);
		*/
		Path file = Paths.get("irclog." + historyLength, new String[0]);
		int{L} stillLowP = 0;
		Files.write(file, bytes, defaultOpenOptions);
		buffer.clear();
		historyLength++;
	    }
	} catch (Throwable e) {}
    }
}
