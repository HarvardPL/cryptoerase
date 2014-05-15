import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JEditorPane;

public class CachedJEditorPane extends JEditorPane {
    private static final long serialVersionUID = -2704024459339695654L;
    private final URLCache cache;

    public CachedJEditorPane() {
        super();
        cache = new URLCache();
    }

    /**
     * Fetches a stream for the given URL, which is about to
     * be loaded by the <code>setPage</code> method.  By
     * default, this simply opens the URL and returns the
     * stream.  This can be reimplemented to do useful things
     * like fetch the stream from a cache, monitor the progress
     * of the stream, etc.
     * <p>
     * This method is expected to have the the side effect of
     * establishing the content type, and therefore setting the
     * appropriate <code>EditorKit</code> to use for loading the stream.
     * <p>
     * If this the stream was an http connection, redirects
     * will be followed and the resulting URL will be set as
     * the <code>Document.StreamDescriptionProperty</code> so that relative
     * URL's can be properly resolved.
     *
     * @param page  the URL of the page
     */
    protected InputStream getStream(URL page) throws IOException {
	//	InputStream is = cache.get(page);
        /*if (is != null) {
            System.out.println("Cache hit for " + page);
        }
        else {*/
	//            System.out.println("Cache miss for " + page);
	URL{L} pageCheck = page;
	InputStream{L} data = super.getStream(page);
	cache.put(page, data);
	    //    is = cache.get(page);
	    //}
	return data;
	    //        return is;
    }

    public InputStream resolveStream(URL page) throws IOException {
	return getStream(page);
    }
}
