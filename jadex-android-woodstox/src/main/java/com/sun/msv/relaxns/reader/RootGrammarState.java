/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.reader;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.relaxns.verifier.IslandSchemaImpl;
import com.sun.msv.relaxns.verifier.SchemaProviderImpl;
import com.sun.msv.util.StartTagInfo;

/**
 * invokes State object that parses the document element.
 * 
 * this state is used only one when starting parsing a RELAX schema.
 * For included module/grammar, different states are used.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootGrammarState extends SimpleState implements ExpressionOwner
{
    protected State createChildState( StartTagInfo tag ) {
        if(tag.localName.equals("grammar") ) 
            // it is a grammar.
            return new GrammarState();
        
        return null;
    }
    
    protected void endSelf()
    {// wrap-up.
        final RELAXNSReader reader = (RELAXNSReader)this.reader;
        
        SchemaProviderImpl schemaProvider = new SchemaProviderImpl(reader.grammar);
        reader.schemaProvider = schemaProvider;
        
        if(!reader.controller.hadError()) {
            // abort further wrap up if there was an error.
            
            // then bind it as the final wrap-up.
            if( !schemaProvider.bind(reader.controller) )
                reader.controller.setErrorFlag();
        
            // also bind top-level expression
            if( reader.grammar.topLevel!=null )
                // this 'if' clause is necessary when
                // <topLevel> is not specified (which is an error, and already reported.)
                reader.grammar.topLevel = 
                    reader.grammar.topLevel.visit(
                        new IslandSchemaImpl.Binder(schemaProvider, reader.controller, reader.pool ) );
        }
    }
    
    // GrammarState implements ExpressionState,
    // so RootState has to implement ExpressionOwner.
    public final void onEndChild(Expression exp) {}
}
