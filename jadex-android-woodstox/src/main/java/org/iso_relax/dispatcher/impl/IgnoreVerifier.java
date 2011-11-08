/*
 * @(#)$Id$
 *
 * Copyright 2001 Kohsuke KAWAGUCHI
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
package org.iso_relax.dispatcher.impl;

import org.iso_relax.dispatcher.Dispatcher;
import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ignores namespaces which have no associated grammar.
 * 
 * @author
 *		<a href="mailto:k-kawa@bigfoot.com">Kohsuke KAWAGUCHI</a>
 */
public final class IgnoreVerifier
	extends DefaultHandler
	implements IslandVerifier
{
	private final ElementDecl[] rules;
	
	/**
	 * 
	 * @param assignedRules
	 *		this Verifier is supposed to validate these rules.
	 *		since this IslandVerifier actually does nothing,
	 *		all these rules will be reported as satisfied
	 *		upon completion.
	 */
	public IgnoreVerifier( String namespaceToIgnore, ElementDecl[] assignedRules )
	{
		this.namespaceToIgnore = namespaceToIgnore;
		this.rules = assignedRules;
	}
	
	/**
	 * elements in this namespace is validated by this IgnoreVerifier.
	 */
	private final String namespaceToIgnore;
	
	public ElementDecl[] endIsland() { return rules; }
	public void endChildIsland( String uri, ElementDecl[] assignedLabels ){}
	
	private Dispatcher dispatcher;
	public void setDispatcher( Dispatcher disp ) { this.dispatcher=disp; }
	
	public void startElement( String namespaceURI, String localName, String qName, Attributes attributes )
		throws SAXException
	{
		if( namespaceToIgnore.equals(namespaceURI) )
			return;		// this element is "validated".
		
		// try to locate a grammar of this namespace
		IslandSchema is = dispatcher.getSchemaProvider().getSchemaByNamespace(namespaceURI);
		if( is==null )
		{// no grammar is declared with this namespace URI.
			return;	// continue ignoring.
		}

		// a schema is found: revert to normal mode and validate them.
		IslandVerifier iv = is.createNewVerifier( namespaceURI, is.getElementDecls() );
		dispatcher.switchVerifier(iv);
		
		// simulate this startElement method.
		iv.startElement(namespaceURI,localName,qName,attributes);
	}
}
