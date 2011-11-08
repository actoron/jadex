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


/**
 * very limited 'IDREF' type of XML Schema Part 2.
 * 
 * <p>
 * The cross-reference semantics of the ID/IDREF types must be
 * implemented externally. This type by itself does not enforce such a 
 * constraint.
 * 
 * <p>
 * One can call the {@link #getIdType()} method to enforce the cross-reference
 * semantics.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IDREFType extends NcnameType {
    
    public static final IDREFType theInstance = new IDREFType();
    
    protected IDREFType()    { super("IDREF"); }
    
    public int getIdType() { return ID_TYPE_IDREF; }
    
    protected Object readResolve() {
        // prevent serialization from breaking the singleton.
        return theInstance;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
