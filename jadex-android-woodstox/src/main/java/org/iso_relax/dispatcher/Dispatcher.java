/*
 * @(#)$Id$
 *
 * Copyright 2001 MURATA Makoto, KAWAGUCHI Kohsuke
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.iso_relax.dispatcher;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * splits incoming SAX events to "islands", and feed events to IslandVerifier.
 * 
 * @author
 *		<a href="mailto:mura034@attglobal.net">MURATA Makoto (FAMILY Given)</a>,
 *		<a href="mailto:k-kawa@bigfoot.com">Kohsuke KAWAGUCHI</a>
 * 
 * @version 1.1
 */
public interface Dispatcher {
	/**
	 * configure XMLReader to use this Dispatcher as a ContentHandler.
	 */
	void attachXMLReader( XMLReader reader );
	
	/**
	 * switches to the child IslandVerifier.
	 * this method can only be called during startElement method.
	 */
	void switchVerifier( IslandVerifier newVerifier ) throws SAXException;
	
	/**
	 * sets application-implemented ErrorHandler, which will receive all validation
	 * errors.
	 */
	void setErrorHandler( ErrorHandler handler );
	
	/**
	 * gets ErrorHandler to which IslandVerifier reports validation errors.
	 * 
	 * the caller may not assume that this method returns the same object
	 * that was passed to setErrorHandler method.
	 * 
	 * this method cannot return null.
	 */
	ErrorHandler getErrorHandler();
	
	/** get ShcmeaProvider object which is attached to this Dispatcher. */
	SchemaProvider getSchemaProvider();

	
	
	
	
	public static class NotationDecl {
		
		public final String name;
		public final String publicId;
		public final String systemId;
		public NotationDecl( String name, String publicId, String systemId ) {
			this.name=name; this.publicId=publicId; this.systemId=systemId;
		}
	}
	
	/** counts notation declarations found in this XML instance. */
	int countNotationDecls();
	
	/** gets <i>i</i>th notation declaration found in this XML instance.
	 * 
	 * IslandVerifiers can not receive DTDHandler events.
	 * Those who need DTD information should call this method.
	 */
	NotationDecl getNotationDecl( int index );
	
	public static class UnparsedEntityDecl {
		
		public final String name;
		public final String publicId;
		public final String systemId;
		public final String notation;
		public UnparsedEntityDecl( String name, String publicId, String systemId, String notation ) {
			this.name=name; this.publicId=publicId; this.systemId=systemId; this.notation=notation;
		}
	}
	
	/** counts unparsed entities found in this XML instance. */
	int countUnparsedEntityDecls();
	
	/** gets <i>i</i>th unparsed entity found in this XML instance.
	 * 
	 * IslandVerifiers can not receive DTDHandler events.
	 * Those who need DTD information should call this method.
	 */
	UnparsedEntityDecl getUnparsedEntityDecl( int index );
	
	
}
