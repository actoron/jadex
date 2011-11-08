/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

import java.util.Vector;

import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.util.ExpressionWalker;

/**
 * Element declaration.
 * 
 * <p>
 * the inherited exp field holds an expression that
 * also matches to substituted element declarations.
 * 
 * <p>
 * The <code>body</code> field contains an expression that matches
 * only to this element declaration without no substituted element decls.
 * 
 * 
 * <h2>Element Declaration Schema Component Properties</h2>
 * <p>
 * This table shows the mapping between
 * <a href="http://www.w3.org/TR/xmlschema-1/#Element_Declaration_details">
 * "element declaration schema component properties"</a>
 * (which is defined in the spec) and corresponding method/field of this class.
 * 
 * <table border=1>
 *  <thead><tr>
 *   <td>Property of the spec</td>
 *   <td>method/field of this class</td>
 *  </tr></thead>
 *  <tbody><tr>
 *   <td>
 *    name
 *   </td><td>
 *    The {@link #name} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    target namespace
 *   </td><td>
 *    the {@link #getTargetNamespace()} method.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    type definition
 *   </td><td>
 *    {@link #getTypeDefinition()} method.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    scope
 *   </td><td>
 *    <b>To be implemented</b>
 *   </td>
 *  </tr><tr>
 *   <td>
 *    value constraint
 *   </td><td>
 *    <b>To be implemented</b>.  Accessible through the {@link #body} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    nillable
 *   </td><td>
 *    the {@link #isNillable} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    identity constraints
 *   </td><td>
 *    The <code>identityConstraints</code> field of the {@link XSElementExp},
 *      which in turn can be obtained throught the {@link #body} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    substitution group affiliation
 *   </td><td>
 *    The {@link #substitutionAffiliation} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    substitution group exclusion
 *   </td><td>
 *    The {@link #finalValue} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    disallowed substitution
 *   </td><td>
 *    The {@link #block} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    abstract
 *   </td><td>
 *    the {@link #isAbstract()} method.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    annotation
 *   </td><td>
 *    Unaccessible. This information is removed during the parsing phase.
 *   </td>
 *  </tr></tbody>
 * </table>
 * 
 * 
 * 
 * <h3>Abstractness</h3>
 * 
 * <p>
 * The <code>exp</code> field and the <code>self</code> field are very similar.
 * In fact, the only difference is that the former is affected by the abstract
 * property, while the latter isn't.
 * 
 * <p>
 * So if it has to be affected by the
 * abstract property (like referencing a complex type as the element body),
 * you should use the <code>exp</code> field.
 * If you don't want to be affected by the abstract property
 * (like referencing a complex type as the base type of another complex type),
 * then you should refer to the <code>body</code> field.
 * 
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementDeclExp extends ReferenceExp
{
    public ElementDeclExp( XMLSchemaSchema schema, String typeLocalName ) {
        super(typeLocalName);
        this.parent = schema;
        this.substitutions = new ReferenceExp( typeLocalName+":substitutions" );
        this.substitutions.exp = Expression.nullSet;
    }

    /**
     * <a href="http://www.w3.org/TR/xmlschema-1/#class_exemplar">
     * The substitution group affiliation property</a>
     * of this component, if any.
     * Otherwise null.
     */
    public ElementDeclExp substitutionAffiliation;
    
    /**
     * The expression that represents the "body" of this expression.
     * Usually, this refers to XSElementExp, but not necessarily.
     */
    public final ReferenceExp body = new ReferenceExp(null);
        
    private XSElementExp element;
    
    public void setElementExp( XSElementExp exp ) {
        this.element = exp;
        body.exp = exp;
    }
    
    public XSElementExp getElementExp() { return element; }
    
    
    /**
     * choices of all elements that can validly substitute this element.
     */
    public final ReferenceExp substitutions;
    
    /**
     * gets the pattern that represents the content model of
     * this element declaration.
     * 
     * This method is just a short cut for <code>self.contentModel</code>.
     */
    public Expression getContentModel() {
        return element.contentModel;
    }
    
    /** parent XMLSchemaSchema object to which this object belongs. */
    public final XMLSchemaSchema parent;
    
    
    /**
     * XML Schema version of {@link ElementExp}.
     * 
     * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
     */
    @SuppressWarnings("serial")
    public static class XSElementExp extends ElementExp {

        public final SimpleNameClass elementName;
        public final NameClass getNameClass() { return elementName; }
        // 05-Oct-2010, tatu: if element has "default" value, it is stored here:
        public final String defaultValue;
        
        public XSElementExp(ElementDeclExp parent, SimpleNameClass elementName, Expression contentModel, String defaultValue ) {
            super(contentModel,false);
            this.elementName = elementName;
            this.defaultValue = defaultValue;
            this.parent = parent;
        }
        
        /**
         * identity constraints associated to this declaration.
         * When no constraint exists, this field may be null (or empty vector).
         * Items are of derived types of {@link IdentityConstraint} class.
         * 
         * <p>
         * These identity constraints are not enforced by the default Verifier
         * implementation.
         */
        public final Vector<Object> identityConstraints = new Vector<Object>();
        
        public final ElementDeclExp parent;
    }
    
