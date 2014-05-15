package javax.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.HighInputStream;
import java.net.URL;

import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;

public class JEditorPane extends JTextComponent implements Scrollable {
    private URL page;
    private InputStream content;
    private HyperlinkListener hlListener;
    private PropertyChangeListener pcListener;

    public URL getPage() {
	return page;
    }

    protected InputStream getStream(URL page) throws IOException {
	InputStream retVal = new HighInputStream();
	return retVal;
    }

    public void setPage(URL newPage) throws IOException {
	URL oldPage = page;
	page = newPage;
	content = getStream(newPage);
	pcListener.propertyChange(new PropertyChangeEvent(this,"page",oldPage,newPage));
    }

    public void addHyperlinkListener(HyperlinkListener l) {
	hlListener = l;
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
	pcListener = l;
    }

    public void setContentType(String type) {}
    public void setEditable(boolean editable) {}
}
