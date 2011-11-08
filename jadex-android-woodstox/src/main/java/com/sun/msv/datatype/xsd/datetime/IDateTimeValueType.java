/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd.datetime;

import java.util.Calendar;

/**
 * Interface as value type of DateTimeType
 * 
 * @author Kohsuke KAWAGUCHI
 */
public interface IDateTimeValueType extends java.io.Serializable {
    BigDateTimeValueType getBigValue();
    
    /** returns the result of addition of this object and TimeDuration.
     * 
     * this object will not be mutated.
     */
    IDateTimeValueType add( ITimeDurationValueType duration );
    
    /** gets the normalized IDateTimeValueType.
     * 
     * normalized value always has GMT timezone or no timezone
     */
    IDateTimeValueType normalize();
    
    /** compare two DateTimeValueType as defined in
     *  com.sun.msv.datatype/Comparator
     */
    int compare( IDateTimeValueType rhs );
    
    /**
     * Approximates the value represented by this object into a Calendar object.
     * This conversion may lose accuracy.
     */
    Calendar toCalendar();
}
