/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.util;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.ExpressionVisitorBoolean;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

/**
 * Base class for "finding" something from an expression.
 * 
 * This class visits all reachable expressions and returns boolean.
 * 
 * In any binary expression, if one branch returns true, then the binary
 * expression itself returns true. Thus it can be used to find something
 * from an expression.
 * 
 * Note that unless the derived class do something, this implementation
 * will recurse infinitely.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionFinder implements ExpressionVisitorBoolean
{
    public boolean onSequence( SequenceExp exp )        { return exp.exp1.visit(this)||exp.exp2.visit(this); }
    public boolean onInterleave( InterleaveExp exp )    { return exp.exp1.visit(this)||exp.exp2.visit(this); }
    public boolean onConcur( ConcurExp exp )            { return exp.exp1.visit(this)||exp.exp2.visit(this); }
    public boolean onChoice( ChoiceExp exp )            { return exp.exp1.visit(this)||exp.exp2.visit(this); }
    public boolean onAttribute( AttributeExp exp )        { return exp.exp.visit(this); }
    public boolean onElement( ElementExp exp )            { return exp.contentModel.visit(this); }
    public boolean onOneOrMore( OneOrMoreExp exp )        { return exp.exp.visit(this); }
    public boolean onMixed( MixedExp exp )                { return exp.exp.visit(this); }
    public boolean onList( ListExp exp )                { return exp.exp.visit(this); }
    public boolean onRef( ReferenceExp exp )            { return exp.exp.visit(this); }
    public boolean onOther( OtherExp exp )                { return exp.exp.visit(this); }
    public boolean onEpsilon()                            { return false; }
    public boolean onNullSet()                            { return false; }
    public boolean onAnyString()                        { return false; }
    public boolean onData( DataExp exp )                { return false; }
    public boolean onValue( ValueExp exp )                { return false; }
}
