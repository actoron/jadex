/*
 * @(#)$Id$
 *
 * Copyright 2001 KAWAGUCHI Kohsuke
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

import java.util.Iterator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * represents a schema that validates one island.
 * 
 * @author
 *		<a href="mailto:k-kawa@bigfoot.com">Kohsuke KAWAGUCHI</a>
 */
public interface IslandSchema {
	/**
	 * creates a new IslandVerifier instance that is going to validate
	 * one island.
	 * 
	 * @param namespaceURI
	 *		namespace URI of the newly found element, which is going to be
	 *		validated by the newly created IslandVerifier.
	 * @param elementDecls
	 *		set of ElementDecl objects that newly created verifier shall validate.
	 */
	IslandVerifier createNewVerifier( String namespaceURI, ElementDecl[] elementDecls );
	
	/**
	 * gets exported elementDecl object that has specified name.
	 * 
	 * @return null
	 *		if no elementDecl is exported under the given name.
	 */
	ElementDecl getElementDeclByName( String name );
	
	/** iterates all exported elementDecl objects. */
	Iterator iterateElementDecls();
	
	/** returns all exported elementDecl objects at once. */
	ElementDecl[] getElementDecls();
	
	/**
	 * gets exported AttributesDecl object that has specified name.
	 * 
	 * @return null
	 *		if no AttributesDecl is exported under the given name.
	 */
	AttributesDecl getAttributesDeclByName( String name );
	
	/** iterates all exported attributesDecl objects. */
	Iterator iterateAttributesDecls();
	
	/** returns all exported attributesDecl objects at once. */
	AttributesDecl[] getAttributesDecls();
	
	/**
	 * creates a new AttributesVerifier instance that is going to validate
	 * attribute declarations.
	 * 
	 * @param namespaceURI
	 *		namespace URI of the attributes, which is going to be
	 *		validated by the newly created verifier.
	 * @param decls
	 *		set of AttributesDecl objects that newly created verifier shall validate.
	 */
	AttributesVerifier createNewAttributesVerifier( String namespaceURI, AttributesDecl[] decls );
	
	/**
	 * binds references to imported elementDecls by using given provider.
	 * 
	 * this method is only called once before the first validation starts.
	 * 
	 * @exception SAXException
	 *		any error has to be reported to ErrorHandler first.
	 */
	void bind( SchemaProvider provider, ErrorHandler errorHandler )
		throws SAXException;
}
