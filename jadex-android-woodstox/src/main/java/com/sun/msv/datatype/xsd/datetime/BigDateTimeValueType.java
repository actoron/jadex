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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

/**
 * DateTimeValueType object that can hold all lexically valid dateTime value.
 * 
 * This class provides:
 * <ol>
 *  <li> Unlimited digits for year (e.g., "year 9999999999999999999999")
 *  <li> Unlimited digits for fraction of second (e.g. 0.00000000000001 sec)
 * </ol>
 * 
 * To provide methods that can change date/time values, normalize method
 * should be modified too.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class BigDateTimeValueType implements IDateTimeValueType {
    
    /** year value.
     * this variable is null if no year is specified.
     *
     * Since there is no year 0, value 0 indicates year -1. -1 indicates -2, and so forth.
     */
    private BigInteger year;
    public BigInteger getYear() {
        return year;
    }

    /** month (always between 0 and 11)
     * this variable is null if no year is specified
     */
    private Integer month;
    public Integer getMonth() {
        return month;
    }

    /** day (always normalized, between 0-30)
     * this variable is null if no year is specified
     */
    private Integer day;
    public Integer getDay() {
        return day;
    }

    /** hour (always between 0 and 23)
     * this variable is null if no year is specified
     */
    private Integer hour;
    public Integer getHour() {
        return hour;
    }

    /** minute (always between 0 and 59)
     * this variable is null if no year is specified
     */
    private Integer minute;
    public Integer getMinute() {
        return minute;
    }

    /** second (always in [0,60) )
     * this variable is null if no year is specified
     */
    private BigDecimal second;
    public BigDecimal getSecond() {
        return second;
    }

    /** time zone specifier. null if missing */
    private java.util.TimeZone zone;
    public java.util.TimeZone getTimeZone() {
        return zone;
    }

    /** creates an instance with the specified BigDateTimeValueType,
     *  with modified time zone.
     * 
     *  created object shares its date/time value component with the original one,
     *  so special care is necessary not to mutate those values.
     */
    public BigDateTimeValueType(BigDateTimeValueType base, java.util.TimeZone newTimeZone) {
        this(base.year, base.month, base.day, base.hour, base.minute, base.second, newTimeZone);
    }

    public BigDateTimeValueType(
        BigInteger year,
        int month,
        int day,
        int hour,
        int minute,
        BigDecimal second,
        java.util.TimeZone timeZone) {
        this(year, new Integer(month), new Integer(day), new Integer(hour), new Integer(minute), second, timeZone);
    }

    public BigDateTimeValueType(
        BigInteger year,
        Integer month,
        Integer day,
        Integer hour,
        Integer minute,
        BigDecimal second,
        java.util.TimeZone timeZone) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.zone = timeZone;
    }

    public BigDateTimeValueType() {
    }

    public BigDateTimeValueType getBigValue() {
        return this;
    }

    public boolean equals(Object o) {
        return equals((IDateTimeValueType)o);
    }

    public boolean equals(IDateTimeValueType rhs) {
        if (!(rhs instanceof BigDateTimeValueType))
            rhs = rhs.getBigValue();
        return equals(this, (BigDateTimeValueType)rhs);
    }

    public boolean equals(BigDateTimeValueType lhs, BigDateTimeValueType rhs) {
        return compare(lhs, rhs) == Comparator.EQUAL;
    }

    /** gets a human-readable representation of this object.
     *
     * return value is not intended to be compliant with the canonical representation
     * of "dateTime" type.
     */
    public String toString() {
        return PreciseCalendarFormatter.format("%Y-%M-%DT%h:%m:%s%z",this);
    }

    public int hashCode() {
        // to be consistent with equals method, we have to normalize
        // value before computation.
        BigDateTimeValueType n = (BigDateTimeValueType)this.normalize();
        return Util.objHashCode(n.year)
            + Util.objHashCode(n.month)
            + Util.objHashCode(n.day)
            + Util.objHashCode(n.hour)
            + Util.objHashCode(n.minute)
            + Util.objHashCode(n.second)
            + Util.objHashCode(n.zone);
    }

    public int compare(IDateTimeValueType o) {
        if (!(o instanceof BigDateTimeValueType))
            o = o.getBigValue();

        return compare(this, (BigDateTimeValueType)o);
    }

    /**
     * compares two BigDateTimeValueType and returns one of the constant defined in
     * {@link Comparator}.
     * 
     * Order-relation between two dateTime is defined in
     * http://www.w3.org/TR/xmlschema-2/#dateTime
     */
    protected static int compare(BigDateTimeValueType lhs, BigDateTimeValueType rhs) {
        lhs = (BigDateTimeValueType)lhs.normalize();
        rhs = (BigDateTimeValueType)rhs.normalize();

        if ((lhs.zone != null && rhs.zone != null) || (lhs.zone == null && rhs.zone == null)) {
            if (!Util.objEqual(lhs.year, rhs.year))
                return Util.objCompare(lhs.year, rhs.year);
            if (!Util.objEqual(lhs.month, rhs.month))
                return Util.objCompare(lhs.month, rhs.month);
            if (!Util.objEqual(lhs.day, rhs.day))
                return Util.objCompare(lhs.day, rhs.day);
            if (!Util.objEqual(lhs.hour, rhs.hour))
                return Util.objCompare(lhs.hour, rhs.hour);
            if (!Util.objEqual(lhs.minute, rhs.minute))
                return Util.objCompare(lhs.minute, rhs.minute);
            if (!Util.objEqual(lhs.second, rhs.second))
                return Util.objCompare(lhs.second, rhs.second);

            return Comparator.EQUAL;
        }

        if (lhs.zone == null) {
            int r;

            r = compare((BigDateTimeValueType)new BigDateTimeValueType(lhs, Util.timeZoneNeg14).normalize(), rhs);
            if (r == Comparator.EQUAL || r == Comparator.LESS)
                return Comparator.LESS; // lhs < rhs

            r = compare((BigDateTimeValueType)new BigDateTimeValueType(lhs, Util.timeZonePos14).normalize(), rhs);
            if (r == Comparator.EQUAL || r == Comparator.GREATER)
                return Comparator.GREATER; // lhs > rhs

            return Comparator.UNDECIDABLE; // lhs <> rhs
        } else {
            int r;

            r = compare(lhs, (BigDateTimeValueType)new BigDateTimeValueType(rhs, Util.timeZonePos14));
            if (r == Comparator.EQUAL || r == Comparator.LESS)
                return Comparator.LESS; // lhs < rhs

            r = compare(lhs, (BigDateTimeValueType)new BigDateTimeValueType(rhs, Util.timeZoneNeg14));
            if (r == Comparator.EQUAL || r == Comparator.GREATER)
                return Comparator.GREATER; // lhs > rhs

            return Comparator.UNDECIDABLE; // lhs <> rhs
        }
    }

    /** normalized DateTimeValue of this object.
     * 
     * once when the normalized value is computed,
     * the value is kept in this varible so that
     * successive calls to normalize method need not
     * have to compute it again.
     * 
     * This approach assumes that modification to the date/time component
     * will never be made.
     */
    private IDateTimeValueType normalizedValue = null;
    
    public IDateTimeValueType normalize() {
        // see if this object is already normalized
        if (zone==TimeZone.ZERO || zone==null)
            return this;

        // see if there is cached normalized value
        if (normalizedValue != null)
            return normalizedValue;

        // for normalization to work correctly,
        // we have to extend the precision.
        // otherwise, addition will remove unspecified fields,
        // and the result becomes incorrect. For example,
        // --03-- + (+08:00)    -->  --02--

        // which is apparently not what we wanted.

        // update: it seems to me that this unintuitive behavior is 
        //         not going to be corrected in XML Schema 1.0

        // faster performance can be achieved by writing optimized inline addition code.
        normalizedValue = this.add(BigTimeDurationValueType.fromMinutes(
            -zone.getRawOffset()/(60*1000)));

        ((BigDateTimeValueType)normalizedValue).zone = TimeZone.ZERO;

        return normalizedValue;
    }
    
    private static BigInteger nullAs0(BigInteger o) {
        if (o != null)
            return o;
        else
            return BigInteger.ZERO;
    }

    private static BigDecimal nullAs0(BigDecimal o) {
        if (o != null)
            return o;
        else
            return Util.decimal0;
    }

    private static BigInteger[] divideAndRemainder(BigInteger x1, BigInteger x2) {
        BigInteger[] r = x1.divideAndRemainder(x2);
        if (r[1].signum() < 0) {
            // in BigInteger, -2/10 = -2, which is not preferable.
            // we want -2/10 to be 8, with quodrant of -1.
            r[1] = r[1].add(x2);
            r[0] = r[0].subtract(BigInteger.ONE);
        }
        return r;
    }

    public IDateTimeValueType add(ITimeDurationValueType _rhs) {
        if (_rhs instanceof BigTimeDurationValueType) {
            // big + big
            BigTimeDurationValueType rhs = (BigTimeDurationValueType)_rhs;

            BigInteger[] quoAndMod = divideAndRemainder(Util.int2bi(this.month).add(signed(rhs,rhs.month)), Util.the12);

            BigInteger oyear;
            int omonth;
            int ohour, ominute;
            BigDecimal osecond;

            omonth = quoAndMod[1].intValue();
            oyear = quoAndMod[0].add(nullAs0(this.year)).add(signed(rhs,rhs.year));

            BigDecimal sec = nullAs0(this.second).add(signed(rhs,rhs.second));

            // quo = floor((this.second+rhs.second)/60)
            //     = floor( (this.second+rhs.second)*10^scale / (60*10^scale) )
            //     = (this.second+rhs.second).unscaled / 60*10^scale

            quoAndMod = divideAndRemainder(sec.unscaledValue(), Util.the60.multiply(Util.the10.pow(sec.scale())));

            osecond = new BigDecimal(quoAndMod[1], sec.scale());

            quoAndMod = divideAndRemainder(quoAndMod[0].add(Util.int2bi(this.minute)).add(signed(rhs,rhs.minute)), Util.the60);
            ominute = quoAndMod[1].intValue();

            quoAndMod = divideAndRemainder(quoAndMod[0].add(Util.int2bi(this.hour)).add(signed(rhs,rhs.hour)), Util.the24);
            ohour = quoAndMod[1].intValue();

            int tempDays;
            int md = Util.maximumDayInMonthFor(oyear, omonth);
            {
                int dayValue = (this.day != null) ? this.day.intValue() : 0;
                if (dayValue < 0)
                    tempDays = 0;
                else if (dayValue >= md)
                    tempDays = md - 1;
                else
                    tempDays = dayValue;
            }

            BigInteger oday = signed(rhs,rhs.day).add(quoAndMod[0]).add(Util.int2bi(tempDays));
            while (true) {
                int carry;
                if (oday.signum() == -1) { // day<0
                    oday = oday.add(Util.int2bi(Util.maximumDayInMonthFor(oyear, (omonth + 11) % 12)));
                    carry = -1;
                } else {
                    BigInteger bmd = Util.int2bi(Util.maximumDayInMonthFor(oyear, omonth));
                    if (oday.compareTo(bmd) >= 0) {
                        oday = oday.subtract(bmd);
                        carry = +1;
                    } else
                        break;
                }

                omonth += carry;
                if (omonth < 0) {
                    omonth += 12;
                    oyear = oyear.subtract(BigInteger.ONE);
                }
                oyear = oyear.add(Util.int2bi(omonth / 12));
                omonth %= 12;
            }

            // set those fields blank which are not originally specified.
            return new BigDateTimeValueType(
                this.year != null ? oyear : null,
                this.month != null ? new Integer(omonth) : null,
                this.day != null ? new Integer(oday.intValue()) : null,
                this.hour != null ? new Integer(ohour) : null,
                this.minute != null ? new Integer(ominute) : null,
                this.second != null ? osecond : null,
                this.zone);
        } else {
            // big + small
            // TODO : implement this to achive better performance

            // just for now, convert it to BigTimeDurationValue and then compute the result.
            return add(_rhs.getBigValue());
        }
    }
    
    private BigInteger signed( BigTimeDurationValueType dur, BigInteger i ) {
        if(dur.signum<0)    return i.negate();
        else                return i;
    }
    
    private BigDecimal signed( BigTimeDurationValueType dur, BigDecimal i ) {
        if(dur.signum<0)    return i.negate();
        else                return i;
    }
    
    public Calendar toCalendar() {
        // set fields of Calendar.
        // In BigDateTimeValueType, the first day of the month is 0,
        // where it is 1 in java.util.Calendar.

//        Calendar cal = new java.util.GregorianCalendar(createJavaTimeZone());
//        cal.clear(); // reset all fields. This method does not reset the time zone.
        // the following is faster than above.
        Calendar cal = new java.util.GregorianCalendar(0,0,0);
        cal.setTimeZone(createJavaTimeZone());
        cal.clear(Calendar.YEAR);
        cal.clear(Calendar.MONTH);
        cal.clear(Calendar.DAY_OF_MONTH);

        if (getYear() != null)
            cal.set(Calendar.YEAR, getYear().intValue());
        if (getMonth() != null)
            cal.set(Calendar.MONTH, getMonth().intValue());
        if (getDay() != null)
            cal.set(Calendar.DAY_OF_MONTH, getDay().intValue() + 1 /*offset*/
            );
        if (getHour() != null)
            cal.set(Calendar.HOUR_OF_DAY, getHour().intValue());
        if (getMinute() != null)
            cal.set(Calendar.MINUTE, getMinute().intValue());
        if (getSecond() != null) {
            cal.set(Calendar.SECOND, getSecond().intValue());
            cal.set(Calendar.MILLISECOND, getSecond().movePointRight(3).intValue() % 1000);
        }

        return cal;
    }

    /**
     * Creates the equivalent Java TimeZone object.
     * 
     * @deprecated
     *      use {@link #getTimeZone()}.
     * @return
     *      a non-null valid object.
     */
    protected java.util.TimeZone createJavaTimeZone() {
        java.util.TimeZone tz = getTimeZone();
        if(tz==null)    return TimeZone.MISSING;
        else            return tz;
    }
    
    
    

    /*
        public static void main( String[] args )
        {
            Object o1 = new BigDateTimeValueType( new BigInteger("2001"), new Integer(5), new Integer(1), null, null, null, null );
            Object o2 = new BigDateTimeValueType( new BigInteger("2001"), new Integer(5), new Integer(1), null, null, null, null );
            
            System.out.println(o1.hashCode());
            System.out.println(o2.hashCode());
            System.out.println(o1.equals(o2));
            System.out.println(o2.equals(o1));
            
            java.util.Set s = new java.util.HashSet();
            s.add(o1);
            System.out.println( s.contains(o2) );
        }
    
        public static void main( String[] args )
        {
            Object o1 = new BigDateTimeValueType( new BigInteger("1512"), new Integer(1), new Integer(4), null, null, null, TimeZone.create(-12*60) );
            Object o2 = new BigDateTimeValueType( new BigInteger("1512"), new Integer(1), new Integer(5), null, null, null, TimeZone.create(+12*60) );
            
            System.out.println(o1.hashCode());
            System.out.println(o2.hashCode());
            System.out.println(o1.equals(o2));
            System.out.println(o2.equals(o1));
        }
     */

    // serialization support
    private static final long serialVersionUID = 1;
}

