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


/**
 * "date" type.
 * 
 * type of the value object is {@link com.sun.msv.datatype.xsd.datetime.IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#dateTime for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DateType extends DateTimeBaseType {
    
    public static final DateType theInstance = new DateType();
    private DateType() { super("date"); }
    
    protected final String getFormat() {
        return "%Y-%M-%D%z";
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
