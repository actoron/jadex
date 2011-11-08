/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import org.relaxng.datatype.ValidationContext;

/** base class of FloatType and DoubleType
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class FloatingNumberType extends BuiltinAtomicType implements Comparator {
    protected FloatingNumberType( String typeName ) { super(typeName); }
    
    final protected boolean checkFormat( String lexicalValue, ValidationContext context ) {
        // FloatType and DoubleType checks format by trying to convert it to value object
        return _createValue(lexicalValue,context)!=null;
    }
    
    protected static boolean isDigitOrPeriodOrSign( char ch ) {
        if( '0'<=ch && ch<='9' )    return true;
        if( ch=='+' || ch=='-' || ch=='.' )    return true;
        return false;
    }
    
    public final int compare( Object lhs, Object rhs ) {
        // float and double type has total order.
        // implementation of Float.compareTo/Double.compareTo is
        // consistent with the spec
        int r = ((Comparable)lhs).compareTo(rhs);
        if(r<0)    return -1;
        if(r>0)    return +1;
        return 0;
    }

    public final int isFacetApplicable( String facetName ) {
        // TODO : should we allow scale facet, or not?
        if( facetName.equals(FACET_PATTERN)
        ||    facetName.equals(FACET_ENUMERATION)
        ||  facetName.equals(FACET_WHITESPACE)
        ||    facetName.equals(FACET_MAXINCLUSIVE)
        ||    facetName.equals(FACET_MININCLUSIVE)
        ||    facetName.equals(FACET_MAXEXCLUSIVE)
        ||    facetName.equals(FACET_MINEXCLUSIVE) )
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }

    private static final long serialVersionUID = -224134863141700384L;
}
