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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * utility functions that creates date/time related objects.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class DateTimeFactory {
    
    public static IDateTimeValueType createFromDateTime(
        Number year, Integer month, Integer day,
        Integer hour, Integer minute, Number mSecond, java.util.TimeZone zone ) {
//        if( year instanceof Integer )
//            return new SmallDateTimeValueType( ... );
        
        BigDecimal second=null;
        
        if( year instanceof Integer )        year = new BigInteger(year.toString());
        
        if( mSecond!=null ) {
            if( mSecond instanceof Integer )    // convert it to second
                second = new BigDecimal(mSecond.toString()).movePointLeft(3);
            else
            if( mSecond instanceof BigDecimal )
                second = ((BigDecimal)mSecond).movePointLeft(3);
            else
                throw new UnsupportedOperationException();
        }
        
        return new BigDateTimeValueType( (BigInteger)year, month, day, hour, minute, second, zone );
    }
    
    public static IDateTimeValueType createFromDate(
        Number year, Integer month, Integer day, java.util.TimeZone zone ) {
        return createFromDateTime( year, month, day, null, null, null, zone );
    }
    
    public static IDateTimeValueType createFromTime(
        Integer hour, Integer minute, Number mSecond, java.util.TimeZone zone ) {
        return createFromDateTime( null, null, null, hour, minute, mSecond, zone );
    }
}
