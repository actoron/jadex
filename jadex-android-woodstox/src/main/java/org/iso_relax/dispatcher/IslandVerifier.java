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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Interface for verifier that validates one island.
 * 
 * @author
 *		<a href="mailto:mura034@attglobal.net">MURATA Makoto (FAMILY Given)</a>,
 *		<a href="mailto:k-kawa@bigfoot.com">Kohsuke KAWAGUCHI</a>
 * 
 * @version 1.1
 */
public interface IslandVerifier extends ContentHandler {
	/**
	 * Dispatcher passes itself to IslandVerifier by calling this method
	 * from Dispatcher.switchVerifier method.
	 */
	void setDispatcher( Dispatcher disp );
	
	/**
	 * substitute for endDocument event.
	 * 
	 * This method is called after endElement method is called
	 * for the top element in the island.
	 * endDocument method is never called for IslandVerifier.
	 * 
	 * @return
	 *		the callee must return all validated ElementDecls.
	 *		If every candidate fails, return an empty array.
	 *		
	 *		It is the callee's responsibility
	 *		to report an error. The callee may also recover from error.
	 * 
	 *		Never return null.
	 */
	public ElementDecl[] endIsland() throws SAXException;
	
	/**
	 * this method is called after verification of the child island
	 * is completed, instead of endElement method.
	 * 
	 * @param uri
	 *		namespace URI of the child island.
	 * @param assignedLabel
	 *		set of elementDecls that were successfully assigned
	 *		to this child island.
	 *		when every elementDecl was failed, then an empty array is passed.
	 */
	public void endChildIsland( String uri, ElementDecl assignedDecls[] ) throws SAXException;
}