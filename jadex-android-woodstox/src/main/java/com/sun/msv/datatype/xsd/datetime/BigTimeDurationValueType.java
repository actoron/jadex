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

import com.sun.msv.datatype.xsd.Comparator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * ITimeDurationValueType implementation that can hold all lexically legal
 * timeDuration value.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class BigTimeDurationValueType implements ITimeDurationValueType {
    
    protected int signum;
    
    // all the fields should be positive
    protected BigInteger year;
    protected BigInteger month;
    protected BigInteger day;
    protected BigInteger hour;
    protected BigInteger minute;
    protected BigDecimal second;

    private static final BigDateTimeValueType[] testInstance =
        new BigDateTimeValueType[]{
            new BigDateTimeValueType(
                new BigInteger("1696"), 8/*Sep*/, 0/*1st*/, 0,0, new BigDecimal(0), TimeZone.ZERO ),
            new BigDateTimeValueType(
                new BigInteger("1697"), 1/*Feb*/, 0/*1st*/, 0,0, new BigDecimal(0), TimeZone.ZERO ),
            new BigDateTimeValueType(
                new BigInteger("1903"), 2/*Mar*/, 0/*1st*/, 0,0, new BigDecimal(0), TimeZone.ZERO ),
            new BigDateTimeValueType(
                new BigInteger("1903"), 6/*Jul*/, 0/*1st*/, 0,0, new BigDecimal(0), TimeZone.ZERO ) };
                                                                                

    public boolean equals(Object o) {
        return equals((ITimeDurationValueType)o);
    }
    public boolean equals(ITimeDurationValueType o) {
        return compare(o) == Comparator.EQUAL;
    }
    
    public String toString() {
        return    (signum<0?"-":"")+
            "P"+nullAsZero(year).abs()+"Y"+
            nullAsZero(month)+"M"+
            nullAsZero(day)+"DT"+
            nullAsZero(hour)+"H"+
            nullAsZero(minute)+"M"+
            (second==null?"":second.toString())+"S";
    }
    
    private BigInteger nullAsZero(BigInteger o) {
        if (o == null)
            return BigInteger.ZERO;
        else
            return o;
    }
    
    /**
     * hash code has to be consistent with equals method.
     */
    public int hashCode() {
        // 400Y = 365D*303 + 366D*97 = 146097D = 3506328 hours
        // = 210379680 minutes
        // and no other smaller years have their equivalent days.

        // hashCode is very complex because it has to consistent with the behavior of equals method.
        return nullAsZero(day)
            .multiply(Util.the24)
            .add(nullAsZero(hour))
            .multiply(Util.the60)
            .add(nullAsZero(minute))
            .mod(Util.the210379680)
            .hashCode();
    }

    public int compare(ITimeDurationValueType o) {
        if (!(o instanceof BigTimeDurationValueType))
            o = o.getBigValue();

        return compare(this, (BigTimeDurationValueType)o);
    }
    
    static private int compare(BigTimeDurationValueType lhs, BigTimeDurationValueType rhs) {
        boolean less = false, greater = false, noDeterminate = false;

        for (int i = 0; i < testInstance.length; i++) {
            BigDateTimeValueType l = (BigDateTimeValueType)testInstance[i].add(lhs);
            BigDateTimeValueType r = (BigDateTimeValueType)testInstance[i].add(rhs);

            int v = BigDateTimeValueType.compare(l, r);

            if (v < 0)
                less = true;
            if (v > 0)
                greater = true;
            if (v == 0) {
                if (!l.equals(r))
                    noDeterminate = true;
            }
        }

        if (noDeterminate)
            return Comparator.UNDECIDABLE;
        if (less && greater)
            return Comparator.UNDECIDABLE;
        if (less)
            return Comparator.LESS; // lhs<rhs
        if (greater)
            return Comparator.GREATER; // lhs>rhs
        return Comparator.EQUAL;
    }

    public BigTimeDurationValueType getBigValue() { return this; }

    /**
     * All the fields should be positive and use the signum field to
     * determine the sign.
     */
    public BigTimeDurationValueType(
        int signum,
        BigInteger year, BigInteger month, BigInteger day,
        BigInteger hour, BigInteger minute, BigDecimal second ) {
        this.signum  = signum;
        this.year    = year!=null?year:BigInteger.ZERO;
        this.month    = month!=null?month:BigInteger.ZERO;
        this.day    = day!=null?day:BigInteger.ZERO;
        this.hour    = hour!=null?hour:BigInteger.ZERO;
        this.minute    = minute!=null?minute:BigInteger.ZERO;
        this.second    = second!=null?second:Util.decimal0;
    }
    
    public static BigTimeDurationValueType fromMinutes( int minutes )
    { return fromMinutes(Util.int2bi(minutes)); }
    public static BigTimeDurationValueType fromMinutes( BigInteger minutes )
    { return new BigTimeDurationValueType(minutes.signum(),null,null,null,null,minutes.abs(),null); }


    /**
     * @return non-null positive value. use {@link #signum} for the sign.
     */
    public BigInteger getDay() {
        return day;
    }

    /**
     * @return non-null positive value. use {@link #signum} for the sign.
     */
    public BigInteger getHour() {
        return hour;
    }

    /**
     * @return non-null positive value. use {@link #signum} for the sign.
     */
    public BigInteger getMinute() {
        return minute;
    }

    /**
     * @return non-null positive value. use {@link #signum} for the sign.
     */
    public BigInteger getMonth() {
        return month;
    }

    /**
     * @return non-null positive value. use {@link #signum} for the sign.
     */
    public BigDecimal getSecond() {
        return second;
    }

    /**
     * @return non-null positive value. use {@link #signum} for the sign.
     */
    public BigInteger getYear() {
        return year;
    }

    /**
     * Reads in the lexical duration format.
     * 
     * @param lexicalRepresentation
     *      whitespace stripped lexical form.
     */
    public BigTimeDurationValueType(String lexicalRepresentation) throws IllegalArgumentException {
        
        final String s = lexicalRepresentation;
        int[] idx = new int[1];
        
        boolean positive;
        
        if (s.charAt(idx[0]) == '-') {
            idx[0]++;
            positive = false;
        } else {
            positive = true;
        }
        
        if (s.charAt(idx[0]++) != 'P') {
            throw new IllegalArgumentException(s); //,idx[0]-1);
        }
        
        // phase 1: chop the string into chunks
        // (where a chunk is '<number><a symbol>'
        //--------------------------------------
        int dateLen = 0;
        String[] dateParts = new String[3];
        int[] datePartsIndex = new int[3];
        while (s.length() != idx[0]
                && isDigit(s.charAt(idx[0]))
                && dateLen < 3) {
            datePartsIndex[dateLen] = idx[0];
            dateParts[dateLen++] = parsePiece(s, idx);
        }
        
        if (s.length() != idx[0] && s.charAt(idx[0]++) != 'T') {
            throw new IllegalArgumentException(s); // ,idx[0]-1);
        }
        
        int timeLen = 0;
        String[] timeParts = new String[3];
        int[] timePartsIndex = new int[3];
        while (s.length() != idx[0]
                && isDigitOrPeriod(s.charAt(idx[0]))
                && timeLen < 3) {
            timePartsIndex[timeLen] = idx[0];
            timeParts[timeLen++] = parsePiece(s, idx);
        }
        
        if (s.length() != idx[0]) {
            throw new IllegalArgumentException(s); // ,idx[0]);
        }
        if (dateLen == 0 && timeLen == 0) {
            throw new IllegalArgumentException(s); // ,idx[0]);
        }
        
        // phase 2: check the ordering of chunks
        //--------------------------------------
        organizeParts(s, dateParts, datePartsIndex, dateLen, "YMD");
        organizeParts(s, timeParts, timePartsIndex, timeLen, "HMS");
        
        // parse into numbers
        year = parseBigInteger(s, dateParts[0], datePartsIndex[0]);
        month = parseBigInteger(s, dateParts[1], datePartsIndex[1]);
        day = parseBigInteger(s, dateParts[2], datePartsIndex[2]);
        hour = parseBigInteger(s, timeParts[0], timePartsIndex[0]);
        minute = parseBigInteger(s, timeParts[1], timePartsIndex[1]);
        second = parseBigDecimal(s, timeParts[2], timePartsIndex[2]);
        
        // null -> 0
        year    = year!=null?year:BigInteger.ZERO;
        month   = month!=null?month:BigInteger.ZERO;
        day     = day!=null?day:BigInteger.ZERO;
        hour    = hour!=null?hour:BigInteger.ZERO;
        minute  = minute!=null?minute:BigInteger.ZERO;
        second  = second!=null?second:Util.decimal0;
        
        if( getSignum(year)==0 && getSignum(month)==0 && getSignum(day)==0
         && getSignum(hour)==0 && getSignum(minute)==0 && getSignum(second)==0) {
            signum = 0;
        } else
        if (positive) {
            signum = 1;
        } else {
            signum = -1;
        }
    }
    
    
    private int getSignum( BigInteger i ) {
        if( i == null ) return 0;
        else            return i.signum();
    }
    
    private int getSignum( BigDecimal i ) {
        if( i == null ) return 0;
        else            return i.signum();
    }
    
    private static boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }
    
    private static boolean isDigitOrPeriod(char ch) {
        return isDigit(ch) || ch == '.';
    }
    
    private static String parsePiece(String whole, int[] idx) throws IllegalArgumentException {
        int start = idx[0];
        while (idx[0] < whole.length()
                && isDigitOrPeriod(whole.charAt(idx[0]))) {
            idx[0]++;
        }
        if (idx[0] == whole.length()) {
            throw new IllegalArgumentException(whole); // ,idx[0]);
        }

        idx[0]++;

        return whole.substring(start, idx[0]);
    }
    
    private static void organizeParts( String whole, String[] parts,
                                       int[] partsIndex, int len, String tokens)
    throws IllegalArgumentException {

        int idx = tokens.length();
        for (int i = len - 1; i >= 0; i--) {
            int nidx =
            tokens.lastIndexOf(
                    parts[i].charAt(parts[i].length() - 1),
                    idx - 1);
            if (nidx == -1) {
                throw new IllegalArgumentException(whole);
                // ,partsIndex[i]+parts[i].length()-1);
            }

            for (int j = nidx + 1; j < idx; j++) {
                parts[j] = null;
            }
            idx = nidx;
            parts[idx] = parts[i];
            partsIndex[idx] = partsIndex[i];
        }
        for (idx--; idx >= 0; idx--) {
            parts[idx] = null;
        }
    }
    
    private static BigInteger parseBigInteger( String whole, String part, int index) throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        part = part.substring(0, part.length() - 1);
        
        // syntax error will cause NumberFormatException, which is IllegalArgumentException 
        return new BigInteger(part);
    }
    
    private static BigDecimal parseBigDecimal( String whole, String part, int index) throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        part = part.substring(0, part.length() - 1);
        
        // syntax error will cause NumberFormatException, which is IllegalArgumentException 
        return new BigDecimal(part);
    }
    
    
    
    

    // serialization support
    private static final long serialVersionUID = 1;
    
    private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        
        // read in the old format where there was no signum field
        // and the most significant field was carrying the sign.
        
        switch( year.signum() ) {
        case -1:    signum=-1; year=year.negate(); return;
        case 1:     signum= 1; return;
        }
        
        switch( month.signum() ) {
        case -1:    signum=-1; month=month.negate(); return;
        case 1:     signum= 1; return;
        }
        
        switch( day.signum() ) {
        case -1:    signum=-1; day=day.negate(); return;
        case 1:     signum= 1; return;
        }
        
        switch( hour.signum() ) {
        case -1:    signum=-1; hour=hour.negate(); return;
        case 1:     signum= 1; return;
        }
        
        switch( minute.signum() ) {
        case -1:    signum=-1; minute=minute.negate(); return;
        case 1:     signum= 1; return;
        }
        
        switch( second.signum() ) {
        case -1:    signum=-1; second=second.negate(); return;
        case 1:     signum= 1; return;
        }
        
        signum = 0;
    }
}
