import java.io.*;
import java.net.*;
import java.util.ArrayList;
import accrue.cryptoerase.runtime.Condition;

public class SimpleIRCClient {
    public static final Condition clearHistory = new Condition();

    private String server;
    private String channel;
    private String nick;
    private String login;

    private boolean scrub = true;

    private Screen screen;
    private Log log;
	
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

    private void writeLine(String line) {
	try { screen.writeLine(line); } catch (Throwable e) {}
	try { log.writeLine(line); } catch (Throwable e) {}
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
	} catch (Throwable e) {}

	try {
	    screen = new Screen(this);
	    log = new Log(this);
	} catch (Throwable e) {}
	try {
	    this.writeLine("Connecting...");
	    screen.flush();
	} catch (Throwable e) {}
        
	// Log on to the server.
	try {
	    writer.write("NICK " + nick + "\r\n");
	    writer.write("USER " + login + " 8 * : Simple IRC client\r\n");
	    writer.flush();
	} catch (Throwable e) {}
        
	try {
	    this.writeLine("Waiting for response...");
	    screen.flush();
	} catch (Throwable e) {}

	// Read lines from the server until it tells us we have connected.
	String line = null;
	try {
	    while ((line = reader.readLine()) != null) {
		if (line.indexOf("004") >= 0) {
		    // We are now logged in.
		    break;
		}
		else if (line.indexOf("433") >= 0) {
		    this.writeLine("Nickname is already in use.");
		    System.exit(-1);
		} else {
		    this.writeLine(line);
		}
	    }
	} catch (Throwable e) {}
        
	try {
	    this.writeLine("Joining " + channel + "...");
	    screen.flush();
	} catch (Throwable e) {}

        // Join the channel.
	try {
	    writer.write("JOIN " + channel + "\r\n");
	    writer.flush();
	} catch (Throwable e) {}
        
	// Keep reading lines from the server or input
        while (true) {
	    boolean redraw = false;
	    String input = screen.getInput();
	    if (input != null) {
		redraw = true;
		String command = input.toLowerCase();
		if (command.startsWith("/clear")) {
		    clearHistory.set();
		    if (scrub) {
			screen = new Screen(this);
			log = new Log(this);
		    }
		} else if (command.startsWith("/scrubon")) {
		    scrub = true;
		} else if (command.startsWith("/scruboff")) {
		    scrub = false;
		} else if (command.startsWith("/history")) {
		    this.writeLine(">>> History contains " + log.historyLength() + " screens");
		} else if (command.startsWith("/replay")) {
		    try {
			int replay = Integer.parseInt(command.substring(7).trim());
			ArrayList history = null;
			try { history = log.getScreen(replay); } catch (Throwable e) {}
			/*
			  // INJECTABLE FAULT: use decrypted data at too low a level
			  String{L} test = (String) history.get(0);
			*/
			int size = 0;
			// Declassify the length of the log
			// ANNOTATION: declassification
			try { size = ({L}) history.size(); } catch (Throwable e) {}
                        for (int i = 0; i < size; i++) {
                            String next = "";
			    try { next = (String) history.get(i); } catch (Throwable e) {}
			    try { this.writeLine(next); } catch (Throwable e) {}
			}
		    } catch (Throwable e) {}
		} else if (command.startsWith("/quit")) {
		    System.exit(0);
		} else {
		    boolean sent = true;
		    try {
			writer.write("PRIVMSG " + channel + " :" + input + "\r\n");
			writer.flush();
		    } catch (Throwable e) {}
		    try {
			this.writeLine(">>> You say: " + input);
		    } catch (Throwable e) {}
		}
	    }
	    try {
		if (reader.ready()) {
		    redraw = true;
		    String fromNetwork = reader.readLine();
		    this.writeLine(fromNetwork);
		    boolean pinged = false;
		    try {
			// Declassify whether the message was PING
			// ANNOTATION: declassifcation
			pinged = ({L})fromNetwork.startsWith("PING ");
		    } catch (Throwable e) {}
		    if (pinged) {
			// We must respond to PINGs to avoid being disconnected.
			try {
			    writer.write("PONG " + fromNetwork.substring(5) + "\r\n");
			    writer.flush();
			} catch (Throwable e) {}
			this.writeLine(">>> You sent a PONG!");
		    }
		}
	    } catch (Throwable e) {}
	    if (redraw) {
		try {
		    screen.flush();
		} catch (Throwable e) {}
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
