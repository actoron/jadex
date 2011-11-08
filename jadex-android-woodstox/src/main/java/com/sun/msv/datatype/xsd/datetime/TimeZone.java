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

import java.io.Serializable;
import java.util.SimpleTimeZone;

/**
 * simple time zone component.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class TimeZone implements java.io.Serializable {
    /**
     * Difference from GMT in terms of minutes.
     * @deprecated here just for the serialization backward compatibility.
     */
    public int minutes;

    private Object readResolve() {
        // use java.util.TimeZone instead
        return new SimpleTimeZone(minutes*60*1000,"");
    }
    
    /**
     * The {@link java.util.TimeZone} representation that corresponds
     * to the ZERO singleton instance. Once again, using a special
     * instance is a hack to make the round-tripping work OK.
     */
    public static final java.util.TimeZone ZERO = new JavaZeroTimeZone();
    
    /**
     * The {@link java.util.TimeZone} representation that corresponds
     * to the missing time zone.
     */
    public static final java.util.TimeZone MISSING = new JavaMissingTimeZone();
    
    
    // serialization support
    private static final long serialVersionUID = 1;    
    
    
//
// nested inner classes
//    
    /**
     * @deprecated
     *      exists just for the backward serialization compatibility.
     */
    static class ZeroTimeZone extends TimeZone {
        ZeroTimeZone() {
        }
        protected Object readResolve() {
            // use the singleton instance
            return ZERO;
        }
        // serialization support
        private static final long serialVersionUID = 1;    
    }
    
    private static class JavaZeroTimeZone extends SimpleTimeZone implements Serializable {
        JavaZeroTimeZone() {
            super(0, "XSD 'Z' timezone");
        } 
        protected Object readResolve() {
            return ZERO;
        }
        // serialization support
        private static final long serialVersionUID = 1;    
    }
    
    private static class JavaMissingTimeZone extends SimpleTimeZone implements Serializable {
        JavaMissingTimeZone() {
            super(0, "XSD missing timezone");
        } 
        protected Object readResolve() {
            return MISSING;
        }
        // serialization support
        private static final long serialVersionUID = 1;    
    }
}
