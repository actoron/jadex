/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

/**
 * ExpressionVisitor that returns Expression object.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface ExpressionVisitorExpression {
    
    Expression onAttribute( AttributeExp exp );
    Expression onChoice( ChoiceExp exp );
    Expression onElement( ElementExp exp );
    Expression onOneOrMore( OneOrMoreExp exp );
    Expression onMixed( MixedExp exp );
    Expression onList( ListExp exp );
    Expression onRef( ReferenceExp exp );
    Expression onOther( OtherExp exp );
    Expression onEpsilon();
    Expression onNullSet();
    Expression onAnyString();
    Expression onSequence( SequenceExp exp );
    Expression onData( DataExp exp );
    Expression onValue( ValueExp exp );
    Expression onConcur( ConcurExp p );
    Expression onInterleave( InterleaveExp p );
}
