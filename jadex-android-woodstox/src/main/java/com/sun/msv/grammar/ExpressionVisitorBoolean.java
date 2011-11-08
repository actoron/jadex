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
 * ExpressionVisitor that returns boolean.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface ExpressionVisitorBoolean {
    
    boolean onAttribute( AttributeExp exp );
    boolean onChoice( ChoiceExp exp );
    boolean onElement( ElementExp exp );
    boolean onOneOrMore( OneOrMoreExp exp );
    boolean onMixed( MixedExp exp );
    boolean onList( ListExp exp );
    boolean onRef( ReferenceExp exp );
    boolean onOther( OtherExp exp );
    boolean onEpsilon();
    boolean onNullSet();
    boolean onAnyString();
    boolean onSequence( SequenceExp exp );
    boolean onData( DataExp exp );
    boolean onValue( ValueExp exp );
    boolean onConcur( ConcurExp p );
    boolean onInterleave( InterleaveExp p );
}
