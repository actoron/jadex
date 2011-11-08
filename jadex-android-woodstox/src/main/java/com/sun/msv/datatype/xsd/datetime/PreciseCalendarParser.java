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
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class PreciseCalendarParser extends AbstractCalendarParser {
    public static BigDateTimeValueType parse( String format, String value ) throws IllegalArgumentException {
        PreciseCalendarParser parser = new PreciseCalendarParser(format,value);
        parser.parse();
        return parser.createCalendar();
    }

    private PreciseCalendarParser( String format, String value ) {
        super(format,value);
    }
    
    private BigDateTimeValueType createCalendar() {
        return new BigDateTimeValueType(year,month,day,hour,minute,second,timeZone);
    }
    
    
    private BigInteger year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private BigDecimal second;
    private java.util.TimeZone timeZone;
        

    protected void parseFractionSeconds() {
        int s = vidx;
        BigInteger bi = parseBigInteger(1,Integer.MAX_VALUE);
        BigDecimal d = new BigDecimal(bi,vidx-s);
        if( second==null)   second = d;
        else                second = second.add(d);
    }

    protected void setTimeZone(java.util.TimeZone tz) {
        if(tz==TimeZone.MISSING)    tz=null;
        this.timeZone = tz;
    }

    protected void setSeconds(int i) {
        BigDecimal d = new BigDecimal(BigInteger.valueOf(i));
        if( second==null)   second = d;
        else                second = second.add(d);
    }

    protected void setMinutes(int i) {
        minute = new Integer(i);
    }

    protected void setHours(int i) {
        hour = new Integer(i);
    }

    protected void setDay(int i) {
        day = new Integer(i-1);     // zero origin
    }

    protected void setMonth(int i) {
        month = new Integer(i-1);   // zero origin
    }

    protected void setYear(int i) {
        year = BigInteger.valueOf(i);
    }
    
}
