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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.BinaryExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionVisitor;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.verifier.Acceptor;

/**
 * calculates how character literals should be treated.
 * 
 * This class is thread-safe: multiple threads can simultaneously
 * access the same instance. Note that there is no guarantee that the
 * derived class is thread-safe.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StringCareLevelCalculator implements ExpressionVisitor {

    protected StringCareLevelCalculator(){}

    /** singleton instance. */
    protected static final StringCareLevelCalculator theInstance = new StringCareLevelCalculator();

    // those expressions which are sensitive about string must return true
    public Object onAttribute( AttributeExp exp )        { return NO_STRING; }
    public Object onElement( ElementExp exp )            { return NO_STRING; }
    public Object onMixed( MixedExp exp )                { return ANY_STRING; }
    public Object onList( ListExp exp )                { return SOME_STRING; }
    public Object onAnyString()                        { return ANY_STRING; }
    public Object onData( DataExp exp ) {
        if(exp.except==Expression.nullSet && exp.dt instanceof XSDatatype) {
            XSDatatype xdt = (XSDatatype)exp.dt;
            if(xdt.isAlwaysValid())
                return ANY_STRING;
        }
        return SOME_STRING;
    }
    public Object onValue( ValueExp exp )                { return SOME_STRING; }

    public Object onChoice(ChoiceExp exp) {
        return doChoice(exp);
    }

    private Object doChoice(BinaryExp exp) {
        Object lhs = exp.exp1.visit(this);
        Object rhs = exp.exp2.visit(this);
        if(lhs==ANY_STRING && rhs==ANY_STRING)  return ANY_STRING;
        if(lhs==NO_STRING  && rhs==NO_STRING)   return NO_STRING;
        return SOME_STRING;
    }

    public Object onOneOrMore(OneOrMoreExp exp) {
        return exp.exp.visit(this);
    }

    public Object onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    public Object onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    public Object onEpsilon() {
        return NO_STRING;
    }

    public Object onNullSet() {
        return NO_STRING;
    }

    public Object onSequence(SequenceExp exp) {
        if(!exp.exp1.isEpsilonReducible())
            return exp.exp1.visit(this);
        else
            return doChoice(exp);
    }

    public Object onConcur(ConcurExp exp) {
        Object lhs = exp.exp1.visit(this);
        Object rhs = exp.exp2.visit(this);
        if(lhs==ANY_STRING && rhs==ANY_STRING)  return ANY_STRING;
        if(lhs==NO_STRING  || rhs==NO_STRING)   return NO_STRING;
        return SOME_STRING;
    }

    public Object onInterleave(InterleaveExp p) {
        return doChoice(p);
    }

    public static int calc( Expression exp ) {
        Object r = exp.visit(theInstance);

        if(r==ANY_STRING)
            return Acceptor.STRING_IGNORE;
        if(r==NO_STRING)
            return Acceptor.STRING_PROHIBITED;

        return Acceptor.STRING_STRICT;
    }

    private static final String ANY_STRING = "anyString";
    private static final String NO_STRING = "noString";
    private static final String SOME_STRING = "someString";
}
