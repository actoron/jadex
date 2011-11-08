/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.typed;

import com.sun.msv.reader.State;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.util.StartTagInfo;

/**
 * reads TREX grammar with 'label' annotation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypedTREXGrammarInterceptor extends TREXGrammarReader.StateFactory
{
    public final static String LABEL_NAMESPACE =
        "http://www.sun.com/xml/msv/trex-type";
    
    public State element( State parent, StartTagInfo tag ) {
        return new TypedElementState();
    }
}
