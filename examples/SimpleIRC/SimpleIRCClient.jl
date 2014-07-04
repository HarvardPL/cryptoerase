import java.io.*;
import java.net.*;
import accrue.cryptoerase.runtime.Condition;

public class SimpleIRCClient {
    public static final Condition{L} clearHistory = new Condition();

    private String server;
    private String channel;
    private String nick;
    private String login;

    private boolean scrub = true;
	
    public SimpleIRCClient(String channel) {
	this("irc.freenode.net", channel);
    }
	
    public SimpleIRCClient(String server, String channel) {
	this(server, channel, "SimpleIRCClient");
    }
	
    public SimpleIRCClient(String server, String channel, String ident) {
	this(server, channel, ident, ident);
    }
	
    public SimpleIRCClient(String server, String channel, String nick, String login) {
	this.server = server;
	this.channel = channel;
	this.nick = nick;
	this.login = login;
    }

    public void start() {
	// Connect directly to the IRC server.
	BufferedWriter writer = null;
	BufferedReader reader = null;
	try {
	    Socket socket = new Socket(server, 6667);
	    writer =
		new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    reader =
		new BufferedReader(new InputStreamReader(socket.getInputStream()));
	} catch (Throwable e) { e.printStackTrace(); }

        Screen screen = new Screen(this);
	try {
	    screen.writeLine("Connecting...");
	    screen.flush();
	} catch (Throwable e) { e.printStackTrace(); }
        
	// Log on to the server.
	try {
	    writer.write("NICK " + nick + "\r\n");
	    writer.write("USER " + login + " 8 * : Simple IRC client\r\n");
	    writer.flush();
	} catch (Throwable e) { e.printStackTrace(); }
        
	try {
	    screen.writeLine("Waiting for response...");
	    screen.flush();
	} catch (Throwable e) { e.printStackTrace(); }

	// Read lines from the server until it tells us we have connected.
	String line = null;
	try {
	    while ((line = reader.readLine( )) != null) {
		if (line.indexOf("004") >= 0) {
		    // We are now logged in.
		    break;
		}
		else if (line.indexOf("433") >= 0) {
		    screen.writeLine("Nickname is already in use.");
		    System.exit(-1);
		} else {
		    screen.writeLine(line);
		}
	    }
	} catch (Throwable e) { e.printStackTrace(); }
        
	try {
	    screen.writeLine("Joining " + channel + "...");
	    screen.flush();
	} catch (Throwable e) { e.printStackTrace(); }

        // Join the channel.
	try {
	    writer.write("JOIN " + channel + "\r\n");
	    writer.flush();
	} catch (Throwable e) { e.printStackTrace(); }
        
	// Keep reading lines from the server or input
        while (true) {
	    boolean{L} redraw = false;
	    String{L} input = screen.getInput();
	    if (input != null) {
		redraw = true;
		String command = input.toLowerCase();
		if (command.startsWith("/clear")) {
		    clearHistory.set();
		    if (scrub) {
			screen = new Screen(this);
		    }
		} else if (command.startsWith("/scrubon")) {
		    scrub = true;
		} else if (command.startsWith("/scruboff")) {
		    scrub = false;
		} else if (command.startsWith("/quit")) {
		    System.exit(0);
		} else {
		    boolean sent = true;
		    try {
			writer.write("PRIVMSG " + channel + " :" + input + "\r\n");
			writer.flush();
		    } catch (Throwable e) { e.printStackTrace(); }
		    try {
			screen.writeLine(">>> You say: " + input);
		    } catch (Throwable e) { e.printStackTrace(); }
		}
	    }
	    try {
		if (reader.ready()) {
		    redraw = true;
		    String{L /clearHistory H} fromNetwork = reader.readLine();
		    if (fromNetwork != null) {
			screen.writeLine(fromNetwork);
			if (fromNetwork.startsWith("PING ")) {
			    // We must respond to PINGs to avoid being disconnected.
			    writer.write("PONG " + fromNetwork.substring(5) + "\r\n");
			    writer.flush();
			    screen.writeLine(">>> You sent a PONG!");
			}
		    }
		}
	    } catch (Throwable e) { e.printStackTrace(); }
	    if (redraw) {
		try {
		    screen.flush();
		} catch (Throwable e) { e.printStackTrace(); }
	    }
        }
    }
	
    public static void main(String[] args) throws Exception {
    	SimpleIRCClient client = null;
    	switch (args.length) {
    	case 1:
	    client = new SimpleIRCClient(args[0]);
	    break;
    	case 2:
	    client = new SimpleIRCClient(args[0], args[1]);
	    break;
    	case 3:
	    client = new SimpleIRCClient(args[0], args[1], args[2]);
	    break;
    	default:
	    System.err.println("Usage: SimpleIRCClient [channel [server [nick]]]");
	    System.exit(-1);
    	}
    	
    	client.start();
    }

}
