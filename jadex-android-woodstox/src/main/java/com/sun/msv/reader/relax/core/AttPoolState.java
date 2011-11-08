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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.relax.AttPoolClause;

/**
 * parses &lt;attPool&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttPoolState extends ClauseState {
    
    protected void endSelf( ) {
    
        super.endSelf();
        
        final String role = startTag.getAttribute("role");
        if(role==null) {
            reader.reportError(RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "attPool","role");
            return;    // recover by ignoring this declaration
        }
        
        if( startTag.getAttribute("combine")==null ) {
            AttPoolClause c = getReader().module.attPools.getOrCreate(role);
        
            if(c.exp!=null) {
                // someone has already initialized this clause.
                // this happens when more than one attPool element declares the same role.
                reader.reportError(
                    new Locator[]{getReader().getDeclaredLocationOf(c),location},
                    RELAXCoreReader.ERR_MULTIPLE_ATTPOOL_DECLARATIONS, new Object[]{role} );
                // recover from error by ignoring previous tag declaration
            }
        
            c.exp = exp;    // exp holds a sequence of AttributeExp
            getReader().setDeclaredLocationOf(c);    // remember where this AttPool is declared
        } else {
            // this attPool has @combine
            
            ReferenceExp e = getReader().combinedAttPools._getOrCreate(role);
            if( e.exp==null )    e.exp = Expression.epsilon;
            // append newly found attributes.
            e.exp = reader.pool.createSequence( exp, e.exp );
            reader.setDeclaredLocationOf(e);
        }
        
        
        return;
    }
}
