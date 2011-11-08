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

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.xmlschema.GroupDeclExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * used to parse &lt;group&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GroupState extends RedefinableDeclState {
    
    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        // TODO: group reference is prohibited under group element.
        return reader.createModelGroupState(this,tag);
    }

    protected ReferenceContainer getContainer() {
        return ((XMLSchemaReader)reader).currentSchema.groupDecls;
    }
    
    protected Expression initialExpression() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        if( startTag.containsAttribute("ref") ) {
            // this this tag has @ref.
            Expression exp = reader.resolveQNameRef(
                startTag, "ref",
                new XMLSchemaReader.RefResolver() {
                    public ReferenceContainer get( XMLSchemaSchema g ) {
                        return g.groupDecls;
                    }
                } );
            if( exp==null )        return Expression.epsilon;    // couldn't resolve QName.
            return exp;
        }
        
        return null;    // use null to indicate that there is no child expression.
    }

    protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
        if( halfCastedExpression!=null ) {
            reader.reportError( XMLSchemaReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
            return halfCastedExpression;
        }
        
        return newChildExpression;    // the first one.
    }
    
    
    protected Expression annealExpression(Expression contentType) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        if( !isGlobal() )        return contentType;
        
        
        // this <group> element is global.
        // register this as a global group definition.
            
        String name = startTag.getAttribute("name");
        if( name==null ) {
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, "group", "name" );
            // recover by returning a meaningless value, thereby ignoring this declaration.
            return Expression.nullSet;
        }
        
        if( contentType==null ) {
            reader.reportError( XMLSchemaReader.ERR_MISSING_CHILD_EXPRESSION );
            return Expression.nullSet;
        }
        
        GroupDeclExp decl;
        if( isRedefine() )
            decl = (GroupDeclExp)super.oldDecl;
        else {
            decl = reader.currentSchema.groupDecls.getOrCreate(name);
            if( decl.exp!=null )
                reader.reportError( 
                    new Locator[]{this.location,reader.getDeclaredLocationOf(decl)},
                    XMLSchemaReader.ERR_DUPLICATE_GROUP_DEFINITION,
                    new Object[]{name} );
        }
        reader.setDeclaredLocationOf(decl);
        decl.exp = contentType;

        return decl;
    }
}
