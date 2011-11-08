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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;

/**
 * ComplexType definition.
 * 
 * ComplexTypeExp holds an expression (as a ReferenceExp) that matches to
 * this type itself.
 * 
 * <p>
 * the {@link #body} field contains the expression that exactly matches
 * to the declared content model (without any substitutable types).
 * 
 * <p>
 * the <code>exp</code> field contains the reference to the body field,
 * if this complex type is not abstract. If abstract, then nullSet is set.
 * You shouldn't directly manipulate the exp field. Instead, you should use
 * the {@link #setAbstract(boolean)} method to do it.
 * 
 * <p>
 * Note: The runtime type substitution
 * (the use of <code>xsi:type</code> attribute)
 * is implemented at the VGM layer. Therefore, AGMs of XML Schema does <b>NOT</b>
 * precisely represent what are actually allowed and what are not.
 * 
 * 
 * <h2>Complex Type Definition Schema Component Properties</h2>
 * <p>
 * This table shows the mapping between
 * <a href="http://www.w3.org/TR/xmlschema-1/#Complex_Type_Definition_details">
 * "complex type definition schema component properties"</a>
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
 *    abstract
 *   </td><td>
 *    the {@link #isAbstract()} method.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    base type definition
 *   </td><td>
 *    {@link #simpleBaseType} or {@link #complexBaseType} field,
 *    depending on whether the base type is a simple type or a complex type.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    derivation method
 *   </td><td>
 *    the {@link #derivationMethod} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    final
 *   </td><td>
 *    the {@link #finalValue} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    prohibited substitutions
 *   </td><td>
 *    the {@link #block} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    attribtue uses <br> attribute wildcard <br> content type
 *   </td><td>
 *    Not directly accessible. Can be found by walking
 *    the children of the {@link #body} field.
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
 * 
 * 
 * @see        ElementDeclExp
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ComplexTypeExp extends XMLSchemaTypeExp {
    
    public ComplexTypeExp( XMLSchemaSchema schema, String localName ) {
        super(localName);
        this.parent = schema;
        setAbstract(false);
    }
    
    
    /** actual content model definition + attribute uses. */
    public final ReferenceExp body = new ReferenceExp(null);
    
    /** attribute wildcard as an expression. */
    public final ReferenceExp attWildcard = new ReferenceExp(null,Expression.epsilon);
    
    
    /** parent XMLSchemaSchema object to which this object belongs. */
    public final XMLSchemaSchema parent;

    /**
     * Attribute wild card constraint.
     * 
     * <p>
     * Due to the nasty definition of the interaction between attribute wildcards,
     * we cannot add the expression for validating wildcard until the very last moment.
     * 
     * <p>
     * Until the wrap-up phase of the schema parsing, this field will contain
     * the "local wildcard definition." In the wrap-up phase, this field is replaced
     * by the "complete wildcard definition." 
     */
    public AttributeWildcard wildcard;
    
    public AttributeWildcard getAttributeWildcard() { return wildcard; }
    public void setAttributeWildcard( AttributeWildcard local ) { wildcard=local; }
    
    
