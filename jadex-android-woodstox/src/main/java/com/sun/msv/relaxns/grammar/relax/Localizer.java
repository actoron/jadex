/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.grammar.relax;

/**
 * localizes messages
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Localizer {
    
    public static String localize( String propertyName, Object[] args ) {
        String format = java.util.ResourceBundle.getBundle(
            "com.sun.msv.relaxns.grammar.relax.Messages").getString(propertyName);
        
        return java.text.MessageFormat.format(format, args );
    }
    
    public static String localize( String prop )
    { return localize( prop, null ); }
    
    public static String localize( String prop, Object arg1 )
    { return localize( prop, new Object[]{arg1} ); }

    public static String localize( String prop, Object arg1, Object arg2 )
    { return localize( prop, new Object[]{arg1,arg2} ); }

    
    public static final String WRN_ANYOTHER_NAMESPACE_IGNORED // arg:1
        = "AnyOtherElementExp.Warning.AnyOtherNamespaceIgnored";
}
