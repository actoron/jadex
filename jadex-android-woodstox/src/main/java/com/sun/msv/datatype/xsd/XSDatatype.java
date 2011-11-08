/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.msv.datatype.SerializationContext;

import java.io.Serializable;

/**
 * Publicly accesible interface of W3C XML Schema datatype (simple type).
 * 
 * <p>
 * The most important methods are defined in the
 * <code>org.relaxng.datatype.Datatype</code> interface.
 * This interface provides additional information which is not covered by
 * {@link org.relaxng.datatype.Datatype} interface.
 * Also, this interface provides various simple type component properties
 * which are defined in the spec.
 * 
 * <p>
 * Application can use this interface to interact with datatype objects.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface XSDatatype extends Serializable, DatabindableDatatype {

    /**
     * gets the local name of the type.
     * If this type is an anonymous type, then this method returns null.
     * 
     * <p>
     * In the terminology of the spec, this method returns
     * the <a href="http://www.w3.org/TR/xmlschema-1/#st-name">name</a> property of
     * this simple type component. If the name property is
     * <a href="http://www.w3.org/TR/xmlschema-1/#key-null">absent</a>,
     * this method returns null.
     */
    String getName();
    
    /**
     * Gets the namespace URI of this simple type definition.
     */
    String getNamespaceUri();
    
    /**
     * gets the displayable name of this type.
     * This method always return something. It is useful to provide a message to the user.
     * 
     * <p>
     * This method is an ad-hoc method and there is no corresponding property in the spec.
     */
    String displayName();
    
    /**
     * converts value object back to the corresponding value in the lexical space.
     * 
     * <p>
     * This method does the reverse operation of the createValue method.
     * The returned string is not necessarily the canonical representation.
     * 
     * Also note that the implementation may accept invalid values without throwing
     * IllegalArgumentException. To make sure that the result is actually a valid
     * representation, call the isValid method.
     * 
     * <p>
     * Be careful not to confuse this method with
     * The serializeJavaObject method, which is defined in
     * the {@link DatabindableDatatype} method.
     * 
     * @param context
     *        Context information that will be possibly used for the conversion.
     *        Currently, this object is used only by QName, but may be extended
     *        in the future.
     * 
     * @exception IllegalArgumentException
     *        if the type of the given object is not the expected one.
     *        For example, this exception is thrown if you pass a BigInteger object
     *        to the "gYear" type.
     */
    String convertToLexicalValue( Object valueObject, SerializationContext context ) throws IllegalArgumentException;

    
    
    
    /**
     * gets the <a href="http://www.w3.org/TR/xmlschema-1/#st-base_type_definition">variety</a>
     * of this simple type.
     * 
     * @return
     *        VARIETY_ATOMIC, VARIETY_LIST, or VARIETY_UNION.
     */
    int getVariety();
    
    public static final int VARIETY_ATOMIC    = 1;
    public static final int VARIETY_LIST    = 2;
    public static final int VARIETY_UNION    = 3;
    
    
    /**
     * checks if this type is declared as final for the specified kind of derivation.
     * 
     * <p>
     * In the terminology of the spec, this method can be used to examine
     * the <a href="http://www.w3.org/TR/xmlschema-1/#st-final">final</a> property of
     * this component.
     * 
     * @param derivationType
     *        one of pre-defined values (DERIVATION_BY_XXX).
     */
    boolean isFinal( int derivationType );
    
    public static final int DERIVATION_BY_RESTRICTION        = 0x01;
    public static final int DERIVATION_BY_LIST                = 0x02;
    public static final int DERIVATION_BY_UNION                = 0x04;

    /**
     * indicates the specified facet is applicable to this type.
     * One of the possible return value from the isFacetApplicable method.
     */
    static final int APPLICABLE = 0;
    /**
     * indicates the specified facet is fixed in this type and
     * therefore not appliable.
     * One of the possible return value from the isFacetApplicable method.
     */
    static final int FIXED        = -1;
    /**
     * indicates the specified facet is not appliable to this type by definition.
     * One of the possible return value from the isFacetApplicable method.
     */
    static final int NOT_ALLOWED= -2;
    /**
     * returns if the specified facet is applicable to this datatype.
     * 
     * @return
     * <dl>
     *  <dt>APPLICABLE        <dd>if the facet is applicable
     *    <dt>FIXED            <dd>if the facet is already fixed (that is,not applicable)
     *    <dt>NOT_ALLOWED        <dd>if the facet is not applicable to this datatype at all.
     *                            this value is also returned for unknown facets.
     */
    public int isFacetApplicable( String facetName );

    /**
     * Gets the names of all applicable facets.
     */
    public String[] getApplicableFacetNames();

    // well-known facet name constants
    final static String    FACET_LENGTH            = "length";
    final static String    FACET_MINLENGTH            = "minLength";
    final static String    FACET_MAXLENGTH            = "maxLength";
    final static String    FACET_PATTERN            = "pattern";
    final static String    FACET_ENUMERATION        = "enumeration";
    final static String    FACET_TOTALDIGITS        = "totalDigits";
    final static String    FACET_FRACTIONDIGITS    = "fractionDigits";
    final static String    FACET_MININCLUSIVE        = "minInclusive";
    final static String    FACET_MAXINCLUSIVE        = "maxInclusive";
    final static String    FACET_MINEXCLUSIVE        = "minExclusive";
    final static String    FACET_MAXEXCLUSIVE        = "maxExclusive";
    final static String    FACET_WHITESPACE        = "whiteSpace";



    
    /**
     * gets the facet object that restricts the specified facet.
     * 
     * This method can be used to access various details of how
     * facets are applied to this datatype.
     *
     * @return null
     *        if no such facet object exists.
     */
    public DataTypeWithFacet getFacetObject( String facetName );

    /**
     * gets the base type of this type.
     * 
     * This method returns null if this object represents the simple ur-type.
     * 
     * <p>
     * This method is intended to capture the semantics of the
     * <a href="http://www.w3.org/TR/xmlschema-1/#st-base_type_definition">base type definition</a>
     * property of the simple type component, but there is an important difference.
     * 
     * <p>
     * Specifically, if you derive a type D from another type B, then
     * calling D.getBaseType() does not necessarily return B. Instead,
     * it may return an intermediate object (that represents a facet).
     * Calling the getBaseType method recursively will eventually return
     * B.
     */
    public XSDatatype getBaseType();
    
    /**
     * Gets the nearest ancestor built-in type.
     * 
     * <p>
     * This method traverses the inheritance chain from this datatype
     * to the root type (anySimpleType) and return the first built-in
     * type it finds.
     * 
     * <p>
     * For example, if you derive a type Foo from NCName and Bar from Foo,
     * then this method returns NCName. 
     * 
     * @return
     *      Always return non-null valid object.
     */
    public XSDatatype getAncestorBuiltinType();
    
    /**
     * tests if this type is a derived type of the specified type.
     * 
     * <p>
     * This method is an implementation of 
     * <a href="http://www.w3.org/TR/xmlschema-1/#cos-st-derived-ok">"Type Derivation OK (Simple)"</a>
     * of the spec. Therefore use caution if what you want is a casual method
     * because this method may cause a lot of unintuitive result.
     * 
     * <p>
     * <b>Note to implementors</b> Use the static version of this method defined
     * in the XSDatatypeImpl class. You don't need to implement this method from scratch.
     * 
     * @param    restrictionAllowed
     *        This test needs "a subset of {extension,restriction,list,union}
     *        (of which only restriction is actually relevant). If this flag is
     *        set to true, this method behaves as if the empty set is passed as the set.
     *        This is usually what you want if you're simply trying to check the
     *        derivation relationship.
     * 
     *        <p>
     *        If this flag is set to false, this method behaves as if {restriction}
     *        is passed as the set.
     */
    public boolean isDerivedTypeOf( XSDatatype baseType, boolean restrictionAllowed );
    
    /**
     * Returns true if this datatype is known to accept any string.
     * This is just a hint that allows the client code to do
     * certain optimization.
     * 
     * <p>
     * This method can return false even if the datatype actually accepts
     * any string. That is, it's perfectly OK for any datatype to return
     * false from this method.
     */
    public boolean isAlwaysValid();
    
    
    public static final String XMLSCHEMA_NSURI =
        "http://www.w3.org/2001/XMLSchema";
}