//
// Schema component properties
//======================================
//
    /**
     * gets the nillable property of this component as
     * <a href="http://www.w3.org/TR/xmlschema-1/#nillable">
     * specified in the spec</a>.
     */
    public boolean isNillable;
    
    
    /**
     * gets the scope property of this component as
     * <a href="http://www.w3.org/TR/xmlschema-1/#e-scope">
     * specified in the spec</a>.
     * 
     * @return
     *        <b>true</b> if this component is global.
     *        <b>false</b> if this component is local.
     */
    public boolean isGlobal() {
        return parent.elementDecls.get(name)==this;
    }
    
    /**
     * gets the target namespace property of this component as
     * <a href="http://www.w3.org/TR/xmlschema-1/#e-target_namespace">
     * specified in the spec</a>.
     * 
     * <p>
     * If the property is <a href="http://www.w3.org/TR/xmlschema-1/#key-null">
     * absent</a>, then this method returns the empty string.
     * 
     * <p>
     * This method is just a shortcut for <code>parent.targetNamespace</code>.
     */
    public final String getTargetNamespace() {
        return parent.targetNamespace;
    }
    
    /**
     * checks if this element declaration is abstract.
     * 
     * @return
     *        true if this method is abstract.
     */
    public boolean isAbstract() {
        if( exp instanceof ChoiceExp ) {
            ChoiceExp cexp = (ChoiceExp)exp;
            if(cexp.exp1!=body && cexp.exp2!=body)
                throw new Error();    // assertion failed
            return true;
        }
        
        if(exp!=substitutions)
            throw new Error();    // assertion failed
        return false;
    }
    
    public void setAbstract( boolean isAbstract ) {
        if(isAbstract)    exp = substitutions;
        else            exp = parent.pool.createChoice( substitutions, body );
    }

    
    public static final int RESTRICTION    = 0x1;
    public static final int EXTENSION    = 0x2;
    public static final int SUBSTITUTION   = 0x4;
    public static final int ALL             = 0x7;

    /**
     * The <a href="http://www.w3.org/TR/xmlschema-1/#e-final">
     * substitution group exclusions property</a> of this schema component,
     * implemented as a bit field.
     * 
     * <p>
     * a bit-wise OR of RESTRICTION and EXTENSION.
     */
    public int finalValue =0;
    
    /**
     * The <a href="http://www.w3.org/TR/xmlschema-1/#e-exact">
     * disallowed substitution property</a> of this schema component,
     * implemented as a bit field.
     * 
     * <p>
     * a bit-wise OR of RESTRICTION, EXTENSION, and SUBSTITUTION.
     */
    public int block =0;
    
    public boolean isSubstitutionBlocked() { return (block&SUBSTITUTION)!=0; }
    public boolean isRestrictionBlocked() { return (block&RESTRICTION)!=0; }
    
    
    /**
     * gets the <a href="http://www.w3.org/TR/xmlschema-1/#type_definition">
     * type definition property</a> of this schema component.
     */
    public XMLSchemaTypeExp getTypeDefinition() {
        final RuntimeException eureka = new RuntimeException();
        final XMLSchemaTypeExp[] result = new XMLSchemaTypeExp[1];
        try {
            getContentModel().visit( new ExpressionWalker(){
                public void onElement( ElementExp exp ) {}
                public void onRef( ReferenceExp exp ) {
                    if(exp instanceof XMLSchemaTypeExp) {
                        result[0] = (XMLSchemaTypeExp)exp;
                        throw eureka;
                    }
                    super.onRef(exp);
                }
            });
            // assertion failed. It couldn't be found.
            throw new Error();
        } catch( RuntimeException e ) {
            if(e==eureka)    return result[0];
            throw e;
        }
    }
    
//
// Implementation details
//=========================================
    public boolean isDefined() {
        return super.isDefined() && element!=null;
    }
    
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
