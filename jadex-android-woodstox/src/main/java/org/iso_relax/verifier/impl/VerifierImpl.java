package org.iso_relax.verifier.impl;

import java.io.File;
import java.io.IOException;

import javaxx.xml.parsers.ParserConfigurationException;
import javaxx.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFilter;
import org.iso_relax.verifier.VerifierHandler;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Partial implementation of {@link Verifier}.
 * 
 * <p>
 * This class is useful as the base class of the verifier implementation.
 * 
 * <p>
 * The only remaining method that has to be implemented by the derived
 * class is the <code>getVerifierHandler</code> method. Please be noted
 * that applications can call the <code>setErrorHandler</code> method
 * after the <code>getVerifierHandler</code> method and that change
 * should take effect.
 * 
 * 
 * @version	$Id$
 * @author  <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class VerifierImpl implements Verifier
{
	protected XMLReader reader;
	
	protected VerifierImpl() throws VerifierConfigurationException {
		prepareXMLReader();
	}
	
	/**
	 * Creates and sets a sole instance of XMLReader which will be used
	 * by this verifier.
	 */
	protected void prepareXMLReader() throws VerifierConfigurationException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			reader = factory.newSAXParser().getXMLReader();
		} catch( SAXException e ) {
			throw new VerifierConfigurationException(e);
		} catch( ParserConfigurationException pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}
	
	public boolean isFeature(String feature)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		
		if (FEATURE_HANDLER.equals(feature) ||
			FEATURE_FILTER.equals(feature))
			return true;
		
		throw new SAXNotRecognizedException(feature);
	}

    public void setFeature(String feature, boolean value)
		throws SAXNotRecognizedException, SAXNotSupportedException {
					 
		throw new SAXNotRecognizedException(feature);
	}

	public Object getProperty(String property)
		throws SAXNotRecognizedException, SAXNotSupportedException {
					 
		throw new SAXNotRecognizedException(property);
	}

    public void setProperty(String property, Object value)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		
		throw new SAXNotRecognizedException(property);
	}

	
	// default error handler must not report any error.
	protected ErrorHandler errorHandler = new ErrorHandler(){
		public void warning( SAXParseException e ) {}
		public void error( SAXParseException e ) {}
		public void fatalError( SAXParseException e ) {}
	};
	
	public void setErrorHandler(ErrorHandler handler) {
		this.errorHandler = handler;
	}

	protected EntityResolver entityResolver;
	public void setEntityResolver(EntityResolver resolver ) {
		this.entityResolver = resolver;
	}

	public boolean verify(String uri) throws SAXException, IOException {
		return verify( new InputSource(uri) );
	}

	public boolean verify(InputSource source) throws SAXException, IOException {
		
		VerifierHandler handler = getVerifierHandler();
		
		reader.setErrorHandler(errorHandler);
		if(entityResolver!=null)
			reader.setEntityResolver(entityResolver);
		reader.setContentHandler(handler);
		reader.parse(source);
		
		return handler.isValid();
	}
    
	public boolean verify(File f) throws SAXException, IOException {
		String uri = "file:" + f.getAbsolutePath();
		if (File.separatorChar == '\\') {
			uri = uri.replace('\\', '/');
		}
        return verify(new InputSource(uri));
    }
    
	public boolean verify(Node node) throws SAXException {
		SAXEventGenerator generator = new SAXEventGenerator(node);
		// generate startDocument/endDocument events
		generator.setDocumentEmulation(true);
		generator.setErrorHandler(errorHandler);
		VerifierHandler handler = getVerifierHandler();
		generator.makeEvent(handler);
		return handler.isValid();
	}

	public abstract VerifierHandler getVerifierHandler() throws SAXException;

	private VerifierFilter filter;
	public VerifierFilter getVerifierFilter() throws SAXException {
		if(filter==null)
			filter = new VerifierFilterImpl(this);
		return filter;
	}
}
