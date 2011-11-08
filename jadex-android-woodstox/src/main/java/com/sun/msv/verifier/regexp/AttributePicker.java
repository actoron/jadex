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
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

/**
 * removes all unnecessary expressions and
 * creates an expression that consists of required attributes and choices only.
 * 
 * <XMP>
 * For example,
 * 
 * <choice>
 *   <element />
 *   <attribute />
 * </choice>
 * 
 * will be converted to
 * 
 * <empty />
 * 
 * because no attribute is required. But
 * 
 * <choice>
 *   <attribute />
 *   <attribute />
 * </choice>
 * 
 * will remain the same because one or the other is required.
 * 
 * this method also removes SequenceExp.
 * 
 * <sequence>
 *   <attribute name="A" />
 *   <attribute name="B" />
 * </sequence>
 * 
 * will be converted to
 * 
 * <attribute name="A" />
 * 
 * This function object is used only for error recovery.
 * Resulting expressions always consist only of <choice>s and <attribute>s.
 * </XMP>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributePicker implements ExpressionVisitorExpression
{
    private final ExpressionPool pool;
    
    public AttributePicker( ExpressionPool pool ) {
        this.pool = pool;
    }
    
    public Expression onElement( ElementExp exp ) {
        return AttributeExp.epsilon;
    }
    
    public Expression onMixed( MixedExp exp ) {
        return exp.exp.visit(this);
    }
    
    public Expression onAnyString() {
        return Expression.epsilon;
    }
    
    public Expression onEpsilon() {
        return Expression.epsilon;
    }
    
    public Expression onNullSet() {
        return Expression.nullSet;
    }
    
    public Expression onRef( ReferenceExp exp ) {
        return exp.exp.visit(this);
    }
    public Expression onOther( OtherExp exp ) {
        return exp.exp.visit(this);
    }
    
    public Expression onData( DataExp exp ) {
        return Expression.epsilon;
    }
    public Expression onValue( ValueExp exp ) {
        return Expression.epsilon;
    }

    public Expression onList( ListExp exp ) {
        return Expression.epsilon;
    }

    public Expression onAttribute( AttributeExp exp ) {
        return exp;
    }
    
    public Expression onOneOrMore( OneOrMoreExp exp ) {
        // reduce A+ -> A
        return exp.exp.visit(this);
    }
    
    public Expression onSequence( SequenceExp exp ) {
        Expression ex1 = exp.exp1.visit(this);
        Expression ex2 = exp.exp2.visit(this);
        
        if(ex1.isEpsilonReducible()) {
            if(ex2.isEpsilonReducible())    return Expression.epsilon;
            else                            return ex2;
        }
        else
            return ex1;
    }
    
    public Expression onInterleave( InterleaveExp exp ) {
        Expression ex1 = exp.exp1.visit(this);
        Expression ex2 = exp.exp2.visit(this);
        
        if(ex1.isEpsilonReducible()) {
            if(ex2.isEpsilonReducible())    return Expression.epsilon;
            else                            return ex2;
        } else
            return ex1;
    }
    
    public Expression onConcur( ConcurExp exp ) {
        // abandon concur.
        return Expression.epsilon;
    }
    
    public Expression onChoice( ChoiceExp exp ) {
        Expression ex1 = exp.exp1.visit(this);
        Expression ex2 = exp.exp2.visit(this);
        // if one of choice is epsilon-reducible,
        // the entire choice becomes optional.
        // optional attributes have to be removed from the result.
        if( ex1.isEpsilonReducible() || ex2.isEpsilonReducible() )
            return Expression.epsilon;
        return pool.createChoice(ex1,ex2);
    }
}
