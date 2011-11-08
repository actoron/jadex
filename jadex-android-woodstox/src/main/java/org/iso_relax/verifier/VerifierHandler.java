package org.iso_relax.verifier;

import org.xml.sax.ContentHandler;

/**
 * SAX2 ContentHandler implementation that validates a document.
 * 
 * <p>
 * An instance of this interface can be obtained through the
 * {@link Verifier#getVerifierHandler} method.
 * 
 * <p>
 * The implementation validates incoming SAX events.
 * The application can check the result by calling the isValid method.
 *
 * @since   Feb. 23, 2001
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public interface VerifierHandler extends ContentHandler {
	/**
	 * Checks if the document was valid.
	 * 
	 * <p>
	 * This method can be only called after this handler receives
	 * the <code>endDocument</code> event.
	 * 
	 * <p>
	 * If you need to know the error at an earlier moment,
	 * you should set an error handler to {@link Verifier}.
	 * 
	 * @return
	 *		<b>true</b> if the document was valid,
	 *		<b>false</b> if not.
	 * 
	 * @exception IllegalStateException
	 *		If this method is called before the endDocument event is dispatched.
	 */
    boolean isValid() throws IllegalStateException;
}
