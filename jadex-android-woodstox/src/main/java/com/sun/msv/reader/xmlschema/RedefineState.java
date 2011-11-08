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

import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.reader.AbortException;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.util.StartTagInfo;


/**
 * used to parse &lt;redefine&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RedefineState extends GlobalDeclState {
    
    // TODO: elementDecl/attributeDecl are prohibited in redefine.
    // TODO: it probably is an error to redefine undefined components.
    
    // TODO: it is NOT an error to fail to load the specified schema (see 4.2.3)

    /**
     * When a simple type is being redefined, the original declaration
     * will be stored here.
     */
    private SimpleTypeExp oldSimpleTypeExp;
    
    protected State createChildState( StartTagInfo tag ) {
        // SimpleType parsing is implemented in reader.datatype.xsd,
        // and therefore it doesn't support redefinition by itself.
        // so we need to take care of redefinition for them.
        // for detail, see RedefinableDeclState
        
        if( tag.localName.equals("simpleType") ) {
            final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
            String name = tag.getAttribute("name");
            
            SimpleTypeExp sexp = reader.currentSchema.simpleTypes.get(name);
            if( sexp==null ) {
                reader.reportError( XMLSchemaReader.ERR_REDEFINE_UNDEFINED, name );
                // recover by using an empty declaration
                sexp = reader.currentSchema.simpleTypes.getOrCreate(name);
            }
            
            reader.currentSchema.simpleTypes.redefine( name, sexp.getClone() );
            
            oldSimpleTypeExp = sexp;    // memorize this declaration
        }
        
        return super.createChildState(tag);
    }
    
    
    protected void startSelf() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        super.startSelf();
    
        try {// parse included grammar first.
            reader.switchSource( this,
                new RootIncludedSchemaState(
                    reader.sfactory.schemaIncluded(this,reader.currentSchema.targetNamespace) ) );
        } catch( AbortException e ) {
            // recover by ignoring the error
        }
        
        // disable duplicate definition check.
        prevDuplicateCheck = reader.doDuplicateDefinitionCheck;
    }
    
    /** previous value of reader#doDuplicateDefinitionCheck. */
    private boolean prevDuplicateCheck;
    
    protected void endSelf() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        reader.doDuplicateDefinitionCheck = prevDuplicateCheck;
        super.endSelf();
    }
    
    
    public void onEndChild( XSDatatypeExp type ) {
        // handle redefinition of simpleType.
        
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        final String typeName = type.name;
        
        if( typeName==null ) {
            // top-level simpleType must define a named type
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, "simpleType", "name" );
            return;    // recover by ignoring this declaration
        }
        
        oldSimpleTypeExp.set(type);
        reader.setDeclaredLocationOf(oldSimpleTypeExp);
        
        // restore the association
        reader.currentSchema.simpleTypes.redefine( oldSimpleTypeExp.name, oldSimpleTypeExp );
        
        oldSimpleTypeExp = null;
    }

}
