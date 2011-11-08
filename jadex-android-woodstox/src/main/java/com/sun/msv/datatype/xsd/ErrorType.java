/*
 * @(#)$Id$
 *
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import com.sun.msv.datatype.SerializationContext;
import org.relaxng.datatype.ValidationContext;

/**
 * A dummy datatype that can be used to recover from errors.
 * This datatype accepts any values and any facets.
 * The TypeIncubator class also recognizes this class and
 * any operation on ErrorType will never cause any error and
 * silently return another ErrorType.
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ErrorType extends BuiltinAtomicType {

    public static final ErrorType theInstance = new ErrorType();
    
    protected ErrorType() {
        super("error");
    }

    protected Object _createValue(String content, ValidationContext context) {
        return this;
    }

    protected boolean checkFormat(String literal, ValidationContext context) {
        return true;
    }

    public String convertToLexicalValue(Object valueObject, SerializationContext context)
        throws IllegalArgumentException {
        return "";
    }

    public int isFacetApplicable(String facetName) {
        return APPLICABLE;
    }

    public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public Class getJavaObjectType() {
        return this.getClass();
    }


    // serialization support
    private static final long serialVersionUID = 1;    
}
