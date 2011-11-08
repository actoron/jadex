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
 * "dateTime" type.
 * 
 * type of the value object is {@link com.sun.msv.datatype.xsd.datetime.IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#dateTime for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DateTimeType extends DateTimeBaseType {
    
    public static final DateTimeType theInstance = new DateTimeType();
    private DateTimeType() {
        super("dateTime");
    }
    
    protected final String getFormat() {
        return "%Y-%M-%DT%h:%m:%s%z";
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
