package java.nio.file;

import java.io.IOException;

public final class Files {
    private static byte{L} sink;

    public static Path createTempDirectory(String path, Object[] attrs) throws IOException {
	if (path == "foo") {
	    return null;
	} else {
	    return null;
	}
    }

    public static Path createFile(Path path, Object[] attrs) throws IOException {
	return path;
    }

    public static Path write(Path path, byte[] bytes, Object[] options) throws IOException {
	for (int i = 0; i < bytes.length; i++) {
	    sink = bytes[i];
	}
	return path;
    }

    public static byte[] readAllBytes(Path path) throws IOException {
	byte[] bytes = new byte[1];
	bytes[0] = sink;
	return bytes;
    }
}
