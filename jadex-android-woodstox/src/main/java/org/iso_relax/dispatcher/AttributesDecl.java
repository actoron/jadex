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
package org.iso_relax.dispatcher;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * represents a constraint for XML attributes.
 * 
 * This interface also provides feature/property mechanism to encourage
 * communications between two different implementations.
 * 
 * @author		<a href="mailto:k-kawa@bigfoot.com">Kohsuke KAWAGUCHI</a>
 */
public interface AttributesDecl {
	/**
	 * gets name of this rule.
	 * every AttributesDecl has a unique name within the schema.
	 */
	String getName();
	
	/** looks up the value of a feature
	 * 
	 * this method works like getFeature method of SAX.
	 * featureName is a fully-qualified URI.
	 * 
	 * Implementators are encouraged to invent their own features,
	 * by using their own URIs.
	 */
	boolean getFeature( String featureName )
		throws SAXNotRecognizedException,SAXNotSupportedException;
	
	/** looks up the value of a property
	 * 
	 * this method works like getProperty method of SAX.
	 * propertyName is a fully-qualified URI.
	 * 
	 * Implementators are encouraged to invent their own properties,
	 * by using their own URIs.
	 */
	Object getProperty( String propertyName )
		throws SAXNotRecognizedException,SAXNotSupportedException;
}
