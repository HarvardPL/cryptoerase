package java.io;

public class ByteArrayOutputStream extends OutputStream {
    private byte[] bytes;

    public ByteArrayOutputStream() {
	bytes = new byte[1];
    }

    public synchronized byte[] toByteArray() {
	return bytes;
    }

    public synchronized void write(int i) {
	bytes[0] = (byte)i;
    }
}
