/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.grammar.relax;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;

import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;

/**
 * place holder for &lt;anyOtherElement&gt; of RELAX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public class AnyOtherElementExp extends ElementExp implements ElementDecl {
    
    public NameClass getNameClass() { return nameClass; }
    public String getName() { return "anyOtherElement:"+nameClass; }

    /**
     * this name class matches all the namespaces that are accepted by this anyOtherElement.
     * this field is set by bind method.
     */
    public NameClass nameClass;
    
    /**
     * where did this reference is written in the source file.
     * can be set to null (to reduce memory usage) at anytime.
     */
    public transient Locator source;
    
    public final String includeNamespace;
    public final String excludeNamespace;
    
    /**
     * creates "skelton" of AnyOtherElement.
     * 
     * pseudo content model and name class must be supplied separately.
     */
    public AnyOtherElementExp( Locator loc,
        String includeNamespace, String excludeNamespace ) {
        // set content model to nullSet
        // to make this elementExp accept absolutely nothing.
        
        // "ignoreUndeclaredAttributes" parameter is meaningless here
        // because validation of this element is performed by
        // AnyOtherElementVerifier and it doesn't care about this flag.
        super(Expression.nullSet,true);
        
        this.source = loc;
        this.includeNamespace = includeNamespace;
        this.excludeNamespace = excludeNamespace;
        
        if( includeNamespace==null && excludeNamespace==null )
            throw new IllegalArgumentException();
        if( includeNamespace!=null && excludeNamespace!=null )
            throw new IllegalArgumentException();
    }
    
    /** creates pseudo content model and name class.
     * 
     * This function is called by RELAXIslandSchema object.
     * Therefore, line information is not automatically available
     * when reporting error.
     * Implementator should keep this in mind and manually pass Locator to reportError method.
     */
    protected void wrapUp( Grammar owner, Expression pseudoContentModel, SchemaProvider provider, ErrorHandler errorHandler )
                            throws SAXException {
        StringTokenizer st;
        if( includeNamespace!=null )
            st = new StringTokenizer(includeNamespace);
        else
            st = new StringTokenizer(excludeNamespace);
        
        NameClass nc =null;
        
        while(st.hasMoreTokens()) {
            String uri = st.nextToken();
            
            if(uri.equals("##local"))    uri="";
            
            if( provider.getSchemaByNamespace(uri)!=null ) {
                // one cannot specify defined URI.
                errorHandler.warning( new SAXParseException(
                    Localizer.localize( Localizer.WRN_ANYOTHER_NAMESPACE_IGNORED, uri ), source ) );
                continue;
            }
            
            NamespaceNameClass nsnc = new NamespaceNameClass(uri);
            if( nc==null )    nc = nsnc;
            else            nc = new ChoiceNameClass(nc,nsnc);
        }
        
        if( excludeNamespace!=null )
        {
            // in case of 'excludeNamespace',
            // all defined namespace is also considered as illegal.
            
            Iterator<?> itr = provider.iterateNamespace();
            while( itr.hasNext() )
            {
                NamespaceNameClass nsnc = new NamespaceNameClass( (String)itr.next() );
                if( nc==null )    nc = nsnc;
                else            nc = new ChoiceNameClass(nc,nsnc);
            }
            
            nc = new NotNameClass(nc);
        }
        
        this.nameClass = nc;
        
        // provide pseudo-content model.
        this.contentModel = 
            owner.getPool().createMixed(
                owner.getPool().createZeroOrMore(
                    owner.getPool().createChoice(this,pseudoContentModel) ) );
    }
    
    public boolean getFeature( String feature ) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(feature);
    }
    
    public Object getProperty( String property ) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(property);
    }
}
