package java.io;

public class ByteArrayInputStream extends InputStream {
    private byte[] bytes;

    public ByteArrayInputStream(byte[] input) {
	bytes = input;
    }

    public int read() {
	return bytes[0];
    }
}
