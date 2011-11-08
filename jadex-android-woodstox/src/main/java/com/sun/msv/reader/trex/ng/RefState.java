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

import com.sun.msv.grammar.ReferenceExp;

/**
 * parses &lt;ref&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RefState extends com.sun.msv.reader.trex.RefState {
    
    public RefState( boolean parentRef ) {
        super(parentRef);
    }
    
    /**
     * Performs the final wrap-up.
     */
    protected void wrapUp( ReferenceExp r ) {
        super.wrapUp(r);
        
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        // memorize this reference as a direct reference.
        if( reader.currentNamedPattern!=null ) {
            if(reader.directRefernce)
                reader.currentNamedPattern.directRefs.add(r);
            else
                reader.currentNamedPattern.indirectRefs.add(r);
        }
    }

}
