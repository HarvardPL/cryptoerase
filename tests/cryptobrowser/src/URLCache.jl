import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;

public class URLCache {
    private Path temp;
    private static final FileAttribute[] defaultFileAttributes =
            { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")) };
    private static final OpenOption[] defaultOpenOptions =
            { StandardOpenOption.TRUNCATE_EXISTING };
    private final PrivateKey PRIVKEY(H){L} privkey;
    private final PublicKey PUBKEY(H){L} pubkey;

    public URLCache() throws Exception {
	KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	kpg.initialize(512); // 512 is the keysize
	KeyPair kp = kpg.generateKeyPair();
	privkey = (PRIVKEY(H){L})kp.getPrivate();
	pubkey = (PUBKEY(H){L})kp.getPublic();

        try {
            temp =
                    Files.createTempDirectory("browsercache",
                                              defaultFileAttributes);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream get(URL url) {
        int code = url.toString().hashCode();
        Path file = temp.resolve(Integer.toHexString(code));

        try {
            byte[] bytes = Files.readAllBytes(file);
	    byte[] decrypt = null;
	    try {
		decrypt = CryptoLibrary.decrypt(privkey, bytes);
	    } catch (Exception e) {}
	    return new ByteArrayInputStream(decrypt);
        }
        catch (IOException ioe) {
            return null;
        }
    }

    public void put(URL url, InputStream value) throws IOException {
	byte[]{L} bytes = null;
	try {
	    bytes = CryptoLibrary.encrypt(pubkey, inputStreamToBytes(value));
	} catch (Exception e) {}
	
	int code = url.toString().hashCode();
	Path file = temp.resolve(Integer.toHexString(code));
	Files.createFile(file, defaultFileAttributes);
	Files.write(file, bytes, defaultOpenOptions);
    }

    private static byte[] inputStreamToBytes(InputStream is) throws IOException {
	ByteArrayOutputStream{L} byteArrayOutputStream =
				     new ByteArrayOutputStream();

	try {
	    int read = is.read();
	    while (read != -1) {
		byteArrayOutputStream.write(read);
		read = is.read();
	    }
	} catch (Exception e) {}

	return byteArrayOutputStream.toByteArray();
    }
}
