/*
 * @(#)$Id$
 *
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.OtherExp;

/**
 * Used to mark a occurrence constraint which cannot
 * be easily represented by primitive expressions.
 * 
 * <p>
 * This expression is just a marker, and the exp field
 * of this instance still contains the precise expression
 * of the occurrence constraint.
 * 
 * <p>
 * For example, if A is maxOccurs=5 and minOccurs=3,
 * then the exp field of this instance will be:
 * <code>A,A,A,(A,A?)?</code>, the maxOccurs field
 * will be 5, the minOccurs field will be 3, and
 * the itemExp field will hold a reference to <code>A</code>.
 * 
 * <p>
 * Note that MSV doesn't using this marker by itself.
 * It is intended to help other applications that use
 * the AGM of MSV.
 * 
 * <p>
 * Also note that this expression will not
 * be used in the following cases to avoid excessive allocation
 * of this expression:
 * 
 * <ul>
 *  <li>when maxOccurs=unbounded and minOccurs is 1 or 0
 *  <li>when maxOccurs=1
 * </ul>
 * 
 * <p>
 * Those cases can be expressed quite nicely with existing primitives
 * So the client shouldn't find it difficuult to process them.
 * I appreciate any feedback on this issue.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class OccurrenceExp extends OtherExp {
    public OccurrenceExp(
        Expression preciseExp,
        int maxOccurs, int minOccurs, Expression itemExp ) {
        super(preciseExp);
        this.maxOccurs = maxOccurs;
        this.minOccurs = minOccurs;
        this.itemExp = itemExp;
    }
    
    /** Maximum occurence. -1 to indicate "unbounded" */
    public final int maxOccurs;
    /** Minimum occurence. */
    public final int minOccurs;
    
    /** The unit of repetition. */
    public final Expression itemExp;
    
    /** Obtains a string representation suitable for quick debugging. */
    public String toString() {
        return itemExp.toString()+"["+minOccurs+","+
            (maxOccurs==-1?"inf":String.valueOf(maxOccurs))+"]";
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
