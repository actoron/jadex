/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.datatype.xsd;


/**
 * Interface implemented by the parent state of FacetState.
 * 
 * the parent holds a Facets object, to which FacetState will add
 * facets.
 * 
 * After all facets are added, the parent state should derive a
 * new type.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface FacetStateParent {
    /** gets an incubator object that the owner holds. */
    XSTypeIncubator getIncubator();
}
