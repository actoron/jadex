/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.reader.SimpleState;

/**
 * Base implementation for NameClass primitives
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClassState extends SimpleState {
    public final void endSelf() {
        // pass the pattern to the parent
        ((NameClassOwner)parentState).onEndChild(makeNameClass());
        super.endSelf();
    }
        
    /**
     * This method is called from endElement method.
     * Implementation has to provide NameClass object that represents the content of
     * this element.
     */
    protected abstract NameClass makeNameClass();
    
    protected final String getPropagatedNamespace() {
        return ((TREXBaseReader)reader).targetNamespace;
    }
}
