/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;

/**
 * parses &lt;attribute&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends com.sun.msv.reader.trex.AttributeState
{
    private static final String infosetURI = "http://www.w3.org/2000/xmlns";
    protected void endSelf() {
        super.endSelf();
        
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        reader.restrictionChecker.checkNameClass(nameClass);
        
        nameClass.visit( new NameClassVisitor() {
            public Object onAnyName( AnyNameClass nc ) { return null; }
            public Object onSimple(SimpleNameClass nc) {
                if(nc.namespaceURI.equals(infosetURI))
                    reader.reportError( RELAXNGReader.ERR_INFOSET_URI_ATTRIBUTE );
                
                if(nc.namespaceURI.length()==0 && nc.localName.equals("xmlns"))
                    reader.reportError( RELAXNGReader.ERR_XMLNS_ATTRIBUTE );
                return null;
            }
            public Object onNsName( NamespaceNameClass nc ) {
                if(nc.namespaceURI.equals(infosetURI))
                    reader.reportError( RELAXNGReader.ERR_INFOSET_URI_ATTRIBUTE );
                return null;
            }
            public Object onNot( NotNameClass nc ) {
                nc.child.visit(this);
                return null;
            }
            public Object onDifference( DifferenceNameClass nc ) {
                nc.nc1.visit(this); nc.nc2.visit(this);
                return null;
            }
            public Object onChoice( ChoiceNameClass nc ) {
                nc.nc1.visit(this); nc.nc2.visit(this);
                return null;
            }
        });
    }
}
