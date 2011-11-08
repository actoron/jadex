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
 * this state is used for parsing included module.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class RootModuleMergeState extends SimpleState
{
    protected State createChildState( StartTagInfo tag )
    {
        if(tag.namespaceURI.equals(RELAXCoreReader.RELAXCoreNamespace)
        && tag.localName.equals("module"))
            return new ModuleMergeState(
                ((RELAXCoreReader)reader).module.targetNamespace );
            // included module must have the same target namespace
            // or it must be a chameleon module.
        
        return null;
    }
}
