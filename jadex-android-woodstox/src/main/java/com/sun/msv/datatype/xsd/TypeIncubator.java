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
import org.relaxng.datatype.ValidationContext;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * derives a new type by adding facets.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypeIncubator {

    /** storage for non-repeatable facets */
    private final Map impl = new java.util.HashMap();

    /** base type */
    private final XSDatatypeImpl baseType;

    public TypeIncubator(XSDatatype baseType) {
        this.baseType = (XSDatatypeImpl)baseType;
        if (baseType == null)
            throw new IllegalArgumentException();
    }

    /**
     * adds a facet to the type.
     *
     * @deprecated
     *        please use the addFacet method, which is better named.
     */
    public void add(String name, String strValue, boolean fixed, ValidationContext context) throws DatatypeException {
        addFacet(name, strValue, fixed, context);
    }

    /** adds a facet to the type.
     *
     * @exception    DatatypeException
     *        when given facet is already specified
     */
    public void addFacet(String name, String strValue, boolean fixed, ValidationContext context)
        throws DatatypeException {

        if (baseType instanceof ErrorType)
            return; // silently ignore any further error

        // checks applicability of the facet
        switch (baseType.isFacetApplicable(name)) {
            case XSDatatypeImpl.APPLICABLE :
                break;
            case XSDatatypeImpl.FIXED :
                // simply ignore this facet.
                return;
                // to issue an error, we need to first make sure that the
                // specified value is different from the fixed value.
                //            throw new DatatypeException( XSDatatypeImpl.localize(
                //                XSDatatypeImpl.ERR_OVERRIDING_FIXED_FACET, name ) );
            case XSDatatypeImpl.NOT_ALLOWED :
                throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_NOT_APPLICABLE_FACET, name));
            default :
                throw new Error(); // assertion failed
        }

        Object value;

        if (isValueFacet(name)) {
            value = baseType.createValue(strValue, context);
            if (value == null)
                throw new DatatypeException(
                    XSDatatypeImpl.localize(
                        XSDatatypeImpl.ERR_INVALID_VALUE_FOR_THIS_TYPE,
                        strValue,
                        baseType.displayName()));
        } else
            value = strValue;

        if (isRepeatable(name)) {
            FacetInfo fi;
            if (impl.containsKey(name))
                fi = (FacetInfo)impl.get(name);
            else
                impl.put(name, fi = new FacetInfo(new Vector(), fixed));

            ((Vector)fi.value).add(value);
            // TODO : what shall we do if
            // <enumeration value="a" fixed="true" />
            // <enumeration value="b" fixed="false" />
            fi.fixed |= fixed;
        } else {
            if (impl.containsKey(name))
                throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_DUPLICATE_FACET, name));
            impl.put(name, new FacetInfo(value, fixed));
        }
    }

    final static private String[][] exclusiveFacetPairs =
        new String[][] {
            new String[] { XSDatatypeImpl.FACET_LENGTH, XSDatatypeImpl.FACET_MINLENGTH },
            new String[] { XSDatatypeImpl.FACET_LENGTH, XSDatatypeImpl.FACET_MAXLENGTH },
            new String[] { XSDatatypeImpl.FACET_MAXINCLUSIVE, XSDatatypeImpl.FACET_MAXEXCLUSIVE },
            new String[] { XSDatatypeImpl.FACET_MININCLUSIVE, XSDatatypeImpl.FACET_MINEXCLUSIVE }
    };

    /** @deprecated */
    public XSDatatypeImpl derive(String newName) throws DatatypeException {
        return derive("", newName);
    }

    /**
     * derives a new datatype from a datatype by facets that were set.
     * 
     * It is completely legal to use null as the newTypeName parameter,
     * which means the derivation of an anonymous datatype.
     *
     * @exception DatatypeException
     *        DatatypeException is thrown if derivation is somehow invalid.
     *        For example, not applicable facets are applied, or enumeration
     *        has invalid values, ... things like that.
     */
    public XSDatatypeImpl derive(String newNameUri, String newLocalName) throws DatatypeException {

        if (baseType instanceof ErrorType)
            return baseType;

        if (baseType.isFinal(XSDatatype.DERIVATION_BY_RESTRICTION))
            throw new DatatypeException(
                XSDatatypeImpl.localize(XSDatatypeImpl.ERR_INVALID_BASE_TYPE, baseType.displayName()));

        if (isEmpty()) {
            // if no facet is specified, and user wants anonymous type,
            // then no need to create another object.
            // TODO: for the type-derivation-OK test to work correctly,
            // maybe we need to wrap this by a FinalComponent.
            if (newNameUri == null && newLocalName == null)
                return baseType;

            // using FinalComponent as a wrapper,
            // so that the new type object can have its own name.
            return new FinalComponent(newNameUri, newLocalName, baseType, 0);
        }

        XSDatatypeImpl r = baseType; // start from current datatype

        // TODO : make sure that the following interpretation is true
        /*
            several facet consistency check is done here.
            those which are done in this time are:
        
                - length and (minLength/maxLength) are exclusive
                - maxInclusive and maxExclusive are exclusive
                - minInclusive and minExclusive are exclusive
        
        
            those are exclusive within the one restriction;
            that is, it is legal to derive types in the following way:
        
            <simpleType name="foo">
                <restriction baseType="string">
                    <minLength value="3" />
                </restrction>
            </simpleType>
        
            <simpleType name="bar">
                <restriction baseType="foo">
                    <length value="5" />
                </restrction>
            </simpleType>
        
            although the following is considered as an error
        
            <simpleType name="bar">
                <restriction baseType="foo">
                    <length value="5" />
                    <minLength value="3" />
                </restrction>
            </simpleType>
        
        
            This method is the perfect place to perform this kind of check. 
        */

        // makes sure that no mutually exclusive facets are specified
        for (int i = 0; i < exclusiveFacetPairs.length; i++)
            if (contains(exclusiveFacetPairs[i][0]) && contains(exclusiveFacetPairs[i][1]))
                throw new DatatypeException(
                    XSDatatypeImpl.localize(
                        XSDatatypeImpl.ERR_X_AND_Y_ARE_EXCLUSIVE,
                        exclusiveFacetPairs[i][0],
                        exclusiveFacetPairs[i][1]));

        if (contains(XSDatatypeImpl.FACET_TOTALDIGITS))
            r =
                new TotalDigitsFacet(
                    newNameUri,
                    newLocalName,
                    r,
                    getPositiveInteger(XSDatatype.FACET_TOTALDIGITS),
                    isFixed(XSDatatype.FACET_TOTALDIGITS));
        if (contains(XSDatatypeImpl.FACET_FRACTIONDIGITS))
            r =
                new FractionDigitsFacet(
                    newNameUri,
                    newLocalName,
                    r,
                    getNonNegativeInteger(XSDatatype.FACET_FRACTIONDIGITS),
                    isFixed(XSDatatype.FACET_FRACTIONDIGITS));
        if (contains(XSDatatypeImpl.FACET_MININCLUSIVE))
            r =
                new MinInclusiveFacet(
                    newNameUri,
                    newLocalName,
                    r,
                    getFacet(XSDatatype.FACET_MININCLUSIVE),
                    isFixed(XSDatatype.FACET_MININCLUSIVE));
        if (contains(XSDatatypeImpl.FACET_MAXINCLUSIVE))
            r =
                new MaxInclusiveFacet(
                    newNameUri,
                    newLocalName,
                    r,
                    getFacet(XSDatatype.FACET_MAXINCLUSIVE),
                    isFixed(XSDatatype.FACET_MAXINCLUSIVE));
        if (contains(XSDatatypeImpl.FACET_MINEXCLUSIVE))
            r =
                new MinExclusiveFacet(
                    newNameUri,
                    newLocalName,
                    r,
                    getFacet(XSDatatype.FACET_MINEXCLUSIVE),
                    isFixed(XSDatatype.FACET_MINEXCLUSIVE));
        if (contains(XSDatatypeImpl.FACET_MAXEXCLUSIVE))
            r =
                new MaxExclusiveFacet(
                    newNameUri,
                    newLocalName,
                    r,
                    getFacet(XSDatatype.FACET_MAXEXCLUSIVE),
                    isFixed(XSDatatype.FACET_MAXEXCLUSIVE));
        if (contains(XSDatatypeImpl.FACET_LENGTH))
            r = new LengthFacet(newNameUri, newLocalName, r, this);
        if (contains(XSDatatypeImpl.FACET_MINLENGTH))
            r = new MinLengthFacet(newNameUri, newLocalName, r, this);
        if (contains(XSDatatypeImpl.FACET_MAXLENGTH))
            r = new MaxLengthFacet(newNameUri, newLocalName, r, this);
        if (contains(XSDatatypeImpl.FACET_WHITESPACE))
            r = new WhiteSpaceFacet(newNameUri, newLocalName, r, this);
        if (contains(XSDatatypeImpl.FACET_PATTERN))
            r = new PatternFacet(newNameUri, newLocalName, r, this);
        if (contains(XSDatatypeImpl.FACET_ENUMERATION))
            r =
                new EnumerationFacet(
                    newNameUri,
                    newLocalName,
                    r,
                    getVector(XSDatatype.FACET_ENUMERATION),
                    isFixed(XSDatatype.FACET_ENUMERATION));

        // additional facet consistency check
        {
            DataTypeWithFacet o1, o2;

            // check that minLength <= maxLength
            o1 = r.getFacetObject(XSDatatypeImpl.FACET_MAXLENGTH);
            o2 = r.getFacetObject(XSDatatypeImpl.FACET_MINLENGTH);

            if (o1 != null && o2 != null && ((MaxLengthFacet)o1).maxLength < ((MinLengthFacet)o2).minLength)
                throw reportFacetInconsistency(
                    newLocalName,
                    o1,
                    XSDatatypeImpl.FACET_MAXLENGTH,
                    o2,
                    XSDatatypeImpl.FACET_MINLENGTH);

            // check that scale <= precision
            o1 = r.getFacetObject(XSDatatypeImpl.FACET_FRACTIONDIGITS);
            o2 = r.getFacetObject(XSDatatypeImpl.FACET_TOTALDIGITS);

            if (o1 != null && o2 != null && ((FractionDigitsFacet)o1).scale > ((TotalDigitsFacet)o2).precision)
                throw reportFacetInconsistency(
                    newLocalName,
                    o1,
                    XSDatatypeImpl.FACET_FRACTIONDIGITS,
                    o2,
                    XSDatatypeImpl.FACET_TOTALDIGITS);

            // check that minInclusive <= maxInclusive
            checkRangeConsistency(r, XSDatatypeImpl.FACET_MININCLUSIVE, XSDatatypeImpl.FACET_MAXINCLUSIVE);
            checkRangeConsistency(r, XSDatatypeImpl.FACET_MINEXCLUSIVE, XSDatatypeImpl.FACET_MAXEXCLUSIVE);

            // TODO : I'm not sure that the following two checks should be done or not.
            //            since the spec doesn't have these constraints
            checkRangeConsistency(r, XSDatatypeImpl.FACET_MININCLUSIVE, XSDatatypeImpl.FACET_MAXEXCLUSIVE);
            checkRangeConsistency(r, XSDatatypeImpl.FACET_MINEXCLUSIVE, XSDatatypeImpl.FACET_MAXINCLUSIVE);
        }

        return r;
    }

    /**
     * check (min,max) facet specification and makes sure that
     * they are consistent
     * 
     * @exception DatatypeException
     *        when two facets are inconsistent
     */
    private static void checkRangeConsistency(XSDatatypeImpl newType, String facetName1, String facetName2)
        throws DatatypeException {

        DataTypeWithFacet o1 = newType.getFacetObject(facetName1);
        DataTypeWithFacet o2 = newType.getFacetObject(facetName2);

        if (o1 != null && o2 != null) {
            final int c =
                ((Comparator)o1.getConcreteType()).compare(((RangeFacet)o1).limitValue, ((RangeFacet)o2).limitValue);
            if (c == Comparator.GREATER)
                throw reportFacetInconsistency(newType.displayName(), o1, facetName1, o2, facetName2);
        }
    }

    /**
     * creates a BadTypeException with appropriate error message.
     *
     * this method is only useful for reporting facet consistency violation.
     */
    private static DatatypeException reportFacetInconsistency(
        String newName,
        DataTypeWithFacet o1,
        String facetName1,
        DataTypeWithFacet o2,
        String facetName2) {
        // analyze the situation further so as to
        // provide better error messages

        String o1typeName = o1.getName();
        String o2typeName = o2.getName();

        if (o1typeName.equals(o2typeName))
            // o1typeName==o2typeName==newName
            return new DatatypeException(
                XSDatatypeImpl.localize(XSDatatypeImpl.ERR_INCONSISTENT_FACETS_1, facetName1, facetName2));

        if (o1typeName.equals(newName))
            // o2 must be specified in somewhere in the derivation chain
            return new DatatypeException(
                XSDatatypeImpl.localize(
                    XSDatatypeImpl.ERR_INCONSISTENT_FACETS_2,
                    facetName1,
                    o2.displayName(),
                    facetName2));

        if (o2typeName.equals(newName))
            // vice versa
            return new DatatypeException(
                XSDatatypeImpl.localize(
                    XSDatatypeImpl.ERR_INCONSISTENT_FACETS_2,
                    facetName2,
                    o1.displayName(),
                    facetName1));

        // this is not possible
        // because facet consistency check is done by every derivation.
        throw new IllegalStateException();
    }

    /**
     * returns true if the specified facet is a facet that needs value-space-level check.
     */
    private static boolean isValueFacet(String facetName) {
        return facetName.equals(XSDatatypeImpl.FACET_ENUMERATION)
            || facetName.equals(XSDatatypeImpl.FACET_MAXEXCLUSIVE)
            || facetName.equals(XSDatatypeImpl.FACET_MINEXCLUSIVE)
            || facetName.equals(XSDatatypeImpl.FACET_MAXINCLUSIVE)
            || facetName.equals(XSDatatypeImpl.FACET_MININCLUSIVE);
    }

    /**
     * returns true if the specified facet is a facet which can be set multiple times.
     */
    private static boolean isRepeatable(String facetName) {
        return facetName.equals(XSDatatypeImpl.FACET_ENUMERATION) || facetName.equals(XSDatatypeImpl.FACET_PATTERN);
    }

    /**
     * returns true if that facet is fixed.
     * 
     * the behavior is undefined when the specified facetName doesn't exist
     * in this map.
     */
    public boolean isFixed(String facetName) {
        return ((FacetInfo)impl.get(facetName)).fixed;
    }

    /**
     * gets a value of non-repeatable facet
     * 
     * the behavior is undefined when the specified facetName doesn't exist
     * in this map.
     */
    public Object getFacet(String facetName) {
        return ((FacetInfo)impl.get(facetName)).value;
    }

    /**
     * gets a value of repeatable facet
     * 
     * the behavior is undefined when the specified facetName doesn't exist
     * in this map.
     */
    public Vector getVector(String facetName) {
        return (Vector) ((FacetInfo)impl.get(facetName)).value;
    }

    /**
     * gets a value of non-repeatable facet as a positive integer
     *
     * the behavior is undefined when the specified facetName doesn't exist
     * in this map.
     * 
     * @exception DatatypeException
     *        if the parameter cannot be parsed as a positive integer
     */
    public int getPositiveInteger(String facetName) throws DatatypeException {
        try {
            // TODO : is this implementation correct?
            int value = Integer.parseInt((String)getFacet(facetName));
            if (value > 0)
                return value;
        } catch (NumberFormatException e) {
            // let's try BigInteger to see if the value is actually positive
            try {
                // if we can parse it in BigInteger, then treat is as Integer.MAX_VALUE
                // this will work for most cases, I suppose.
                if (new BigInteger((String)getFacet(facetName)).signum() > 0)
                    return Integer.MAX_VALUE;
            } catch (NumberFormatException ee) {
                ;
            }
        }

        throw new DatatypeException(
            XSDatatypeImpl.localize(XSDatatypeImpl.ERR_FACET_MUST_BE_POSITIVE_INTEGER, facetName));
    }

    /**
     * gets a value of non-repeatable facet as a non-negative integer
     * 
     * the behavior is undefined when the specified facetName doesn't exist
     * in this map.
     * 
     * @exception DatatypeException
     *        if the parameter cannot be parsed as a non-negative integer
     */
    public int getNonNegativeInteger(String facetName) throws DatatypeException {
        try {
            // TODO : is this implementation correct? Can I use Integer.parseInt?
            int value = Integer.parseInt((String)getFacet(facetName));
            if (value >= 0)
                return value;
        } catch (NumberFormatException e) {
            ;
        }

        throw new DatatypeException(
            XSDatatypeImpl.localize(XSDatatypeImpl.ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER, facetName));
    }

    /** checks if the specified facet was added to this map  */
    private boolean contains(String facetName) {
        return impl.containsKey(facetName);
    }

    /** returns true if no facet is added */
    public boolean isEmpty() {
        return impl.isEmpty();
    }

    private static class FacetInfo {
        public Object value;
        public boolean fixed;
        public FacetInfo(Object value, boolean fixed) {
            this.value = value;
            this.fixed = fixed;
        }
    }

    /**
     * dumps the contents to the given object.
     * this method is for debug use only.
     */
    public void dump(java.io.PrintStream out) {
        Iterator itr = impl.keySet().iterator();
        while (itr.hasNext()) {
            String facetName = (String)itr.next();
            FacetInfo fi = (FacetInfo)impl.get(facetName);

            if (fi.value instanceof Vector) {
                out.println(facetName + " :");
                Vector v = (Vector)fi.value;
                for (int i = 0; i < v.size(); i++)
                    out.println("  " + v.elementAt(i));
            } else
                out.println(facetName + " : " + fi.value);
        }
    }

    /**
     * gets names of the facets in this object
     * this method is used to produce error messages.
     */
    public String getFacetNames() {
        String r = "";
        Iterator itr = impl.keySet().iterator();
        while (itr.hasNext()) {
            if (r.length() != 0)
                r += ", ";
            r += (String)itr.next();
        }

        return r;
    }
}
