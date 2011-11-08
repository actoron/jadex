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
 * used to parse &lt;import&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ImportState extends ChildlessState {
    
    protected void startSelf() {
        super.startSelf();
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        // TODO: @schemaLocation is optional.
        String namespace = startTag.getAttribute("namespace");
        if( namespace==null )   namespace="";
        
        if( namespace.equals(reader.currentSchema.targetNamespace) ) {
            reader.reportError( XMLSchemaReader.ERR_IMPORTING_SAME_NAMESPACE );
            return;
        }
        
        if( reader.isSchemaDefined( reader.getOrCreateSchema(namespace) ) )
            // this grammar is already defined.
            // so ignore it.
            return;
                
        try {
            reader.switchSource( this,
                new RootIncludedSchemaState(reader.sfactory.schemaHead(namespace)) );
        } catch( AbortException e ) {
            // recover by ignoring the error
        }
    }
}
