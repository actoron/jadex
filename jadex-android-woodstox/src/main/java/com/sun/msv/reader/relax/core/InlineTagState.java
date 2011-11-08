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

import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.relax.TagClause;

/**
 * parses &lt;tag&gt; element inlined in &lt;elementRule&gt;
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class InlineTagState extends ClauseState
{
    protected void endSelf()
    {
        super.endSelf();
        
        String name = startTag.getAttribute("name");
        
        if(name==null)
        {// then it defaults to the label of parent state
            
            name = parentState.getStartTag().getAttribute("label");
            if(name==null)    // label attribute of the parent itself defaults to role attribute.
                name = parentState.getStartTag().getAttribute("role");
            
            if(name==null)
                // this is an error of elementRule.
                // so user will receive an error by ElementRuleBaseState.
                // silently ignore this error here.
                name = "<undefined>";
        }
        
        
        if(!(parentState instanceof ElementRuleBaseState ))
            // inline element must be used as a child of elementRule
            throw new Error();    // assertion failed.
        
        TagClause c = new TagClause();
        
        c.nameClass = new SimpleNameClass(
            getReader().module.targetNamespace,
            name );
        c.exp = exp;    // exp holds a sequence of AttributeExp
        
        ((ElementRuleBaseState)parentState).onEndInlineClause(c);
        
        return;
    }
}
