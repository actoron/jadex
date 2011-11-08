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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.relax.HedgeRules;
import com.sun.msv.reader.relax.HedgeRuleBaseState;

/**
 * parses &lt;hedgeRule&gt; element
 * 
 * this class is used as the base class of TopLevelState
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class HedgeRuleState extends HedgeRuleBaseState
{
    protected void endSelf( Expression contentModel )
    {
        final String label = startTag.getAttribute("label");
        if( label==null )
        {
            reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "hedgeRule", "label" );
            return;    // recover by ignoring this hedgeRule
        }
        
        final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        
        HedgeRules hr = reader.module.hedgeRules.getOrCreate(label);
        reader.setDeclaredLocationOf(hr); // remember where this hedgeRule is defined.
        hr.addHedge(contentModel,reader.pool);
    }
}
