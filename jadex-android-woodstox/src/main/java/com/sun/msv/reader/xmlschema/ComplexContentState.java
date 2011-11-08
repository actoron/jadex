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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.reader.ExpressionWithChildState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;complexContent&gt; element.
 * 
 * the expression created by this state is used as ComplexTypeExp.self field.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ComplexContentState extends ExpressionWithChildState {
    
    /** ComplexType object that we are now constructing. */
    protected ComplexTypeExp parentDecl;
    
    protected ComplexContentState( ComplexTypeExp decl ) {
        this.parentDecl = decl;
    }
    
    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        if( super.exp!=null )
            // we have already parsed restriction/extension.
            return null;
        
        if( tag.localName.equals("restriction") )    return reader.sfactory.complexRst(this,tag,parentDecl);
        if( tag.localName.equals("extension") )        return reader.sfactory.complexExt(this,tag,parentDecl);
        
        return super.createChildState(tag);
    }
    
    protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
        if( halfCastedExpression!=null )
            // assertion failed.
            // this situation should be prevented by createChildState method.
            throw new Error();
        
        return newChildExpression;
    }

    /* Patch to address issue #1, [http://github.com/kohsuke/msv/issues#issue/1].
     */
    @Override
    protected Expression annealExpression(Expression contentType) {
      String mixed = startTag.getAttribute("mixed");

      if ("true".equals(mixed)) {
        contentType = reader.pool.createMixed(contentType);
      } else {
        if (mixed != null && !"false".equals(mixed)) {
          reader.reportError(XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "mixed", mixed);
          // recover by ignoring this error.
        }
      }

      return contentType;
    }

}
