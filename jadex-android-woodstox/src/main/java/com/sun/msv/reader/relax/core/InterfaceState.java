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

import com.sun.msv.grammar.relax.RELAXModule;
import com.sun.msv.reader.ChildlessState;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;interface&gt; element and &lt;div&gt; in interface.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class InterfaceState extends SimpleState
{
    protected State createChildState( StartTagInfo tag )
    {
        if(!tag.namespaceURI.equals(RELAXCoreReader.RELAXCoreNamespace))    return null;
        
        if(tag.localName.equals("div"))        return new InterfaceState();
        
        RELAXModule module = getReader().module;
        
        if(tag.localName.equals("export"))
        {
            final String label = tag.getAttribute("label");
            
            if(label!=null)
                module.elementRules.getOrCreate(label).exported = true;
            else
                reader.reportError(RELAXCoreReader.ERR_MISSING_ATTRIBUTE,
                                   "export", "label" );
                // recover by ignoring this export
            
            return new ChildlessState();
        }
        
        return null;
    }

    protected RELAXCoreReader getReader() { return (RELAXCoreReader)reader; }
}
