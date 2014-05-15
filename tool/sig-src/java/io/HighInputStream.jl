package java.io;

import java.io.InputStream;

public class HighInputStream extends InputStream {
    public HighInputStream() {}
    public int read() { return ({H})42; }
}
