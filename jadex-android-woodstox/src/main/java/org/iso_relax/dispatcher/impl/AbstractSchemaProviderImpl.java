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
import java.util.Map;

import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.SchemaProvider;

/**
 * default implementation of SchemaProvider.
 * 
 * Applications can use this class as the base class of their own SchemaProvider.
 * 
 * @author		<a href="mailto:k-kawa@bigfoot.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class AbstractSchemaProviderImpl implements SchemaProvider {
	
	/** a map from primary namespace to IslandSchema. */
	protected final Map schemata = new java.util.HashMap();
	
	/** adds a new IslandSchema.
	 *
	 * the caller should make sure that the given uri is not defined already.
	 */
	public void addSchema( String uri, IslandSchema s ) {
		if( schemata.containsKey(uri) )
			throw new IllegalArgumentException();
		schemata.put( uri, s );
	}
			 
	public IslandSchema getSchemaByNamespace( String uri ) {
		return (IslandSchema)schemata.get(uri);
	}
	
	public Iterator iterateNamespace() {
		return schemata.keySet().iterator();
	}
	
	public IslandSchema[] getSchemata() {
		IslandSchema[] r = new IslandSchema[schemata.size()];
		schemata.values().toArray(r);
		return r;
	}
}
