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

import java.util.Set;

import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.util.StringPair;

/**
 * Special name class implementation used for the wild card of the "lax" mode.
 * 
 * <p>
 * In "lax" mode, we need a name class that matches all undefined names.
 * Although it is possible to use DifferenceNameClass for this purpose,
 * it is not a cost-efficient way because typically it becomes very large.
 * (If there are twenty element declarations, we'll need twenty DifferenceNameClass
 * to exclude all defined names).
 * 
 * <p>
 * This name class uses a {@link Set} to hold multiple names. If a name
 * is contained in that set, it'll be rejected. If a name is not contained,
 * it'll be accepted.
 * 
 * <p>
 * Special care is taken to make this NC as seamless as possible.
 * When the visit method is called, the equivalent name class is constructed
 * internally and the visitor will visit that name class. In this way, the visitors
 * won't notice the existance of this "special" name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LaxDefaultNameClass extends NameClass {
    
    /**
     * @param _base
     *        this name class accepts a name if
     *        <ol>
     *         <li>it's in the 'base" name class and
     *         <li>it's not one of those excluded names
     */
    public LaxDefaultNameClass( NameClass _base ) {
        this.base = _base;
        names.add( new StringPair(NAMESPACE_WILDCARD,LOCALNAME_WILDCARD) );
    }
    
    private NameClass base;
    
    public Object visit( NameClassVisitor visitor ) {
        // create equivalent name class and let visitor visit it.
        if( equivalentNameClass==null ) {
            NameClass nc = base;
            StringPair[] items = (StringPair[])names.toArray(new StringPair[0]);
            for( int i=0; i<items.length; i++ ) {
                if( items[i].namespaceURI==NAMESPACE_WILDCARD
                 || items[i].localName==LOCALNAME_WILDCARD )
                    continue;
                
                nc = new DifferenceNameClass(nc,
                    new SimpleNameClass(items[i]));
            }
            equivalentNameClass = nc;
        }
        
        return equivalentNameClass.visit(visitor);
    }
    
    /**
     * equivalent name class by conventional primitives.
     * Initially null, and created on demand.
     */
    protected NameClass equivalentNameClass;
    
    public boolean accepts( String namespaceURI, String localName ) {
        return base.accepts(namespaceURI,localName) &&
                !names.contains( new StringPair(namespaceURI,localName) );
    }
    
    /**
     * set of {@link StringPair}s.
     * each item represents one name.
     * it also contains WILDCARD as entry.
     */
    private final Set<StringPair> names = new java.util.HashSet<StringPair>();
    
    /**
     * add a name so that this name will be rejected by the accepts method.
     */
    public void addName( String namespaceURI, String localName ) {
        names.add( new StringPair(namespaceURI,localName) );
        names.add( new StringPair(namespaceURI,LOCALNAME_WILDCARD) );
        names.add( new StringPair(NAMESPACE_WILDCARD,localName) );
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
