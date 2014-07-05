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
	int{L} checkLow = 0;
    	this.parent = parent;
    	KeyPair kp = null;
    	try {
    		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    		kpg.initialize(2048); // 2048 is the keysize
    		kp = kpg.generateKeyPair();
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.exit(-1);
    	}
    	
    	privkey = (PRIVKEY(L /parent.clearHistory T){L}) kp.getPrivate();
    	pubkey = (PUBKEY(L /parent.clearHistory T){L}) kp.getPublic();
    	
    	buffer = new ArrayList();
    	historyLength = 0;
    }

    public int historyLength() {
    	return this.historyLength;
    }
    
    public ArrayList getScreen(int num) {
	int{L} checkLow = 0;
        try {
	    if (num < 0 || num >= historyLength) {
    		return null;
	    }
	    
	    if (num == historyLength - 1) {
    		return new ArrayList(buffer);
	    }
	    
	    Path file = Paths.get("irclog." + num, new String[0]);
	    byte[] bytes = Files.readAllBytes(file);
	    return CryptoLibrary.decryptStrings(privkey, bytes);
	    } catch (Throwable e) {
	    try {
		e.printStackTrace();
		System.exit(-1);
	    } catch (Throwable t) {}
	}
	return null;
    }

    public void writeLine(String line) {
	int{L} checkLow = 0;
	try {
	    int{L} bufferSize = 0;
	    try {
		buffer.add(line);
		bufferSize = ({L}) buffer.size();
	    } catch (Throwable e) {}
	    boolean{L} writeOut = bufferSize >= 20;
	    if (writeOut) {
		byte[] bytes = CryptoLibrary.encryptStrings(pubkey, buffer);
		/* 
		   // INJECTABLE FAULT: attempt to write high data to disk
		   byte[] bytes = new byte[100];
		   bytes[0] = ({H}) ((byte)42);
		*/
		Path file = Paths.get("irclog." + historyLength, new String[0]);
		Files.write(file, bytes, defaultOpenOptions);
		buffer.clear();
		historyLength++;
	    }
	} catch (Throwable e) {}
    }
}
