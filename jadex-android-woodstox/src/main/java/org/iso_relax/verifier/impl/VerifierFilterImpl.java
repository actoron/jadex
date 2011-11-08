package org.iso_relax.verifier.impl;

import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFilter;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * plain vanilla {@link VerifierFilter} implementation.
 * 
 * <p>
 * A verifier implementation can use this class to support VerifierFilter functionality.
 * 
 * <p>
 * To use this class, implement the {@link Verifier#getVerifierFilter()} method
 * as follows:
 * <pre>
 * public VerifierFilter getVerifierFilter() throws SAXException {
 *   return new VerifierFilterImpl(getVerifierHandler());
 * }
 * </pre>
 *
 * <p>
 * Also, usually you may want to override <code>setErrorHandler</code> method so that
 * your <code>VerifierHandler</code> will send errors to that handler.
 * 
 * @version	$Id$
 * @author  <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class VerifierFilterImpl extends XMLFilterImpl implements VerifierFilter {
	
	public VerifierFilterImpl( Verifier _verifier ) throws SAXException {
		this.verifier = _verifier;
		this.core = verifier.getVerifierHandler();
	}
	
	private final Verifier verifier;
	private final VerifierHandler core;

	
	public boolean isValid() {
		return core.isValid();
	}
	
	public void setErrorHandler( ErrorHandler handler ) {
		super.setErrorHandler(handler);
		// we need to call the setErrorHandler method of the verifier,
		// so that the verifier handler will use this error handler from now on.
		verifier.setErrorHandler(handler);
	}
	public void setEntityResolver( EntityResolver resolver ) {
		super.setEntityResolver(resolver);
		verifier.setEntityResolver(resolver);
	}

	//
	//
	//	ContentHandler events
	//
	//
	
	public void setDocumentLocator (Locator locator) {
		core.setDocumentLocator(locator);
		super.setDocumentLocator(locator);
	}
	
	public void startDocument() throws SAXException {
		core.startDocument();
		super.startDocument();
	}
		
	public void endDocument () throws SAXException {
		core.endDocument();
		super.endDocument();
	}

	public void startPrefixMapping (String prefix, String uri) throws SAXException {
		core.startPrefixMapping(prefix,uri);
		super.startPrefixMapping(prefix,uri);
	}

	public void endPrefixMapping (String prefix) throws SAXException {
		core.endPrefixMapping(prefix);
		super.endPrefixMapping(prefix);
	}
	    
	public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException {
		core.startElement(uri,localName,qName,attributes);
		super.startElement(uri,localName,qName,attributes);
	}
	    
	public void endElement (String uri, String localName, String qName) throws SAXException {
		core.endElement(uri,localName,qName);
		super.endElement(uri,localName,qName);
	}
	    
	public void characters (char ch[], int start, int length) throws SAXException {
		core.characters(ch,start,length);
		super.characters(ch,start,length);
	}
	    
	public void ignorableWhitespace (char ch[], int start, int length) throws SAXException {
		core.ignorableWhitespace(ch,start,length);
		super.ignorableWhitespace(ch,start,length);
	}
	    
	public void processingInstruction (String target, String data) throws SAXException {
		core.processingInstruction(target,data);
		super.processingInstruction(target,data);
	}

	public void skippedEntity (String name) throws SAXException {
		core.skippedEntity(name);
		super.skippedEntity(name);
	}
}
