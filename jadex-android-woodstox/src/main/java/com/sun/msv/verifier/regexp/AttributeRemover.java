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
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;

/**
 * Creates an expression whose AttributeExp is completely replaced by epsilon.
 * 
 * This step is used to erase all unconsumed AttributeExp from the expression.
 * This class is used for error recovery. Usually, unconsumed attributes
 * indicates a violation of the validity.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeRemover extends ExpressionCloner
{
    public AttributeRemover( ExpressionPool pool ) { super(pool); }
    
    public Expression onAttribute( AttributeExp exp )    { return Expression.epsilon; }
    public Expression onRef( ReferenceExp exp )            { return exp.exp.visit(this); }
    public Expression onOther( OtherExp exp )            { return exp.exp.visit(this); }
    public Expression onElement( ElementExp exp )        { return exp; }
}
