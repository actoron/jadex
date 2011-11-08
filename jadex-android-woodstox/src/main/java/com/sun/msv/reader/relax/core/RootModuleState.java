/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core;

import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * invokes State object that parses the document element.
 * 
 * this state is used to parse RELAX module referenced by RELAX Namespace.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class RootModuleState extends SimpleState {
    protected final String expectedNamespace;
    
    RootModuleState( String expectedNamespace ) {
        this.expectedNamespace = expectedNamespace;
    }
    
    protected State createChildState( StartTagInfo tag ) {
        if(tag.namespaceURI.equals(RELAXCoreReader.RELAXCoreNamespace)
        && tag.localName.equals("module"))
            return new ModuleState(expectedNamespace);
        
        return null;
    }
    
    // module wrap-up.
    protected void endSelf() {
        
        final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        reader.wrapUp();
        
        super.endSelf();
    }
}
