/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;


/**
 * represents one field of an identity constraint.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Field implements java.io.Serializable {
    
    /**
     * XPath that characterizes this field.
     * 'A|B' is represented by using two FieldPath objects.
     */
    public XPath[]    paths;
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
