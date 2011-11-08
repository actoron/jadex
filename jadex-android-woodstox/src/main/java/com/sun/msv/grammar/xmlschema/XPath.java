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

import com.sun.msv.grammar.NameClass;

/**
 * internal representation of XPath ('aaa/bbb/ccc/ ... /eee').
 * Note that 'A|B' is repsented by using two Path instances.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XPath implements java.io.Serializable {
    /**
     * this field is true if the XPath starts with '//' operator.
     */
    public boolean            isAnyDescendant;
    
    /**
     * each name class represents each step.
     */
    public NameClass[]        steps;
    
    /**
     * optional attribute step that can only appear as the last child.
     * Null if not present.
     */
    public NameClass        attributeStep;

    
    // serialization support
    private static final long serialVersionUID = 1;    
}
