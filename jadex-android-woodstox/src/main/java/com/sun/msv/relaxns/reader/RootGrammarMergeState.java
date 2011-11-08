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

import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * invokes State object that parses the document element.
 * 
 * this state is used for parsing included grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class RootGrammarMergeState extends SimpleState
{
    protected State createChildState( StartTagInfo tag ) {
        if(tag.localName.equals("grammar"))
            return new GrammarState();
        
        return null;
    }
}
