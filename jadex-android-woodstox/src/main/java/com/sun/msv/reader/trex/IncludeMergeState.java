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

import com.sun.msv.reader.AbortException;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * &lt;include&gt; element as an immediate child of &lt;grammar&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IncludeMergeState extends SimpleState {
    
    protected State createChildState( StartTagInfo tag ) {
        // no child element is allowed by default.
        return null;
    }
    
    protected void endSelf() {
        // For RELAX NG, inclusion has to be done at the endSelf method,
        // not in the startSelf method.
        final String href = startTag.getAttribute("href");

        if(href==null)
        {// name attribute is required.
            reader.reportError( TREXBaseReader.ERR_MISSING_ATTRIBUTE,
                "include","href");
            // recover by ignoring this include element
        }
        else
            try {
                // parse specified file
                TREXBaseReader reader = (TREXBaseReader) this.reader;
                reader.switchSource(this,href,reader.sfactory.includedGrammar());
            } catch( AbortException e ) {
                // recover by ignoring the error
            }
        
        super.endSelf();
    }
}
