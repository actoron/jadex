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

import com.sun.msv.grammar.ElementExp;

/**
 * special Token that matchs any element.
 * 
 * this token is used only for error recovery, to compute
 * "residual of elements of concern"(EoCR).
 * 
 * EoCR is defined as follows
 * 
 * <PRE>
 * EoCR(exp) := exp/e1 | exp/e2 | ... | exp/en
 * 
 * {ei} = elements of concern
 * exp/ei = residual(exp,ei)
 * '|' represents choice
 * </PRE>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class AnyElementToken extends ElementToken
{
    /**
     * use this singleton instance instead of creating an object.
     */
    public static final Token theInstance = new AnyElementToken();
    private AnyElementToken(){ super(null); }
    public boolean match( ElementExp exp ) { return true; }
}
