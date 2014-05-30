import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

// The Simple Web Browser.
public class Browser extends JFrame implements HyperlinkListener {
    private static final long serialVersionUID = 6482307664780440525L;

    // These are the buttons for iterating through the page list.
    private JButton backButton, forwardButton;

    // Page location text field.
    private JTextField locationTextField;

    // Editor pane for displaying pages.
    private JEditorPane displayEditorPane;

    // Browser's list of pages that have been visited.
    private ArrayList pageList = new ArrayList();

    // Constructor for Mini Web Browser.
    public Browser() {
        // Set application title.
        super("Browser");

        // Set window size.
        setSize(1024, 768);

        // Handle closing events.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });

        // Set up file menu.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Set up button panel.
        JPanel buttonPanel = new JPanel();
        backButton = new JButton("< Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionBack();
            }
        });
        backButton.setEnabled(false);
        buttonPanel.add(backButton);
        forwardButton = new JButton("Forward >");
        forwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionForward();
            }
        });
        forwardButton.setEnabled(false);
        buttonPanel.add(forwardButton);
        locationTextField = new JTextField(35);
        locationTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionGo();
                }
            }
        });
        buttonPanel.add(locationTextField);
        JButton goButton = new JButton("GO");
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionGo();
            }
        });
        buttonPanel.add(goButton);

        // Set up page display.
	try {
	    displayEditorPane = new CachedJEditorPane();
	} catch (Exception e) {
	    System.err.println("Initializing CachedJEditorPane failed, falling back on JEditorPane...\n");
	    e.printStackTrace();
	    displayEditorPane = new JEditorPane();
	}
        displayEditorPane.setContentType("text/html");
        displayEditorPane.setEditable(false);
        displayEditorPane.addHyperlinkListener(this);
        displayEditorPane.addPropertyChangeListener("page",
                                                    new PropertyChangeListener() {
                                                        public void propertyChange(
                                                                PropertyChangeEvent evt) {
                                                            actionUpdate();
                                                        }
                                                    });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(displayEditorPane),
                             BorderLayout.CENTER);
    }

    // Exit this program.
    private void actionExit() {
        System.exit(0);
    }

    // Go back to the page viewed before the current page.
    private void actionBack() {
        URL currentUrl = displayEditorPane.getPage();
        int pageIndex = pageList.indexOf(currentUrl);
        try {
            showPage((URL) pageList.get(pageIndex - 1));
        }
        catch (Exception e) {
        }
    }

    // Go forward to the page viewed after the current page.
    private void actionForward() {
        URL currentUrl = displayEditorPane.getPage();
        int pageIndex = pageList.indexOf(currentUrl);
        try {
            showPage((URL) pageList.get(pageIndex + 1));
        }
        catch (Exception e) {
        }
    }

    // Load and show the page specified in the location text field.
    private void actionGo() {
        URL verifiedUrl = verifyUrl(locationTextField.getText());
        if (verifiedUrl != null) {
            showPage(verifiedUrl);
        }
        else {
            showError("Invalid URL");
        }
    }

    // Update the history
    private void actionUpdate() {
        // Get URL of new page being displayed.
        URL newUrl = displayEditorPane.getPage();

        int listSize = pageList.size();
        while (listSize > 20) {
            pageList.remove(0);
        }
        pageList.add(newUrl);

        // Update location text field with URL of current page.
        locationTextField.setText(newUrl.toString());

        // Update buttons based on the page being displayed.
        updateButtons();
    }

    // Show dialog box with error message.
    private void showError(String errorMessage) {
        JOptionPane.showMessageDialog(this,
                                      errorMessage,
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    // Verify URL format.
    private URL verifyUrl(String url) {
        // Only allow HTTP URLs.
        if (!url.toLowerCase().startsWith("http://")) return null;

        // Verify format of URL.
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        }
        catch (Exception e) {
            return null;
        }

        return verifiedUrl;
    }

    /* Show the specified page and add it to
       the page list if specified. */
    private void showPage(URL pageUrl) {
        // Show hour glass cursor while crawling is under way.
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            // Get URL of page currently being displayed.
            URL currentUrl = displayEditorPane.getPage();

            // Load and display specified page.
            displayEditorPane.setPage(pageUrl);
        }
        catch (Exception e) {
            // Show error messsage.
            showError("Unable to load page");
        }
        finally {
            // Return to default cursor.
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /* Update back and forward buttons based on
       the page being displayed. */
    private void updateButtons() {
        if (pageList.size() < 2) {
            backButton.setEnabled(false);
            forwardButton.setEnabled(false);
        }
        else {
            URL currentUrl = displayEditorPane.getPage();
            int pageIndex = pageList.indexOf(currentUrl);
            backButton.setEnabled(pageIndex > 0);
            forwardButton.setEnabled(pageIndex < (pageList.size() - 1));
        }
    }

    // Handle hyperlink's being clicked.
    public void hyperlinkUpdate(HyperlinkEvent event) {
        HyperlinkEvent.EventType eventType = event.getEventType();
        if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
            if (event instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent linkEvent =
                        (HTMLFrameHyperlinkEvent) event;
                HTMLDocument document =
                        (HTMLDocument) displayEditorPane.getDocument();
                document.processHTMLFrameHyperlinkEvent(linkEvent);
            }
            else {
                showPage(event.getURL());
            }
        }
    }

    // Run the Mini Browser.
    public static void main(String[] args) throws MalformedURLException {
        Browser browser = new Browser();
        browser.setVisible(true);

	HyperlinkEvent he = new HyperlinkEvent(browser, HyperlinkEvent.EventType.ACTIVATED, new URL("http://en.wikipedia.org/wiki/Main_Page"));
	browser.hyperlinkUpdate(he);
    }
}
