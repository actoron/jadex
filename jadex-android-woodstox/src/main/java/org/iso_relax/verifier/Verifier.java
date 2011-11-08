package org.iso_relax.verifier;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * An instance of this interface can validates documents.
 * 
 * <p>
 * An instance of this interface can be obtained through the
 * {@link VerifierFactory#newVerifier} method or 
 * {@link Schema#newVerifier} method.
 * Once it is created, an application can use one instance to validate 
 * multiple documents.
 * 
 * <p>
 * This interface is <b>not thread-safe</b> and <b>not reentrant</b>.
 * That is, only one thread can use it at any given time, and you can only
 * validate one document at any given time.
 *
 * @since   Feb. 23, 2001
 * @version Mar.  4, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 * @author	<a href="mailto:kohsukekawaguchi@yahoo.com">Kohsuke KAWAGUCHI</a>
 */
public interface Verifier {
	/**
	 * a read-only feature that checks whether the implementation supports
	 * <code>getVerifierHandler</code> method.
	 * 
	 * <p>
	 * Now a verifier implementation is <b>required</b> to support
	 * VerifierHandler. Therefore an application doesn't need to
	 * check this feature.
	 * 
	 * @deprecated
	 */
    String FEATURE_HANDLER = "http://www.iso-relax.org/verifier/handler";
	
	/**
	 * a read-only feature that checks whether the implementation supports
	 * <code>getVerifierFilter</code> method.
	 * 
	 * <p>
	 * Now a verifier implementation is <b>required</b> to support
	 * VerifierFilter.
	 * Therefore an application doesn't need to check this feature.
	 * 
	 * @deprecated
	 */
    String FEATURE_FILTER = "http://www.iso-relax.org/verifier/filter";

    /**
     * Checks whether a feature is supported or not.
     * 
     * <p>
     * This method is modeled after SAX2.
     *
     * @param feature feature name
     */
    boolean isFeature(String feature)
        throws SAXNotRecognizedException, SAXNotSupportedException;

    /**
     * Sets a value to a feature.
     * 
     * <p>
     * This method is modeled after SAX2.
     *
     * @param feature feature name
     * @param value feature value
     */
    void setFeature(String feature, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException;

    /**
     * Gets a property value
     * 
     * <p>
     * This method is modeled after SAX2.
     *
     * @param property property name
     */
    Object getProperty(String property)
        throws SAXNotRecognizedException, SAXNotSupportedException;

    /**
     * Sets a property value
     * 
     * <p>
     * This method is modeled after SAX2.
     *
     * @param property property name
     * @param value property value
     */
    void setProperty(String property, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException;

    /**
     * Sets a <code>ErrorHandler</code> that receives validation
     * errors/warnings.
     * 
     * <p>
     * If no error handler is set explicitly, a verifier implementation
     * will not report any error/warning at all. However, the caller can
     * still obtain the result of validation through the return value.
     * 
     * <p>
     * Conscious developers should always set an error handler explicitly
     * as the default behavior has been changed several times.
     * 
     * @param handler
     *		this object will receive errors/warning encountered
     *		during the validation.
     */
    void setErrorHandler(ErrorHandler handler);

    /**
     * Sets a <code>EntityResolver</code> to resolve external entity locations.
     * 
     * <p>
     * The given entity resolver is used in the
     * {@link verify(String)} method and the 
     * {@link verify(InputSource)} method.
     * 
     * @param handler EntityResolver
     */
    void setEntityResolver(EntityResolver handler);

    /**
     * validates an XML document.
     *
     * @param uri
     *		URI of a document.
     * 
     * @return
     *		<b>true</b> if the document is valid.
     *		<b>false</b> if otherwise.
     */
    boolean verify(String uri) throws SAXException, IOException;

    /**
     * validates an XML document.
     *
     * @param source InputSource of a XML document to verify.
     * 
     * @return
     *		<b>true</b> if the document is valid.
     *		<b>false</b> if otherwise.
     */
    boolean verify(InputSource source) throws SAXException, IOException;
    
    /**
     * validates an XML document.
     *
     * @param file
     *		File to be validated
     * 
     * @return
     *		<b>true</b> if the document is valid.
     *		<b>false</b> if otherwise.
     */
    boolean verify(File file) throws SAXException, IOException;
    
    /**
     * validates an XML document.
     * 
     * <p>
     * An implementation is required to accept <code>Document</code> object
     * as the node parameter. If it also implements partial validation,
     * it can also accepts things like <code>Element</code>.
     * 
     * @param node
     *		the root DOM node of an XML document.
     * 
     * @exception	UnsupportedOperationException
     *		If the node type of the node parameter is something which
     *		this implementation does not support.
     * 
     * @return
     *		<b>true</b> if the document is valid.
     *		<b>false</b> if otherwise.
     */
    boolean verify(Node node) throws SAXException;

    /**
     * Gets a VerifierHandler.
     * 
     * <p>
     * you can use the returned
     * <code>VerifierHandler</code> to validate documents
     * through SAX.
     * 
     * <p>
     * Note that two different invocations of this method
     * can return the same value; this method does NOT
     * necessarily create a new <code>VerifierHandler</code> object.
     */
    VerifierHandler getVerifierHandler() throws SAXException;

    /**
     * Gets a VerifierFilter.
     * 
     * <p>
     * you can use the returned
     * <code>VerifierHandler</code> to validate documents
     * through SAX.
     * 
     * <p>
     * Note that two different invocations of this method
     * can return the same value; this method does NOT
     * necessarily create a new <code>VerifierFilter</code> object.
     */
    VerifierFilter getVerifierFilter() throws SAXException;
}
