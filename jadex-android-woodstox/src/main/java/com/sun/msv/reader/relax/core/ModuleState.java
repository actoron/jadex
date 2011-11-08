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

/**
 * parses &lt;module&gt; element.
 * 
 * this state is used to parse "head module", which is not included by
 * any other modules.
 * 
 * modules merged by include element are handled by MergeModuleState.
 * 
 * <p>
 * this class switchs RELAXReader.currentModule so that successive declarations
 * are placed in the proper module.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ModuleState extends ModuleMergeState
{
    protected ModuleState(String expectedNamespace) { super(expectedNamespace); }

    protected void startSelf()
    {
        super.startSelf();
        // switch current module to new one.
        getReader().module = new RELAXModule(reader.pool,targetNamespace);
    }
}
