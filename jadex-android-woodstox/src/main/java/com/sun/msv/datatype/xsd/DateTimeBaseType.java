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

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.datetime.CalendarFormatter;
import com.sun.msv.datatype.xsd.datetime.CalendarParser;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.PreciseCalendarFormatter;
import com.sun.msv.datatype.xsd.datetime.PreciseCalendarParser;
import org.relaxng.datatype.ValidationContext;

import java.util.Calendar;

/**
 * base implementation of dateTime and dateTime-truncated types.
 * this class uses IDateTimeValueType as the value object.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class DateTimeBaseType extends BuiltinAtomicType implements Comparator {
    
    protected DateTimeBaseType(String typeName) {
        super(typeName);
    }

    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    protected final boolean checkFormat(String content, ValidationContext context) {
        // string derived types should use _createValue method to check its validity
        try {
            CalendarParser.parse(getFormat(),content);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public final Object _createValue(String content, ValidationContext context) {
        // for string, lexical space is value space by itself
        try {
            return PreciseCalendarParser.parse(getFormat(),content);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public final String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof IDateTimeValueType))
            throw new IllegalArgumentException();
        
        return PreciseCalendarFormatter.format(getFormat(),(IDateTimeValueType)value);
        
    }
        
    /** converts our DateTimeValueType to a java-friendly Date type. */
    public final Object _createJavaObject(String literal, ValidationContext context) {
        return CalendarParser.parse(getFormat(),literal);
    }

    public final String serializeJavaObject(Object value, SerializationContext context) {
        if(!(value instanceof Calendar))    throw new IllegalArgumentException();
        
        return CalendarFormatter.format( getFormat(), (Calendar)value );
    }
    
    public Class getJavaObjectType() {
        return Calendar.class;
    }
    
    /**
     * Formatting string passed to {@link CalendarParser#parse(String, String)}.
     */
    protected abstract String getFormat();
    
    
    
    

    /** compare two DateTimeValueType */
    public int compare(Object lhs, Object rhs) {
        return ((IDateTimeValueType)lhs).compare((IDateTimeValueType)rhs);
    }
    
    public final int isFacetApplicable(String facetName) {
        if(facetName.equals(FACET_PATTERN)
        || facetName.equals(FACET_ENUMERATION)
        || facetName.equals(FACET_WHITESPACE)
        || facetName.equals(FACET_MAXINCLUSIVE)
        || facetName.equals(FACET_MAXEXCLUSIVE)
        || facetName.equals(FACET_MININCLUSIVE)
        || facetName.equals(FACET_MINEXCLUSIVE))
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }

    private static final long serialVersionUID = 1465669066779112677L;
}
