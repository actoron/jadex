/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.reader.State;
import com.sun.msv.reader.trex.NameClassWithChildState;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;anyName&gt; name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NGNameState extends NameClassWithChildState {
    
    NGNameState() {
        allowNullChild = true;
    }
    
    protected State createChildState( StartTagInfo tag ) {
        // <except> tag is allowed only once.
        if( super.nameClass==null && tag.localName.equals("except") )
            return ((RELAXNGReader)reader).getStateFactory().nsExcept(this,tag);
        return null;
    }
    
    protected NameClass castNameClass( NameClass halfCastedNameClass, NameClass newChildNameClass ) {
        // error check is done by the createChildState method.
        return newChildNameClass;
    }
    
    /**
     * performs final wrap-up and returns a fully created NameClass object
     * that represents this element.
     */
    protected NameClass annealNameClass( NameClass nameClass ) {
        NameClass r = getMainNameClass();
        if( nameClass!=null )
            r = new DifferenceNameClass( r, nameClass );
        return r;
    }
    
    /** this method should return the name class that is used as the base. */
    protected abstract NameClass getMainNameClass();
    
    /** Parsing state for &lt;anyName&gt; */
    public static class AnyNameState extends NGNameState {
        protected NameClass getMainNameClass() {
            return NameClass.ALL;
        }
    }
    
    /** Parsing state for &lt;nsName&gt; */
    public static class NsNameState extends NGNameState {
        protected NameClass getMainNameClass() {
            return new NamespaceNameClass( getPropagatedNamespace() );
        }
    }
    
}
