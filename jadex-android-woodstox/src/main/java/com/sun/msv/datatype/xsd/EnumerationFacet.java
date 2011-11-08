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

import java.util.Collection;
import java.util.Set;

/**
 * "enumeration" facets validator.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class EnumerationFacet extends DataTypeWithValueConstraintFacet {
    protected EnumerationFacet( String nsUri, String typeName, XSDatatypeImpl baseType, Collection _values, boolean _isFixed )
        throws DatatypeException {
        super(nsUri,typeName,baseType,FACET_ENUMERATION,_isFixed);
        values = new java.util.HashSet( _values );
    }
    
    /** set of valid values */
    public final Set values;

    public Object _createValue( String literal, ValidationContext context ) {
        Object o = baseType._createValue(literal,context);
        if(o==null || !values.contains(o))        return null;
        return o;
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if( _createValue(content,context)!=null )    return;
        
        // TODO: guess which item the user was trying to specify
        
        if( values.size()<=4 ) {
            // if choices are small in number, include them into error messages.
            Object[] members = values.toArray();
            String r="";
            
            if( members[0] instanceof String
            ||  members[0] instanceof Number ) {
                // this will cover 80% of the use case.
                r += "\""+members[0].toString()+"\"";
                for( int i=1; i<members.length; i++ )
                    r+= "/\""+members[i].toString()+"\"";
                
                r = "("+r+")";    // oh, don't tell me I should use StringBuffer.
                
                throw new DatatypeException( DatatypeException.UNKNOWN,
                    localize(ERR_ENUMERATION_WITH_ARG, r) );
            }
        }
        throw new DatatypeException( DatatypeException.UNKNOWN,
            localize(ERR_ENUMERATION) );
    }


    // serialization support
    private static final long serialVersionUID = 1;    
}
