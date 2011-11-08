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
 * "float" type.
 * 
 * type of the value object is <code>java.lang.Float</code>.
 * See http://www.w3.org/TR/xmlschema-2/#float for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FloatType extends FloatingNumberType {
    
    public static final FloatType theInstance = new FloatType();
    private FloatType() { super("float"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        return load(lexicalValue);
    }
    
    public static Float load( String s ) {
        
        /* Incompatibilities of XML Schema's float "xfloat" and Java's float "jfloat"
        
            * jfloat.valueOf ignores leading and trailing whitespaces,
              whereas this is not allowed in xfloat.
            * jfloat.valueOf allows "float type suffix" (f, F) to be
              appended after float literal (e.g., 1.52e-2f), whereare
              this is not the case of xfloat.
        
            gray zone
            ---------
            * jfloat allows ".523". And there is no clear statement that mentions
              this case in xfloat. Although probably this is allowed.
            * 
        */
        
        try {
            if(s.equals("NaN"))        return new Float(Float.NaN);
            if(s.equals("INF"))        return new Float(Float.POSITIVE_INFINITY);
            if(s.equals("-INF"))    return new Float(Float.NEGATIVE_INFINITY);
            
            if(s.length()==0
            || !isDigitOrPeriodOrSign(s.charAt(0))
            || !isDigitOrPeriodOrSign(s.charAt(s.length()-1)) )
                return null;
            
            // these screening process is necessary due to the wobble of Float.valueOf method
            return Float.valueOf(s);
        } catch( NumberFormatException e ) {
            return null;
        }
    }
    public Class getJavaObjectType() {
        return Float.class;
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof Float ))
            throw new IllegalArgumentException();
        
        return save( (Float)value );
    }
    
    public static String save( Float value ) {
        float v = value.floatValue();
        if( v==Float.NaN )                    return "NaN";
        if( v==Float.POSITIVE_INFINITY )    return "INF";
        if( v==Float.NEGATIVE_INFINITY )    return "-INF";
        return value.toString();
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
