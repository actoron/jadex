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

import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.NameClass;

/**
 * parses &lt;choice&gt; name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NameClassChoiceState extends NameClassWithChildState {
    protected NameClass castNameClass( NameClass halfCasted, NameClass newChild ) {
        if( halfCasted==null )    return newChild;    // first item
        else return new ChoiceNameClass( halfCasted, newChild );
    }
}
