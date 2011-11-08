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
 * computes regular expression derivative.
 * 
 * this class receives a regexp and computes the right language after eating
 * given token.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ResidualCalculator implements ExpressionVisitorExpression {
    protected Token token;
    protected final ExpressionPool pool;
    
    public ResidualCalculator( ExpressionPool pool )    { this.pool=pool; }
    
    /** compute the residual */
    final Expression calcResidual( Expression exp, ElementToken token ) {
        if( token.acceptedPatterns!=null && token.acceptedPatterns.length==1 ) {
            // we can use optimization table
            OptimizationTag ot;
            if(exp.verifierTag==null)
                exp.verifierTag = ot = new OptimizationTag();
            else {
                ot = (OptimizationTag)exp.verifierTag;
                Expression residual = (Expression)ot.simpleElementTokenResidual.get(token.acceptedPatterns[0]);
                if(residual!=null)
                    return residual;    // cache hit.
            }
            
            this.token = token;
            Expression residual = exp.visit(this);
            // cache this residual
            ot.simpleElementTokenResidual.put(token.acceptedPatterns[0],residual);
            return residual;
        }
        
        // no chance of using cache.
        this.token = token;
        return exp.visit(this);
    }
    
    /** compute the residual */
    public final Expression calcResidual( Expression exp, Token token ) {
        if( token instanceof ElementToken )
            return calcResidual(exp,(ElementToken)token);
        
        this.token=token;
        Expression r = exp.visit(this);

        // if token is ignorable, make expression as so.
        if( token.isIgnorable() )
            r = pool.createChoice(r,exp);
        
        return r;
    }
    
    public Expression onAttribute( AttributeExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                return Expression.nullSet;
    }
    public Expression onChoice( ChoiceExp exp ) {
        return pool.createChoice( exp.exp1.visit(this), exp.exp2.visit(this) );
    }
    public Expression onElement( ElementExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                return Expression.nullSet;
    }
    public Expression onOneOrMore( OneOrMoreExp exp ) {
        return pool.createSequence(
            exp.exp.visit(this),
            pool.createZeroOrMore(exp.exp) );
    }
    public Expression onMixed( MixedExp exp ) {
        // if token can be interpreted as anyString, eat it.
        if( token.matchAnyString() )    return exp;
        
        // otherwise, it must be consumed by the children.
        return pool.createMixed( exp.exp.visit(this) );
    }
    public Expression onEpsilon() {
        return Expression.nullSet;
    }
    public Expression onNullSet() {
        return Expression.nullSet;
    }
    public Expression onAnyString() {
        // anyString is not reduced to epsilon.
        // it remains there even after consuming StringToken.
        if(token.matchAnyString())        return Expression.anyString;
        else                            return Expression.nullSet;
    }
    public Expression onRef( ReferenceExp exp ) {
        return exp.exp.visit(this);
    }
    public Expression onOther( OtherExp exp ) {
        return exp.exp.visit(this);
    }
    public Expression onSequence( SequenceExp exp ) {
        Expression r = pool.createSequence( exp.exp1.visit(this), exp.exp2 );
        
        if( exp.exp1.isEpsilonReducible() )
            return pool.createChoice( r, exp.exp2.visit(this) );
        else
            return r;
    }
    
    public Expression onData( DataExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                    return Expression.nullSet;
    }
    
    public Expression onValue( ValueExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                    return Expression.nullSet;
    }
    
    public Expression onList( ListExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                    return Expression.nullSet;
    }
    
    public Expression onConcur( ConcurExp exp ) {
        return pool.createConcur(
            exp.exp1.visit(this), exp.exp2.visit(this) );
    }
    public Expression onInterleave( InterleaveExp exp ) {
        return pool.createChoice(
            pool.createInterleave( exp.exp1.visit(this), exp.exp2 ),
            pool.createInterleave( exp.exp1, exp.exp2.visit(this) ) );
    }
}
