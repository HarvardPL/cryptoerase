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
	    byte[] toDecrypt = new byte[bytes.length];
	    for (int i = 0; i < bytes.length; i++) {
		toDecrypt[i] = bytes[i];
	    }
	    int{L} lowAtDecrypt = 0;
            byte[] decrypt = CryptoLibrary.decrypt(privkey, toDecrypt);
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(decrypt)));
            ArrayList screen = new ArrayList();
            String line = null;
            while ((line = br.readLine()) != null) {
            	screen.add(line);
            }
            return screen;
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		try {
		    for (int i = 0; i < buffer.size(); i++) {
			ps.println(buffer.get(i));
		    }
		} catch (Throwable e) {}
		ps.flush();
		byte[] asBytes = baos.toByteArray();
		byte[] toEncrypt = new byte[asBytes.length];
		int{L} lowHere = 0;
		for (int i = 0; i < toEncrypt.length; i++) {
		    byte{L /parent.clearHistory T} b = asBytes[i];
		    toEncrypt[i] = b;
		}
		
		byte[] bytes = CryptoLibrary.encrypt(pubkey, toEncrypt);
		
		Path file = Paths.get("irclog." + historyLength, new String[0]);
		Files.write(file, bytes, defaultOpenOptions);
		buffer.clear();
		historyLength++;
	    }
	} catch (Throwable e) {}
    }
}
