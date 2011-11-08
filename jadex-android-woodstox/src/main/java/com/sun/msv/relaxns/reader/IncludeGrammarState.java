/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.reader;

import com.sun.msv.reader.AbortException;
import com.sun.msv.reader.ChildlessState;

/**
 * parses &lt;include&gt; element of RELAX Namespace.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IncludeGrammarState extends ChildlessState
{
    protected void startSelf()
    {
        super.startSelf();
    
        final String href = startTag.getAttribute("grammarLocation");

        if(href==null) {
            // name attribute is required.
            reader.reportError( RELAXNSReader.ERR_MISSING_ATTRIBUTE,
                "include","grammarLocation");
            // recover by ignoring this include element
        } else
            try {
                reader.switchSource(this,href,new RootGrammarMergeState());
            } catch( AbortException e ) {
                // recover by ignoring this include element
            }
    }
}
