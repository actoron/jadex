/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import com.sun.msv.reader.AbortException;
import com.sun.msv.reader.ChildlessState;

/**
 * used to parse &lt;include&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IncludeState extends ChildlessState {
    
    protected void startSelf() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        super.startSelf();
        try {
            reader.switchSource( this,
                new RootIncludedSchemaState(
                    reader.sfactory.schemaIncluded(this,reader.currentSchema.targetNamespace) ) );
        } catch( AbortException e ) {
            // recover by ignoring the error
        }
    }
}
