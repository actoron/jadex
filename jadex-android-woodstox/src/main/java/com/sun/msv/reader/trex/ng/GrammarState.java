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


/**
 * parses &lt;grammar&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarState extends com.sun.msv.reader.trex.GrammarState {
    protected void startSelf() {
        super.startSelf();
        
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        // memorize this reference as a direct reference.
        if( reader.currentNamedPattern!=null ) {
            if(reader.directRefernce)
                reader.currentNamedPattern.directRefs.add(newGrammar);
            else
                reader.currentNamedPattern.indirectRefs.add(newGrammar);
        }
    }
}
