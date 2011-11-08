/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.util;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;

/**
 * Abstract implementation of a function
 * <pre>
 *   NameClass x NameClass ->  boolean
 * </pre>
 * 
 * Override the probe method to define the actual function.
 *
 * 
 * <p>
 * To compute, create an instance and call the check method. This class is not
 * reentrant, so the caller is responsible not to reuse the same object by multiple
 * threads.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClassComparator implements NameClassVisitor {
            
    /** Two name classes to be tested. */
    protected NameClass nc1,nc2;
            
    /**
     * This exception will be thrown when a collision is found.
     */
    protected final RuntimeException eureka = new RuntimeException();
            
    /**
     * Returns true if two name classes collide.
     */
    public boolean check( NameClass _new, NameClass _old ) {
                
        try {
            nc1 = _new;
            nc2 = _old;
            _old.visit(this);
            _new.visit(this);
            return false;
        } catch( RuntimeException e ) {
            if(e==eureka)   return true;    // the collision was found.
            throw e;
        }
    }
            
    /**
     * Throw <code>eureka</code> to return true from the probe method.
     */
    protected abstract void probe( String uri, String local );
            
    private /*static*/ final String MAGIC = "\u0000";
            
    public Object onAnyName( AnyNameClass nc ) {
        probe(MAGIC,MAGIC);
        return null;
    }
    public Object onNsName( NamespaceNameClass nc ) {
        probe(nc.namespaceURI,MAGIC);
        return null;
    }
    public Object onSimple( SimpleNameClass nc ) {
        probe(nc.namespaceURI,nc.localName);
        return null;
    }
    public Object onNot( NotNameClass nc ) {
        nc.child.visit(this);
        return null;
    }
    public Object onDifference( DifferenceNameClass nc ) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }
    public Object onChoice( ChoiceNameClass nc ) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }
}