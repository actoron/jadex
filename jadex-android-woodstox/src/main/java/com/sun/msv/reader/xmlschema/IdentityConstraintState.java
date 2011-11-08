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

import java.util.StringTokenizer;
import java.util.Vector;

import org.xml.sax.Locator;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.xmlschema.Field;
import com.sun.msv.grammar.xmlschema.IdentityConstraint;
import com.sun.msv.grammar.xmlschema.KeyConstraint;
import com.sun.msv.grammar.xmlschema.KeyRefConstraint;
import com.sun.msv.grammar.xmlschema.UniqueConstraint;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.grammar.xmlschema.XPath;
import com.sun.msv.reader.ChildlessState;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * used to parse &lt;unique&gt;,&lt;key&gt;, and &lt;keyref&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IdentityConstraintState extends SimpleState {
    
    protected XPath[] selector;
    protected final Vector<Field> fields = new Vector<Field>();
    
    protected State createChildState( StartTagInfo tag ) {
    
        if(tag.localName.equals("selector")) {
            String v = tag.getAttribute("xpath");
            if(v!=null)
                selector = parseSelector(v);
            else {
                reader.reportError(XMLSchemaReader.ERR_MISSING_ATTRIBUTE, "selector", "xpath" );
                selector = new XPath[0];    // recover by providing a dummy selector
            }
            
            return new ChildlessState();
        }
        if(tag.localName.equals("field")) {
            String v = tag.getAttribute("xpath");
            if(v!=null)
                fields.add( parseField(v) );
            else {
                reader.reportError(XMLSchemaReader.ERR_MISSING_ATTRIBUTE, "field", "xpath" );
                // recover by ignoring this field.
            }
            return new ChildlessState();
        }
        
        return null;
    }
    
    protected void endSelf() {
        createIdentityConstraint();
        super.endSelf();
    }
    
    protected void createIdentityConstraint() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        IdentityConstraint id;
        
        String name = startTag.getAttribute("name");
        if(name==null) {
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE,
                startTag.localName, "name" );
            return;    // recover by ignoring this constraint.
        }
        
        Field[] fs = (Field[])fields.toArray( new Field[fields.size()] );
        
        if( startTag.localName.equals("key") )
            id = new KeyConstraint( reader.currentSchema.targetNamespace, name, selector, fs );
        else
        if( startTag.localName.equals("unique") )
            id = new UniqueConstraint( reader.currentSchema.targetNamespace, name, selector, fs );
        else
        if( startTag.localName.equals("keyref") ) {
            final String refer = startTag.getAttribute("refer");
            if(refer==null) {
                reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE,
                    startTag.localName, "refer" );
                return;    // recover by ignoring this constraint.
            }
            final String[] qn = reader.splitQName(refer);
            if(qn==null) {
                reader.reportError( XMLSchemaReader.ERR_UNDECLARED_PREFIX, qn );
                return;
            }
            
            final KeyRefConstraint keyRef = 
                new KeyRefConstraint( reader.currentSchema.targetNamespace, name, selector, fs );
            id = keyRef;
            
            // back patch "key" field of KeyRefConstraint.
            reader.addBackPatchJob( new GrammarReader.BackPatch(){
                public State getOwnerState() { return IdentityConstraintState.this; }
                public void patch() {
                    XMLSchemaSchema s = reader.grammar.getByNamespace(qn[0]);
                    if(s==null) {
                        reader.reportError( XMLSchemaReader.ERR_UNDEFINED_SCHEMA, qn[0] );
                        return;
                    }
                    IdentityConstraint idc = s.identityConstraints.get(qn[1]);
                    if(idc==null) {
                        reader.reportError( XMLSchemaReader.ERR_UNDEFINED_KEY, refer );
                        return;
                    }
                    if(!(idc instanceof KeyConstraint )) {
                        reader.reportError( XMLSchemaReader.ERR_KEYREF_REFERRING_NON_KEY, refer );
                        return;
                    }
                    if( idc.fields.length != keyRef.fields.length ) {
                        reader.reportError(
                            new Locator[]{
                                getLocation(),
                                reader.getDeclaredLocationOf(idc) },
                            XMLSchemaReader.ERR_KEY_FIELD_NUMBER_MISMATCH,
                            new Object[]{
                                idc.localName,
                                new Integer(idc.fields.length),
                                keyRef.localName,
                                new Integer(keyRef.fields.length) } );
                        return;
                    }
                    
                    keyRef.key = (KeyConstraint)idc;
                }
            });
        } else
            // this state can be used only when local name is "key","keyref", or "unique".
            throw new Error();
        
        if( reader.currentSchema.identityConstraints.get(name)!=null ) {
            reader.reportError(
                new Locator[]{ this.location, reader.getDeclaredLocationOf(id) },
                XMLSchemaReader.ERR_DUPLICATE_IDENTITY_CONSTRAINT_DEFINITION,
                new Object[]{name} );
        } else {
            reader.currentSchema.identityConstraints.add(name,id);
        }
        reader.setDeclaredLocationOf(id);
        ((ElementDeclState)parentState).onIdentityConstraint(id);
    }

    protected XPath[] parseSelector( String xpath ) {
        final Vector<XPath> pathObjs = new Vector<XPath>();
        
        // split to A|B|C
        StringTokenizer paths = new StringTokenizer(xpath,"|");
        while(paths.hasMoreTokens()) {
            XPath pathObj = new XPath();
            pathObjs.add(pathObj);
            
            if(!parsePath(pathObj,paths.nextToken(),false))
                return new XPath[0];
        }
        
        return (XPath[])pathObjs.toArray(new XPath[pathObjs.size()]);
    }
    
    protected Field parseField( String xpath ) {
        final Vector<XPath> pathObjs = new Vector<XPath>();
        Field field = new Field();
        
        // split to A|B|C
        StringTokenizer paths = new StringTokenizer(xpath,"|");
        while(paths.hasMoreTokens()) {
            XPath pathObj = new XPath();
            pathObjs.add(pathObj);
            
            if(!parsePath(pathObj,paths.nextToken(),true))
                return new Field();    // recover by returning a dummy field.
        }
        
        field.paths = (XPath[])pathObjs.toArray(new XPath[pathObjs.size()]);
        return field;
    }
    
    
    /**
     * parses "aa/bb/cc/.../".
     * 
     * @return    true if it succeeds in parsing. Otherwise false.
     */
    protected boolean parsePath( XPath pathObj, String xpath, boolean parseField ) {
        final Vector<NameClass> stepObjs = new Vector<NameClass>();
        if(xpath.startsWith(".//")) {
            pathObj.isAnyDescendant = true;
            xpath = xpath.substring(3);
        }
            
        // split to X/Y/Z
        StringTokenizer steps = new StringTokenizer(xpath,"/");
        stepObjs.clear();
        while(steps.hasMoreTokens()) {
            String step = steps.nextToken();
            if( step.equals(".") )    continue;
                
            if( step.equals("*") ) {
                stepObjs.add( NameClass.ALL );
                continue;
            }
            
            boolean attribute = false;
            
            if( step.charAt(0)=='@' && parseField && !steps.hasMoreTokens()) {
                // attribute step is allowed only when parsing a field XPath
                // and as the last token.
                attribute = true;
                step = step.substring(1);
            } // otherwise the following statement will fail to parse this token.
                
            // resolve QName.
            String[] qn = reader.splitQName(step);
            if(qn==null) {
                // failed to resolve QName properly
                reader.reportError( XMLSchemaReader.ERR_BAD_XPATH, step );
                return false;
            }
            
            if( attribute && step.indexOf(':')<0 )
                qn[0] = "";    // if this is an attribute and step is NCName,
                            // then its namespace URI is "", rather than the
                            // default namespace.
            
            NameClass nc;
            
            // TODO: NCName test.
            if( qn[1].equals("*") )        nc = new NamespaceNameClass(qn[0]);
            else                        nc = new SimpleNameClass(qn[0],qn[1]);
            
            if( attribute==true )    pathObj.attributeStep = nc;
            else                    stepObjs.add(nc);
        }
            
        pathObj.steps = (NameClass[])stepObjs.toArray(new NameClass[stepObjs.size()]);
        return true;
    }
}
