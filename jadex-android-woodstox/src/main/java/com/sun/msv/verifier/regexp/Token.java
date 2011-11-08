/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.ValueExp;

/**
 * primitive unit of XML instance.
 * 
 * this object is fed to expression.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class Token
{
    /** returns true if the given ElementExp can consume this token  */
    public boolean match( ElementExp p )        { return false;    }
    public boolean match( AttributeExp p )        { return false; }
    /** returns true if the given DataExp can consume this token */
    public boolean match( DataExp p )            { return false; }
    public boolean match( ValueExp p )            { return false; }
    /** returns true if the given ListExp can consume this token */
    public boolean match( ListExp p )            { return false; }
    
    /** returns true if anyString pattern can consume this token */
    public boolean matchAnyString()            { return false; }

    /** checks if this token is ignorable. */
    boolean isIgnorable() { return false; }
}
