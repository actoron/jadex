/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader;

import com.sun.msv.util.StartTagInfo;

/**
 * Base implementation for those states who cannot have any children.
 * (e.g., RELAX: empty, null, etc.   TREX: anyString, notAllowed, etc.)
 * 
 * Note that this class does not prohibit children from different namespace.
 * Those "foreign" elements are just ignored.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionWithoutChildState extends ExpressionState
{
    protected final State createChildState(StartTagInfo tag)
    {
        // return null to indicate that this element does not accept a child.
        return null;
    }
}
