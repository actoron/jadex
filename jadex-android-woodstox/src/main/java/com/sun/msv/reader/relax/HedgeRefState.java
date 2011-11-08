/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax;

import com.sun.msv.grammar.Expression;

/**
 * parses &lt;hedgeRef label="..." /&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class HedgeRefState extends LabelRefState
{
    protected final Expression resolve( String namespace, String label )
    { return ((RELAXReader)reader).resolveHedgeRef(namespace,label); }
}
