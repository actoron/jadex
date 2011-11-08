package org.iso_relax.verifier;

import org.xml.sax.XMLFilter;

/**
 * XMLFilter implementation that validates a document.
 * 
 * <p>
 * An instance of this interface can be obtained through the
 * {@link Verifier#getVerifierFilter} method.
 * 
 * <p>
 * The implementation validates incoming SAX events and then pass it
 * to the successive SAX handlers.
 *
 * @since   Feb. 23, 2001
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public interface VerifierFilter extends XMLFilter {
	/**
	 * checks if the document was valid.
	 * 
	 * <p>
	 * This method can be only called after this handler receives
	 * the endDocument event.
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