//
// Schema component properties
//======================================
//
    
    /**
     * gets the target namespace property of this component as
     * <a href="http://www.w3.org/TR/xmlschema-1/#ct-target_namespace">
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
     * base type of this complex type.
     * 
     * Either baseComplexType field or baseSimpleType field is set.
     * If the base type is
     * <a href="http://www.w3.org/TR/xmlschema-1/#section-Built-in-Complex-Type-Definition">
     * ur-type</a>, both fields are set to null.
     * 
     * @see #simpleBaseType
     */
    public ComplexTypeExp complexBaseType;
    /**
     * base type of this complex type.
     * 
     * @see #complexBaseType
     */
    public XSDatatypeExp simpleBaseType;
    
    /**
     * the derivation method used to derive this complex type from the base type.
     * Either RESTRICTION or EXTENSION.
     * 
     * @see #complexBaseType
     *        #simpleBaseType
     */
    public int derivationMethod = -1;
    

    /**
     * checks if this complex type is abstract.
     * 
     * <p>
     * This method corresponds to the abstract property of
     * the complex type declaration schema component.
     * 
     * @return
     *        true if this method is abstract. Flase if not.
     */
    public boolean isAbstract() {
        return exp==Expression.nullSet;
    }
    public void setAbstract( boolean isAbstract ) {
        if( isAbstract )    exp=Expression.nullSet;
        else                exp=parent.pool.createSequence(body,attWildcard);
    }
    
    /**
     * Checks if this type is a derived type of the specified type.
     * 
     * <p>
     * This method is an implementation of
     * <a href="http://www.w3.org/TR/xmlschema-1/#cos-ct-derived-ok">
     * "Type Derivation OK (Complex)"</a> test
     * of the spec.
     * 
     * <p>
     * If you are not familiar with the abovementioned part of the spec,
     * <b>don't use this method</b>. This method probably won't give you
     * what you expected.
     * 
     * @param    constraint
     *        A bit field that represents the restricted derivation. This field
     *        must consists of bitwise and of {@link #EXTENSION} or {@link #RESTRICTION}.
     * 
     * @return
     *        true if the specified type is "validly derived" from this type.
     *        false if not.
     */
    public boolean isDerivedTypeOf( ComplexTypeExp baseType, int constraint ) {
        
        ComplexTypeExp derived = this;
        
        while( derived!=null ) {
            if( derived==baseType )        return true;
            
            if( (derived.derivationMethod&constraint)!=0 )
                return false;    // this type of derivation is prohibited.
            derived = derived.complexBaseType;
        }
        
        return false;
    }
    /**
     * @see #isDerivedTypeOf(ComplexTypeExp,int)
     */
    public boolean isDerivedTypeOf( XSDatatype baseType, int constraint ) {
        ComplexTypeExp derived = this;
        
        while(true) {
            if( derived.complexBaseType==null ) {
                if( derived.simpleBaseType!=null )
                    return derived.simpleBaseType.getCreatedType().isDerivedTypeOf(
                        baseType, (constraint&RESTRICTION)==0 );
                else
                    return false;
            }
            
            if( (derived.derivationMethod&constraint)!=0 )
                return false;    // this type of derivation is prohibited.
            
            derived = derived.complexBaseType;
        }
    }
    public boolean isDerivedTypeOf( XMLSchemaTypeExp exp, int constraint ) {
        if( exp instanceof ComplexTypeExp )
            return isDerivedTypeOf( (ComplexTypeExp)exp, constraint );
        else
            return isDerivedTypeOf( ((SimpleTypeExp)exp).getDatatype(), constraint );
    }


    /**
     * The <a href="http://www.w3.org/TR/xmlschema-1/#ct-final">
     * final property</a> of this schema component, implemented as a bit field.
     * 
     * <p>
     * 0, RESTRICTION, EXTENSION, or (RESTRICTION|EXTENSION).
     */
    public int finalValue =0;
    
    /**
     * The <a href="http://www.w3.org/TR/xmlschema-1/#ct-block">
     * block property</a> of this schema component, implemented as a bit field.
     * 
     * <p>
     * 0, RESTRICTION, EXTENSION, or (RESTRICTION|EXTENSION).
     */
    public int block =0;
    
    /**
     * Gets the value of the block constraint.
     * SimpleTypeExp always returns 0 because it doesn't have the block constraint.
     */
    public int getBlock() { return block; }

    
    
    
//
// Other implementation details
//======================================
//
    /** clone this object. */
    public RedefinableExp getClone() {
        ComplexTypeExp exp = new ComplexTypeExp(parent,super.name);
        exp.redefine(this);
        return exp;
    }

    public void redefine( RedefinableExp _rhs ) {
        super.redefine(_rhs);
        
        ComplexTypeExp rhs = (ComplexTypeExp)_rhs;
        body.exp = rhs.body.exp;
        attWildcard.exp = rhs.attWildcard.exp;
        complexBaseType = rhs.complexBaseType;
        simpleBaseType = rhs.simpleBaseType;
        derivationMethod = rhs.derivationMethod;
        finalValue = rhs.finalValue;
        block = rhs.block;
        if(rhs.wildcard==null)    wildcard = null;
        else                    wildcard = rhs.wildcard.copy();
        
        if( this.parent != rhs.parent )
            // those two must share the parent.
            throw new IllegalArgumentException();
    }

//    /** derives a QName type that only accepts this type name. */
/*    private static XSDatatype getQNameType( final String namespaceURI, final String localName ) {
        try {
            TypeIncubator ti = new TypeIncubator( QnameType.theInstance );
            ti.addFacet( "enumeration", "foo:"+localName, true,
                new ValidationContext() {
                    public String resolveNamespacePrefix( String prefix ) {
                        if( "foo".equals(prefix) )    return namespaceURI;
                        return null;
                    }
                    public boolean isUnparsedEntity( String entityName ) {
                        throw new Error();    // shall never be called.
                    }
                    public boolean isNotation( String notationName ) {
                        throw new Error();    // shall never be called.
                    }
                    public String getBaseUri() { return null; }
                } );
        
            return ti.derive(null);
        } catch( DatatypeException e ) {
            // assertion failed. this can't happen.
            throw new Error();
        }
    }
*/
    /**
     * implementation detail.
     * 
     * A ComplexTypeDecl is properly defined if its self is defined.
     * Note that the default implementation of the isDefined method doesn't 
     * work for this class because the exp field is set by the constructor.
     */
    public boolean isDefined() {
        return body.isDefined();
    }
        
    // serialization support
    private static final long serialVersionUID = 1;    
}
