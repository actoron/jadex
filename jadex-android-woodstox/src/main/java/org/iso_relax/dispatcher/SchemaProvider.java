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

import java.util.Iterator;

/**
 * provides necessary schema information for Dispatcher.
 * 
 * This interface can be implemented by applications.
 * 
 * @author <a href="mailto:k-kawa@bigfoot.com">Kohsuke KAWAGUCHI</a>
 */
public interface SchemaProvider {
	/**
	 * creates IslandVerifier that validates document element.
	 */
	IslandVerifier createTopLevelVerifier();
	
	/**
	 * gets IslandSchema whose primary namespace URI is the given value.
	 * 
	 * @return null
	 *		if no such IslandSchema exists.
	 */
	IslandSchema getSchemaByNamespace( String uri );
	
	/**
	 * iterates all namespaces that are registered in this object.
	 */
	Iterator iterateNamespace();
	
	/**
	 * returns all IslandSchemata at once.
	 */
	IslandSchema[] getSchemata();
}
