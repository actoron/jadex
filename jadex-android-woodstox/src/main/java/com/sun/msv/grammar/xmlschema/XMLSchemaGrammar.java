/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

import java.util.Iterator;
import java.util.Map;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;

/**
 * set of XML Schema. This set can be used to validate a document.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XMLSchemaGrammar implements Grammar {

    public XMLSchemaGrammar() {
        this( new ExpressionPool() );
    }
    
    public XMLSchemaGrammar( ExpressionPool pool ) {
        this.pool = pool;
    }
    
    /** pool object which was used to construct this grammar. */
    protected final ExpressionPool pool;
    public final ExpressionPool getPool() {
        return pool;
    }
    
    public Expression topLevel;
    public final Expression getTopLevel() {
        return topLevel;
    }

    /** map from namespace URI to loaded XMLSchemaSchema object. */
    protected final Map<String, Object> schemata = new java.util.HashMap<String, Object>();
    
    /** gets XMLSchemaSchema object that has the given target namespace.
     * 
     * @return null if no schema is associated with that namespace.
     */
    public XMLSchemaSchema getByNamespace( String targetNamesapce ) {
        return (XMLSchemaSchema)schemata.get(targetNamesapce);
    }
    
    /**
     * returns an Iterator that enumerates XMLSchemaSchema objects
     * that are defined in this grammar.
     */
    public Iterator<Object> iterateSchemas() {
        return schemata.values().iterator();
    }
    
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
