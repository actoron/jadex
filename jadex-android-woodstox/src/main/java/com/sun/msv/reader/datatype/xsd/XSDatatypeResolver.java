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
 * Resolves a datatype name to the corresponding XSDatatypeExp object.
 * 
 * <p>
 * This interface has to be implemented by the GrammarReader
 * if that GrammarReader uses this package.
 */
public interface XSDatatypeResolver
{
    /**
     * @param   datatypeName
     *      The type of this value varies in the schema language.
     *      In XML Schema, for example, in which QNames are used
     *      to designate datatypes, this parameter will be QName.
     *      In RELAX Core, in which the same syntax is used but
     *      NCName is used to designate datatypes. So this parameter
     *      will be NCName.
     * 
     * @return
     *      A non-null valid object. An error should be reported
     *      and recovered by the callee.
     */
    XSDatatypeExp resolveXSDatatype( String datatypeName );
}
