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

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.msv.reader.relax.HedgeRuleBaseState;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;topLevel&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TopLevelState extends HedgeRuleBaseState
{
    protected void endSelf( Expression contentModel ) {
        ((RELAXNSReader)reader).grammar.topLevel = contentModel;
    }

    protected State createChildState( StartTagInfo tag )
    {
        // user tends to forget to specify RELAX Core namespace for
        // topLevel elements. see if this is the case
        if( tag.namespaceURI.equals(RELAXNSReader.RELAXNamespaceNamespace))
        {// bingo.
            reader.reportError( RELAXNSReader.ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE );
            // return null so that user will also receive "malplaced element" error.
            return null;
        }
        
        return super.createChildState(tag);
    }

    protected boolean isGrammarElement( StartTagInfo tag ) {
        // children of <topLevel> must be RELAXCore.
        if( tag.namespaceURI.equals(RELAXCoreReader.RELAXCoreNamespace) )
            return true;
        
        // for better error message, allow RELAX Namespace elements.
        // this error is handled at createChildState method.
        if( tag.namespaceURI.equals(RELAXNSReader.RELAXNamespaceNamespace) )
            return true;
        
        return false;
    }
}
