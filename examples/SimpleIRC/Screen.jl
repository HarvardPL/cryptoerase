import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class Screen {
    static final String ANSI_CLS = "\u001b[2J";
    static final String ANSI_HOME = "\u001b[H";
    
    private final SimpleIRCClient parent;

    private ArrayList{L /parent.clearHistory T} buffer;
    private final PrintStream out;
    private final InputStreamReader{L} isr;
    private final ArrayList{L} inputBuffer;
    private String incomplete;
    
    public Screen(SimpleIRCClient parent) {
	int{L} screenCtorLow = 0;
    	this.parent = parent;
    	this.buffer = new ArrayList();
    	this.inputBuffer = new ArrayList();
	this.out = System.out;
	this.isr = new InputStreamReader(System.in);
    	this.incomplete = "";
    }
    
    public String getInput() {
	this.bufferInput();
	if (!inputBuffer.isEmpty()) {
	    String input = (String) inputBuffer.get(0);
	    inputBuffer.remove(0);
	    return input;
    	} else {
	    return null;
    	}
    }
    
    public void bufferInput() {
	try {
	    int c = -1;
	    while (isr.ready() && (c = isr.read()) != -1) {
		if (c == '\n') {
		    inputBuffer.add(incomplete);
		    incomplete = "";
		} else {
		    incomplete += (char) c;
		}
	    }
	} catch (Throwable e) {}
    }
    
    public void flush() {
	int{L} flushEnter = 0;
    	this.bufferInput();
    	this.clearScreen();
	int drawn = 0;
	try {
	    for (int i = 0; i < buffer.size(); i++) {
		// Declassify output to the screen
    		out.println(({L})buffer.get(i));
		drawn++;
	    }
	} catch (Throwable e) {}
	for (int i = drawn; drawn < 20; drawn++) {
	    out.println();
    	}
	out.print("> " + incomplete);
    	out.flush();
    }
    
    public void writeLine(String line) {
	try {
	    buffer.add(line);
	    while (buffer.size() > 20) {
		buffer.remove(0);
	    }
	} catch (Throwable e) {}
    }
    
    public void clearScreen() {
    	out.print(ANSI_CLS + ANSI_HOME);
    	out.flush();
    }
}
