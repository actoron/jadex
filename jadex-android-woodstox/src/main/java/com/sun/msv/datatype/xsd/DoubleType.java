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

import com.sun.msv.datatype.SerializationContext;
import org.relaxng.datatype.ValidationContext;

/**
 * "double" type.
 * 
 * type of the value object is <code>java.lang.Double</code>.
 * See http://www.w3.org/TR/xmlschema-2/#double for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DoubleType extends FloatingNumberType {
    
    public static final DoubleType theInstance = new DoubleType();
    private DoubleType() { super("double"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        return load(lexicalValue);
    }
    
    public static Double load( String lexicalValue ) {
        // TODO : probably the same problems exist as in the case of float
        try {
            if(lexicalValue.equals("NaN"))    return new Double(Double.NaN);
            if(lexicalValue.equals("INF"))    return new Double(Double.POSITIVE_INFINITY);
            if(lexicalValue.equals("-INF"))    return new Double(Double.NEGATIVE_INFINITY);
            
            if(lexicalValue.length()==0
            || !isDigitOrPeriodOrSign(lexicalValue.charAt(0))
            || !isDigitOrPeriodOrSign(lexicalValue.charAt(lexicalValue.length()-1)) )
                return null;
            
            
            // these screening process is necessary due to the wobble of Float.valueOf method
            return Double.valueOf(lexicalValue);
        } catch( NumberFormatException e ) {
            return null;
        }
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof Double ))
            throw new IllegalArgumentException();
        
        return save((Double)value);
    }
    
    public static String save( Double value ) {
        double v = value.doubleValue();
        if( v==Double.NaN )                    return "NaN";
        if( v==Double.POSITIVE_INFINITY )    return "INF";
        if( v==Double.NEGATIVE_INFINITY )    return "-INF";
        return value.toString();
    }
    public Class getJavaObjectType() {
        return Double.class;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
