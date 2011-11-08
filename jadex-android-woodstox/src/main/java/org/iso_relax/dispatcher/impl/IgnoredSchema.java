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

import java.util.Iterator;
import java.util.Vector;

import org.iso_relax.dispatcher.AttributesDecl;
import org.iso_relax.dispatcher.AttributesVerifier;
import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;

/**
 * IslandSchema implementation for "ignored" island.
 * 
 * This schema exports whatever importer wants, and anything is valid in this schema.
 * 
 * @author		<a href="mailto:k-kawa@bigfoot.com">Kohsuke KAWAGUCHI</a>
 */
public class IgnoredSchema implements IslandSchema
{
	private static final ElementDecl[] theElemDecl = new ElementDecl[]{
			new ElementDecl(){
				public String getName() { return "$$any$$"; }
				public Object getProperty(String propertyName)
								throws SAXNotRecognizedException {
					throw new SAXNotRecognizedException(propertyName);
				}
				public boolean getFeature(String featureName)
								throws SAXNotRecognizedException {
					throw new SAXNotRecognizedException(featureName);
				}
			} };
	
	private static final AttributesDecl[] theAttDecl = new AttributesDecl[]{
			new AttributesDecl(){
				public String getName() { return "$$any$$"; }
				public Object getProperty(String propertyName)
								throws SAXNotRecognizedException {
					throw new SAXNotRecognizedException(propertyName);
				}
				public boolean getFeature(String featureName)
								throws SAXNotRecognizedException {
					throw new SAXNotRecognizedException(featureName);
				}
			} };
	
	public ElementDecl getElementDeclByName( String name ) {
		return theElemDecl[0];
	}
	public ElementDecl[] getElementDecls() {
		return theElemDecl;
	}
	public Iterator iterateElementDecls() {
		Vector vec = new Vector();
		vec.add(theElemDecl[0]);
		return vec.iterator();
	}
	
	public IslandVerifier createNewVerifier( String namespaceURI, ElementDecl[] rules ) {
		return new IgnoreVerifier(namespaceURI,rules);
	}
	
	public AttributesDecl getAttributesDeclByName( String name ) {
		return theAttDecl[0];
	}
	public AttributesDecl[] getAttributesDecls() {
		return theAttDecl;
	}
	public Iterator iterateAttributesDecls() {
		Vector vec = new Vector();
		vec.add(theAttDecl[0]);
		return vec.iterator();
	}

	public AttributesVerifier createNewAttributesVerifier(
						String namespaceURI, AttributesDecl[] decls ) {
		throw new Error("not implemented yet");
	}

	
	
	public void bind( SchemaProvider provider, ErrorHandler handler ) {}
	
}
