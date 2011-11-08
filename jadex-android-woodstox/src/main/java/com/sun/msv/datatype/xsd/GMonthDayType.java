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
 * "gMonthDay" type.
 * 
 * type of the value object is {@link com.sun.msv.datatype.xsd.datetime.IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#gMonthDay for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GMonthDayType extends DateTimeBaseType {
    public static final GMonthDayType theInstance = new GMonthDayType();
    private GMonthDayType() { super("gMonthDay"); }
    
    protected final String getFormat() {
        return "--%M-%D%z";
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
