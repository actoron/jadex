/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.writer;

import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.Grammar;

/**
 * Converter from AGM to the XML representation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("deprecation")
public interface GrammarWriter {
    
    /**
     * Sets DocumentHandler. This handler will receive the result of conversion.
     */
    void setDocumentHandler( DocumentHandler handler );
    
    /**
     * Converts this grammar to the XML representation.
     * 
     * @exception UnsupportedOperationException
     *        if this grammar cannot be serialized.
     *        this exception can be thrown on the half way of the conversion.
     * 
     * @exception SAXException
     *        DocumentHandler may throw a SAXException.
     */
    void write( Grammar grammar ) throws UnsupportedOperationException, SAXException;
}
