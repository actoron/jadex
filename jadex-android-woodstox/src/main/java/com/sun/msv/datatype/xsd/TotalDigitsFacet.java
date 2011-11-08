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

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

/**
 * 'totalDigits' facet.
 *
 * this class holds these facet information and performs validation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TotalDigitsFacet extends DataTypeWithLexicalConstraintFacet {
    /** maximum number of total digits. */
    public final int        precision;

    public TotalDigitsFacet( String nsUri, String typeName, XSDatatypeImpl baseType, int _precision, boolean _isFixed )
        throws DatatypeException {
        super( nsUri, typeName, baseType, FACET_TOTALDIGITS, _isFixed );
        
        precision = _precision;
        
        // loosened facet check
        DataTypeWithFacet o = baseType.getFacetObject(FACET_TOTALDIGITS);
        if(o!=null && ((TotalDigitsFacet)o).precision < this.precision )
            throw new DatatypeException( localize( ERR_LOOSENED_FACET,
                FACET_TOTALDIGITS, o.displayName() ) );
        
        // consistency with scale is checked in XSDatatypeImpl.derive method.
    }

    protected boolean checkLexicalConstraint( String content ) {
        return countPrecision(content)<=precision;
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        final int cnt = countPrecision(content);
        if( cnt<=precision )    return;
        
        throw new DatatypeException( DatatypeException.UNKNOWN,
            localize(ERR_TOO_MUCH_PRECISION, new Integer(cnt), new Integer(precision)) );
    }
    
    /** counts the number of digits */
    protected static int countPrecision( String literal ) {
        final int len = literal.length();
        boolean skipMode = true;
        boolean seenDot = false;
        
        int count=0;
        int trailingZero=0;
        
        for( int i=0; i<len; i++ ) {
            final char ch = literal.charAt(i);
            
            if(ch=='.') {
                skipMode = false;// digits after '.' is considered significant.
                seenDot = true;
            }
            
            if( skipMode ) {
                // in skip mode, leading zeros are skipped
                if( '1'<=ch && ch<='9' ) {
                    count++;
                    skipMode = false;
                }
            } else {
                if( seenDot && ch=='0' )    trailingZero++;
                else                        trailingZero=0;
                
                if( '0'<=ch && ch<='9' )
                    count++;
            }
        }
        
        return count-trailingZero;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
