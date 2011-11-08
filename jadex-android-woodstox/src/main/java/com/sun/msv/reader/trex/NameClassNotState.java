/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NotNameClass;

/**
 * parses &lt;not&gt; name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NameClassNotState extends NameClassWithChildState {
    protected NameClass castNameClass( NameClass halfCastedNameClass, NameClass child ) {
        // this parameter is null only for the first time invocation.
        if( halfCastedNameClass!=null ) {
            // <not> only allows one child.
            reader.reportError( TREXBaseReader.ERR_MORE_THAN_ONE_NAMECLASS );
            // recovery can be done by simply doing nothing at all.
            return halfCastedNameClass;
        } else
            return new NotNameClass(child);
    }
}
