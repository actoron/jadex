/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier;

/**
 * Represents a kind of "constraint" over XML document.
 * 
 * Usually, this is what people call a schema.
 * 
 * <p>
 * Call the {@link #createAcceptor()} method to start validation.
 * 
 * @see Acceptor
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface DocumentDeclaration {
    
    /**
     * creates a new Acceptor that will validate the document element.
     * 
     * In RELAX, this concept is equivalent to &lt;topLevel&gt;
     * In TREX, this concept is equivalent to &lt;start&gt;
     * 
     * @return
     *        The implementation cannot return null.
     *        Apparently, it is impossible to fail in this early stage.
     */
    Acceptor createAcceptor();
}
