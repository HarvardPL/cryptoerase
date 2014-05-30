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

public class URLCache {
    private Path temp;
    private static final FileAttribute[] defaultFileAttributes =
            { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")) };
    private static final OpenOption[] defaultOpenOptions =
            { StandardOpenOption.TRUNCATE_EXISTING };
    private final int PRIVKEY(H){L} privkey;
    private final int PUBKEY(H){L} pubkey;

    public URLCache() {
	privkey = (PRIVKEY(H){L})42;
	pubkey = (PUBKEY(H){L})42;

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
            return new ByteArrayInputStream(CryptoLibrary.decrypt(privkey, bytes));
        }
        catch (IOException ioe) {
            return null;
        }
    }

    public void put(URL url, InputStream value) {
	byte[] bytes = CryptoLibrary.encrypt(pubkey, inputStreamToBytes(value));

	int code = url.toString().hashCode();
	Path file = temp.resolve(Integer.toHexString(code));
	Files.createFile(file, defaultFileAttributes);
	Files.write(file, bytes, defaultOpenOptions);
    }

    private static byte[] inputStreamToBytes(InputStream is) {
	ByteArrayOutputStream{L} byteArrayOutputStream =
				     new ByteArrayOutputStream();
	try {
	    int read;
	    read = is.read();
	    while (read != -1) {
		//	byteArrayOutputStream.write(read);
		try {
		read = is.read();
		} catch (Exception e) {}
	    }
	} catch (Exception e) {}

	byte[] retVal = byteArrayOutputStream.toByteArray();
	return retVal;
    }
}
