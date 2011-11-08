/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.grammar;

import org.xml.sax.SAXNotRecognizedException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;

/**
 * Implementation of ElementDecl interface by MSV grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public class DeclImpl
    implements    org.iso_relax.dispatcher.ElementDecl,
                org.iso_relax.dispatcher.AttributesDecl,
                java.io.Serializable {
    /** "meat" of this Rule. */
    public final Expression exp;
    
    /** name of this rule */
    protected final String name;
    
    public DeclImpl( ReferenceExp exp ) {
        this( exp.name, exp.exp );
    }
    public DeclImpl( String name, Expression exp ) {
        this.exp=exp;
        this.name=name;
    }
    
    public String getName() { return name; }
    
    public boolean getFeature( String feature ) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(feature);
    }
    
    public Object getProperty( String property ) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(property);
    }
}
