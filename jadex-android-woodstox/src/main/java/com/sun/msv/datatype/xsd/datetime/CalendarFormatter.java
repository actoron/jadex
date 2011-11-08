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
 * Formats a {@link Calendar} object to a String.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class CalendarFormatter extends AbstractCalendarFormatter {
    
    private CalendarFormatter() {} // no instanciation
    
    private static final CalendarFormatter theInstance = new CalendarFormatter();
    
    public static String format( String format, Calendar cal ) {
        return theInstance.doFormat(format,cal);
    }
    
    protected Calendar toCalendar(Object cal) {
        return (Calendar)cal;
    }

    protected void formatYear(Object cal, StringBuffer buf) {
        int year = ((Calendar)cal).get(Calendar.YEAR);
        
        String s;
        if (year <= 0) // negative value
            s = Integer.toString(1 - year);
        else // positive value
            s = Integer.toString(year);

        while (s.length() < 4)
            s = "0" + s;
        if (year <= 0)
            s = "-" + s;
        
        buf.append(s);
    }

    protected void formatMonth(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar)cal).get(Calendar.MONTH)+1,buf);
    }

    protected void formatDays(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar)cal).get(Calendar.DAY_OF_MONTH),buf);
    }

    protected void formatHours(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar)cal).get(Calendar.HOUR_OF_DAY),buf);
    }

    protected void formatMinutes(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar)cal).get(Calendar.MINUTE),buf);
    }

    protected void formatSeconds(Object _cal, StringBuffer buf) {
        Calendar cal = (Calendar)_cal;
        formatTwoDigits(cal.get(Calendar.SECOND),buf);
        if (cal.isSet(Calendar.MILLISECOND)) { // milliseconds
            int n = cal.get(Calendar.MILLISECOND);
            if(n!=0) {
                String ms = Integer.toString(n);
                while (ms.length() < 3)
                    ms = "0" + ms; // left 0 paddings.

                buf.append('.');
                buf.append(ms);
            }
        }
    }

}
