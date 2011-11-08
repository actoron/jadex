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

import java.math.BigInteger;

/**
 * Value object of "integer" type.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IntegerValueType extends Number implements Comparable, java.io.Serializable {
    /** canonical representation of absolute value of integer
     * 
     * BNF of canonical representation
     * 
     * canonical_integer    :=  "0" | nonzero
     * nonzero                := '-'? ["1"-"9"] ["0"-"9"]*
     */
    private final String value;

    /** internal use only: construct object from canonicalized value */
    private IntegerValueType(String canonicalizedValue) {
        value = canonicalizedValue;
    }
    
    private IntegerValueType( long v ) {
        this.value = Long.toString(v);
    }

    /**
     * translates a BigInteger to an IntegerValueType.
     */
    public static IntegerValueType create(BigInteger bi) {
        return create(bi.toString());
    }

    /**
     * translates non-canonicalized representation of an integer into
     * an IntegerValueType.
     * 
     * @return    null
     *        when the parameter is not even valid with respect to
     *        the lexical space of "integer" type specified in
     *        XML Schema datatype spec.
     */
    public static IntegerValueType create(String nonCanonicalizedValue) {
        int idx = 0;
        String v = "";
        final int len = nonCanonicalizedValue.length();

        if (len == 0)
            return null;
        switch (nonCanonicalizedValue.charAt(idx)) {
            case '+' :
                idx++;
                break; // ignore the sign
            case '-' :
                v += '-';
                idx++;
                break;
            case '0' :
            case '1' :
            case '2' :
            case '3' :
            case '4' :
            case '5' :
            case '6' :
            case '7' :
            case '8' :
            case '9' :
                break;
            default :
                return null;
        }

        if (idx == len)
            return null; // just sign only: reject it

        // skip leading '0'
        while (idx < len && nonCanonicalizedValue.charAt(idx) == '0')
            idx++;

        if (idx == len) {
            // all the digits are skipped : that means this value is 0
            return new IntegerValueType("0");
        }

        // adding digits
        while (idx < len) {
            final char ch = nonCanonicalizedValue.charAt(idx++);
            if ('0' <= ch && ch <= '9')
                v += ch;
            else
                return null; // illegal char
        }

        return new IntegerValueType(v);
    }

    /**
     * @return 1   if this value is bigger than rhs
     *          0   if the values are the same
     *          -1  if rhs is bigger than this.
     */
    public int compareTo(Object o) {
        IntegerValueType rhs;
        if (o instanceof IntegerValueType)
            rhs = (IntegerValueType)o;
        else
            rhs = new IntegerValueType(((Number)o).longValue());

        boolean lhsIsNegative = value.charAt(0) == '-';
        boolean rhsIsNegative = rhs.value.charAt(0) == '-';

        if (lhsIsNegative && !rhsIsNegative)
            return -1;
        if (rhsIsNegative && !lhsIsNegative)
            return 1;

        // now both number have the same sign.

        int lp, rp, llen, rlen;

        if (lhsIsNegative && rhsIsNegative)
            lp = rp = 1;
        else
            lp = rp = 0;

        llen = value.length() - lp;
        rlen = rhs.value.length() - rp;

        if (llen > rlen)
            return lhsIsNegative ? -1 : 1;
        if (llen < rlen)
            return lhsIsNegative ? 1 : -1;

        // now we have the same length. compare left to right
        while (llen > 0) {
            final char lch = value.charAt(lp++);
            final char rch = rhs.value.charAt(rp++);

            if (lch > rch)
                return lhsIsNegative ? -1 : 1;
            if (lch < rch)
                return lhsIsNegative ? 1 : -1;

            llen--;
        }

        return 0; // they are the same value
    }

    public boolean equals(Object o) {
        if (o instanceof IntegerValueType)
            return value.equals(((IntegerValueType)o).value);
        else
            return false;
    }

    public int hashCode() {
        return value.hashCode();
    }
    public String toString() {
        return value;
    }

    public int precision() {
        // TODO : what is the exact definition of "precision"?
        // What is the precision of "100"? 1, or 3?
        final int len = value.length();
        if (value.charAt(0) == '-')
            return len - 1;
        else
            return len;
    }

    /** returns true if the value if non-positive (less than or equal to zero) */
    public boolean isNonPositive() {
        final char ch = value.charAt(0);
        if (ch == '-' || ch == '0')
            return true;
        return false;
    }

    /** returns true if the value if positive (greater than zero) */
    public boolean isPositive() {
        final char ch = value.charAt(0);
        if (ch == '-' || ch == '0')
            return false;
        return true;
    }

    /** returns true if the value if negative (less than zero) */
    public boolean isNegative() {
        return value.charAt(0) == '-';
    }

    /** returns true if the value if non-negative (greater than or equal to zero) */
    public boolean isNonNegative() {
        return value.charAt(0) != '-';
    }

    /** converts to BigInteger. */
    public BigInteger toBigInteger() {
        return new BigInteger(value);
    }

    public double doubleValue() {
        return toBigInteger().doubleValue();
    }

    public float floatValue() {
        return (float)doubleValue();
    }

    public int intValue() {
        return toBigInteger().intValue();
    }

    public long longValue() {
        return toBigInteger().longValue();
    }

    // serialization support
    private static final long serialVersionUID = 1;
}
