/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

import com.sun.msv.grammar.util.NameClassCollisionChecker;
import com.sun.msv.grammar.util.NameClassComparator;
import com.sun.msv.grammar.util.NameClassSimplifier;
import com.sun.msv.util.StringPair;

/**
 * validator of (namespaceURI,localPart) pair.
 * 
 * This is equivalent to RELAX NG's "name class".
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClass implements java.io.Serializable {
    /**
     * checks if this name class accepts given namespace:localName pair.
     * 
     * @param namespaceURI
     *        namespace URI to be tested. If this value equals to
     *        NAMESPACE_WILDCARD, implementation must assume that
     *        valid namespace is specified. this twist will be used for
     *        error diagnosis.
     * 
     * @param localName
     *        local part to be tested. As with namespaceURI, LOCALNAME_WILDCARD
     *        will acts as a wild card.
     * 
     * @return
     *        true if the pair is accepted,
     *        false otherwise.
     */
    public abstract boolean accepts( String namespaceURI, String localName );

    public final boolean accepts( StringPair name ) {
        return accepts( name.namespaceURI, name.localName );
    }
    
    /** Returns true if this name class is a superset of another name class. */
    public final boolean includes( NameClass rhs ) {
        boolean r = new NameClassComparator() {
            protected void probe(String uri, String local) {
                if( !nc1.accepts(uri,local) && nc2.accepts(uri,local) )
                    throw eureka;   // this is not a super-set!
            }
        }.check(this,rhs);
        
        return !r;
    }
    
    /** Returns true if this name class doesn't accept anything. */
    public boolean isNull() {
        return !new NameClassCollisionChecker().check(this,NameClass.ALL);
    }
    
    /**
     * Returns true if this name class represents the same set as the given name class.
     */
    public final boolean isEqualTo( NameClass rhs ) {
        boolean r = new NameClassComparator() {
            protected void probe(String uri, String local) {
                boolean a = nc1.accepts(uri,local);
                boolean b = nc2.accepts(uri,local);
                
                if( (a&&!b) || (!a&&b) )    throw eureka;
            }
        }.check(this,rhs);
        
        return !r;
    }

    /**
     * Computes the equivalent but simple name class.
     */
    public NameClass simplify() {
        return NameClassSimplifier.simplify(this);
    }
    
    
    /**
     * visitor pattern support
     */
    public abstract Object visit( NameClassVisitor visitor );
    
    /** wildcard should be accepted by any name class. */
    public static final String NAMESPACE_WILDCARD = "*";
    public static final String LOCALNAME_WILDCARD = "*";
    
    
    /** Computes the intersection of two name classes. */
    public static NameClass intersection( NameClass lhs, NameClass rhs ) {
        return NameClassSimplifier.simplify(
            new DifferenceNameClass( lhs, new NotNameClass(rhs) ) );
    }

    /** Computes the union of two name classes. */
    public static NameClass union( NameClass lhs, NameClass rhs ) {
        return NameClassSimplifier.simplify(
            new ChoiceNameClass(lhs,rhs) );
    }
    
    /** name class that accepts everything. */
    public static final NameClass ALL = new AnyNameClass();
    
    /** Name class that accepts nothing. */
    public static final NameClass NONE = new NotNameClass(ALL);

    
    // serialization support
    private static final long serialVersionUID = 1;    
}
