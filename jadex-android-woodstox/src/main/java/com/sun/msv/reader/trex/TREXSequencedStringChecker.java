/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import java.util.Map;
import java.util.Set;

import com.sun.msv.grammar.AttributeExp;
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

/**
 * makes sure that there is no sequenced string.
 * 
 * "sequenced string" is something like this.
 * <XMP>
 * <oneOrMore>
 *   <string> abc </string>
 * </oneOrMore>
 * </XMP>
 * 
 * Also, TREX prohibits sequence of typed strings and elements.
 * 
 * <p>
 * In this checker, we introduce a function "f" that takes
 * a string and computes the string-sensitivity of the pattern.
 * 
 * <p>
 * "f" returns 3 bits of information. One is whether it contains
 * elements. Another is whehter it contains text. And the last is
 * whether it contains DataExp/ValueExp.
 * 
 * <p>
 * "f" is computed recursively through the pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXSequencedStringChecker implements ExpressionVisitor
{
    /**
     * If this flag is set to true, this class raises an error for
     * anyStrings in two branches of interleave.
     */
    private final boolean rejectTextInInterleave;
    
    /** integer pool implementation. */
    private static final Integer[] intPool = new Integer[]{
            new Integer(0),new Integer(1),new Integer(2),new Integer(3),
            new Integer(4),new Integer(5),new Integer(6),new Integer(7) };
    
    // 3 bit of information
    private static final int HAS_ELEMENT = 4;
    private static final int HAS_ANYSTRING = 2;
    private static final int HAS_DATA = 1; // data or value.
    
    
    private final TREXBaseReader reader;
    
    public TREXSequencedStringChecker( TREXBaseReader reader, boolean _rejectTextInInterleave ) {
        this.reader = reader;
        this.rejectTextInInterleave = _rejectTextInInterleave;
    }

    /**
     * set of checked Expressions.
     * 
     * once an ElementExp/AttributeExp is checked, it will be added to this set.
     * this set is used to prevent infinite recursion.
     */
    private final Set<Expression> checkedExps = new java.util.HashSet<Expression>();
    
    /**
     * set of checked ReferenceExps.
     * 
     * Once a ReferenceExp is checked, it will be added (with its result)
     * to this map. This is useful to speed up the check.
     */
    private final Map<ReferenceExp,Object> checkedRefExps = new java.util.HashMap<ReferenceExp,Object>();
    
    public Object onRef( ReferenceExp exp ) {
        Object r = checkedRefExps.get(exp);
        if(r!=null) return r;
        checkedRefExps.put(exp, r=exp.exp.visit(this) );
        return r;
    }
    public Object onOther( OtherExp exp ) {
        return exp.exp.visit(this);
    }
    
    public Object onInterleave( InterleaveExp exp ) {
        Object l = exp.exp1.visit(this);
        Object r = exp.exp2.visit(this);
        
        if(isError(l,r)) {
            // where is the source of error?
            reader.reportError( TREXBaseReader.ERR_INTERLEAVED_STRING );
            return intPool[0];
        }
        if( rejectTextInInterleave
        &&  (toInt(l)&HAS_ANYSTRING)!=0
        &&  (toInt(r)&HAS_ANYSTRING)!=0 ) {
            reader.reportError( TREXBaseReader.ERR_INTERLEAVED_ANYSTRING );
            return intPool[0];
        }
        
        return merge(l,r);
    }
    
    public Object onSequence( SequenceExp exp ) {
        Object l = exp.exp1.visit(this);
        Object r = exp.exp2.visit(this);
        
        if(isError(l,r)) {
            // where is the source of error?
            reader.reportError( TREXBaseReader.ERR_SEQUENCED_STRING );
            return intPool[0];
        }
        
        return merge(l,r);
    }
    
    public Object onEpsilon() { return intPool[0]; }
    public Object onNullSet() { return intPool[0]; }
    public Object onData( DataExp exp )        { return intPool[HAS_DATA]; }
    public Object onValue( ValueExp exp )    { return intPool[HAS_DATA]; }
    // do not traverse contents of list.
    public Object onList( ListExp exp )        { return intPool[HAS_DATA]; }
    public Object onAnyString()                { return intPool[HAS_ANYSTRING]; }
    
    public Object onAttribute( AttributeExp exp ) {
        if( checkedExps.add(exp) )
            exp.exp.visit(this);
        return intPool[0];
    }
    
    public Object onElement( ElementExp exp ) {
        if( checkedExps.add(exp) )
            // if this is the first visit
            // this has to be done before checking content model
            // otherwise it leads to the infinite recursion.
            exp.contentModel.visit(this);
        return intPool[HAS_ELEMENT];
    }
    
    private static final int toInt( Object o ) { return ((Integer)o).intValue(); }
    
    private static Object merge( Object o1, Object o2 ) {
        return intPool[toInt(o1)|toInt(o2)];
    }
    /**
     * It is an error if a pattern with data is combined to other patterns.
     */
    private static boolean isError( Object o1, Object o2 ) {
        return (toInt(o1)&HAS_DATA)!=0 && toInt(o2)!=0
            || (toInt(o2)&HAS_DATA)!=0 && toInt(o1)!=0;
    }
    
    public Object onChoice( ChoiceExp exp ) {
        return merge( exp.exp1.visit(this), exp.exp2.visit(this) );
    }
    
    public Object onConcur( ConcurExp exp ) {
        return merge( exp.exp1.visit(this), exp.exp2.visit(this) );
    }
    
    public Object onOneOrMore( OneOrMoreExp exp ) {
        Object o = exp.exp.visit(this);
        if( (toInt(o)&HAS_DATA) !=0 ) {
            reader.reportError(TREXBaseReader.ERR_REPEATED_STRING);
            return intPool[0];
        }
        return o;
    }
    
    public Object onMixed( MixedExp exp ) {
        Object o = exp.exp.visit(this);
        
        if( rejectTextInInterleave
        &&  (toInt(o)&HAS_ANYSTRING)!=0 ) {
            reader.reportError( TREXBaseReader.ERR_INTERLEAVED_ANYSTRING );
            return intPool[0];
        }
        
        return merge(o,intPool[HAS_ANYSTRING]);
    }

}
