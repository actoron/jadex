/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

/**
 * an interface that must be implemented by caller to
 * provide context information that is necessary to 
 * perform conversion from value object to the XML representation.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public interface SerializationContext {
    /**
     * get namespace prefix for the given namespace URI.
     * 
     * this method is used to convert QName. 
     * 
     * It is a responsibility of the callee to ensure that
     * the returned prefix is properly declared.
     * 
     * @return
     *        prefix for this namespace URI.
     *        return null to indicate that this namespace URI is the
     *        default name space. In this case, QNames are converted into
     *        unqualified names (just the local names only as NCNames).
     */
    String getNamespacePrefix( String namespaceURI );
}
