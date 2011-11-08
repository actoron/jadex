/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relax;

import org.relaxng.datatype.ValidationContext;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.BuiltinAtomicType;
import com.sun.msv.datatype.xsd.SimpleURType;
import com.sun.msv.datatype.xsd.XSDatatype;

/**
 * 'none' datatype of RELAX.
 * 
 * this type accepts nothing.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NoneType extends BuiltinAtomicType {
    
    public static final NoneType theInstance = new NoneType();
    private NoneType() { super("none"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    public int isFacetApplicable( String facetName ) {
        return NOT_ALLOWED;
    }
    
    public boolean checkFormat( String literal, ValidationContext context ) {
        return false;
    }

    public Object _createValue( String lexicalValue, ValidationContext context ) {
        return null;
    }
    
    public String convertToLexicalValue( Object o, SerializationContext context ) {
        throw new IllegalArgumentException();
    }
    
    public Class<?> getJavaObjectType() {
        return Object.class;    // actually, it never returns a value.
    }
    
    // TODO: implement _checkValid
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
