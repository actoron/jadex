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

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeStreamingValidator;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.helpers.StreamingValidatorImpl;

import java.util.Vector;

/**
 * base implementaion for XSDatatype interface.
 * 
 * <p>
 * This class should be considered as the implementation-detail, and 
 * applications should not access this class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class XSDatatypeImpl implements XSDatatype {
    
    private final String namespaceUri;
    public String getNamespaceUri() { return namespaceUri; }
    
    private final String typeName;
    public String getName()    { return typeName; }
    
    /** this field characterizes how this datatype treats white space. */
    public final WhiteSpaceProcessor whiteSpace;
    
    protected XSDatatypeImpl( String uri, String typeName, WhiteSpaceProcessor whiteSpace ) {
        this.namespaceUri = uri;
        this.typeName    = typeName;
        this.whiteSpace    = whiteSpace;
    }

    final public Object createValue( String lexicalValue, ValidationContext context ) {
        return _createValue(whiteSpace.process(lexicalValue),context);
    }
    
    
    /**
     * converts a whitespace-processed lexical value into the corresponding value object
     */
    abstract protected Object _createValue( String content, ValidationContext context );

    
    final public void checkValid(String content, ValidationContext context) throws DatatypeException {
        _checkValid(whiteSpace.process(content),context);
    }
    
    /** actual 'meat' of the checkValid method */
    abstract protected void _checkValid(String content, ValidationContext context) throws DatatypeException;


    final public Object createJavaObject( String literal, ValidationContext context ) {
        return _createJavaObject(whiteSpace.process(literal),context);
    }
    
    abstract protected Object _createJavaObject( String literal, ValidationContext context );
    

    final public boolean isValid( String literal, ValidationContext context ) {
        // step.1 white space processing
        literal = whiteSpace.process(literal);
        
        if( needValueCheck() )
            // constraint facet that needs computation of value is specified.
            return _createValue(literal,context)!=null;
        else
            // lexical validation is enough.
            return checkFormat(literal,context);
    }
    
    // default implementation
    public boolean isAlwaysValid() { return false; }
    
    public DatatypeStreamingValidator createStreamingValidator( ValidationContext context ) {
        return new StreamingValidatorImpl(this,context);
    }
    
    abstract protected boolean checkFormat( String literal, ValidationContext context );
    protected boolean needValueCheck() { return false; }
    
    /**
     * gets the facet object that restricts the specified facet
     *
     * @return null
     *        if no such facet object exists.
     */
    public DataTypeWithFacet getFacetObject( String facetName ) {
        XSDatatype dt = getBaseType();
        if(dt!=null)    return dt.getFacetObject(facetName);
        else              return null;
    }

    public String[] getApplicableFacetNames() {
        Vector vec = new Vector();
        String[] facetNames = new String[]{
                FACET_ENUMERATION,
                FACET_FRACTIONDIGITS,FACET_TOTALDIGITS,
                FACET_LENGTH,FACET_MINLENGTH,FACET_MAXLENGTH,
                FACET_MAXEXCLUSIVE,FACET_MINEXCLUSIVE,
                FACET_MAXINCLUSIVE,FACET_MININCLUSIVE,
                FACET_PATTERN,
                FACET_WHITESPACE};
        
        for( int i=0; i<facetNames.length; i++ )
            if(isFacetApplicable(facetNames[i])==APPLICABLE)
                vec.add(facetNames[i]);
        
        return (String[])vec.toArray(new String[vec.size()]);
    }
    
    /**
     * gets the concrete type object of the restriction chain.
     */
    abstract public ConcreteType getConcreteType();
    
    
    public final boolean sameValue( Object o1, Object o2 ) {
        if(o1==null || o2==null)    return false;
        return o1.equals(o2);
    }
    public final int valueHashCode( Object o ) {
        return o.hashCode();
    }


    
    public final boolean isDerivedTypeOf( XSDatatype baseType, boolean restrictionAllowed ) {
        return isDerivedTypeOf(baseType,this,restrictionAllowed);
    }
    
    /**
     * an implementation of 
     * <a href="http://www.w3.org/TR/xmlschema-1/#cos-st-derived-ok">"Type Derivation OK (Simple)"</a>
     * of the spec.
     * 
     * @see #isDerivedTypeOf(XSDatatype,boolean)
     */
    public static boolean isDerivedTypeOf( XSDatatype base, XSDatatype derived, boolean restrictionAllowed ) {

        if( base==derived )            return true;
        if( !restrictionAllowed )    return false;

        if( base==SimpleURType.theInstance )    return true;
        
        if( base.getVariety()==VARIETY_UNION ) {
            // if the base type is an union variety,
            // type derivation is OK if "derived" is derived from one of member types.
            XSDatatype t = base;
            while(!(t instanceof UnionType))
                t = t.getBaseType();
            XSDatatypeImpl[] memberTypes = ((UnionType)t).memberTypes;
            for( int i=0; i<memberTypes.length; i++ )
                if( isDerivedTypeOf( memberTypes[i], derived, restrictionAllowed ) )
                    return true;
        }
        
        while( derived!=SimpleURType.theInstance ) {
            if( base==derived )
                return true;
            derived = derived.getBaseType();
        }
        
        return false;
    }
    
    public XSDatatype getAncestorBuiltinType() {
        XSDatatype dt = this;
        
        while( !XMLSCHEMA_NSURI.equals(dt.getNamespaceUri()) )
            dt = dt.getBaseType();
        return dt;
    }

    
    /**
     * A property for RELAX NG DTD compatibility datatypes.
     * <code>ID_TYPE_NULL</code> is returned by default.
     */
    public int getIdType() {
        return ID_TYPE_NULL;
    }
    
    /**
     * A property for RELAX NG DTD compatibility datatypes.
     * Context-independent by default.
     */
    public boolean isContextDependent() {
        return false;
    }
    
    
    protected static final ValidationContext serializedValueChecker =
        new ValidationContext(){
            public boolean isNotation( String s ) { return true; }
            public boolean isUnparsedEntity( String s ) { return true; }
            public String resolveNamespacePrefix( String ns ) { return "abc"; }
            public String getBaseUri() { return null; }
        };
        

    
    public static String localize( String prop, Object[] args ) {
        return java.text.MessageFormat.format(
            java.util.ResourceBundle.getBundle("com.sun.msv.datatype.xsd.Messages").getString(prop),
            args );
    }
    
    public static String localize( String prop ) {
        return localize( prop, null );
    }
    public static String localize( String prop, Object arg1 ) {
        return localize( prop, new Object[]{arg1} );
    }
    public static String localize( String prop, Object arg1, Object arg2 ) {
        return localize( prop, new Object[]{arg1,arg2} );
    }
    public static String localize( String prop, Object arg1, Object arg2, Object arg3 ) {
        return localize( prop, new Object[]{arg1,arg2,arg3} );
    }
    
    
    public static final String ERR_INAPPROPRIATE_FOR_TYPE =
        "DataTypeErrorDiagnosis.InappropriateForType";
    public static final String ERR_TOO_MUCH_PRECISION =
        "DataTypeErrorDiagnosis.TooMuchPrecision";
    public static final String ERR_TOO_MUCH_SCALE =
        "DataTypeErrorDiagnosis.TooMuchScale";
    public static final String ERR_ENUMERATION =
        "DataTypeErrorDiagnosis.Enumeration";
    public static final String ERR_ENUMERATION_WITH_ARG =
        "DataTypeErrorDiagnosis.Enumeration.Arg";
    public static final String ERR_OUT_OF_RANGE =
        "DataTypeErrorDiagnosis.OutOfRange";
    public static final String ERR_LENGTH =
        "DataTypeErrorDiagnosis.Length";
    public static final String ERR_MINLENGTH =
        "DataTypeErrorDiagnosis.MinLength";
    public static final String ERR_MAXLENGTH =
        "DataTypeErrorDiagnosis.MaxLength";
    public static final String ERR_PATTERN_1 =
        "DataTypeErrorDiagnosis.Pattern.1";
    public static final String ERR_PATTERN_MANY =
        "DataTypeErrorDiagnosis.Pattern.Many";


    
    public static final String ERR_INVALID_ITEMTYPE =
        "BadTypeException.InvalidItemType";
    public static final String ERR_INVALID_MEMBER_TYPE =
        "BadTypeException.InvalidMemberType";
    public static final String ERR_INVALID_BASE_TYPE =
        "BadTypeException.InvalidBaseType";
    public static final String ERR_INVALID_WHITESPACE_VALUE =
        "WhiteSpaceProcessor.InvalidWhiteSpaceValue";
    public static final String ERR_PARSE_ERROR = "PatternFacet.ParseError";
    
    public static final String ERR_INVALID_VALUE_FOR_THIS_TYPE =
        "EnumerationFacet.InvalidValueForThisType";
    public final static String ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER =
        "BadTypeException.FacetMustBeNonNegativeInteger";
    public final static String ERR_FACET_MUST_BE_POSITIVE_INTEGER =
        "BadTypeException.FacetMustBePositiveInteger";
    public final static String ERR_OVERRIDING_FIXED_FACET =
        "BadTypeException.OverridingFixedFacet";
    public final static String ERR_INCONSISTENT_FACETS_1 =
        "InconsistentFacets.1";
    public final static String ERR_INCONSISTENT_FACETS_2 =
        "InconsistentFacets.2";
    public final static String ERR_X_AND_Y_ARE_EXCLUSIVE =
        "XAndYAreExclusive";
    public final static String ERR_LOOSENED_FACET =
        "LoosenedFacet";
    public final static String ERR_SCALE_IS_GREATER_THAN_PRECISION =
        "PrecisionScaleFacet.ScaleIsGraterThanPrecision";
    public static final String ERR_DUPLICATE_FACET =
        "BadTypeException.DuplicateFacet";
    public static final String ERR_NOT_APPLICABLE_FACET =
        "BadTypeException.NotApplicableFacet";
    public static final String ERR_EMPTY_UNION =
        "BadTypeException.EmptyUnion";


    // serialization support
    private static final long serialVersionUID = 1;    
}
