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

import org.xml.sax.Locator;

import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.relax.TagClause;

/**
 * parses &lt;tag&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TagState extends ClauseState
{
    protected void endSelf( )
    {
        super.endSelf();
        
        final String name = startTag.getAttribute("name");
        String role = startTag.getAttribute("role");
        if(role==null)    role=name;    // role defaults to name
        
        if(name==null)
        {
            reader.reportError(RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "tag","name");
            return;
        }
        
        TagClause c = getReader().module.tags.getOrCreate(role);
        
        if(c.nameClass!=null)
        {
            // someone has already initialized this clause.
            // this happens when more than one tag element declares the same role.
            reader.reportError(
                new Locator[]{getReader().getDeclaredLocationOf(c),location},
                RELAXCoreReader.ERR_MULTIPLE_TAG_DECLARATIONS, new Object[]{role} );
            // recover from error by ignoring previous tag declaration
        }
        
        c.nameClass = new SimpleNameClass(
            getReader().module.targetNamespace,
            name );
        
        c.exp = exp;    // exp holds a sequence of AttributeExp
        getReader().setDeclaredLocationOf(c);    // remember where this tag is declared
        
        return;
    }
}
