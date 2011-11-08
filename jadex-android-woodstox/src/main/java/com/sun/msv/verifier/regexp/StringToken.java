/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import java.util.StringTokenizer;

import org.relaxng.datatype.Datatype;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.util.DatatypeRef;

/**
 * chunk of string.
 * 
 * ID validation depends on the immutability of this object.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StringToken extends Token {

    public final String literal;
    public final IDContextProvider2 context;
    protected final ResidualCalculator resCalc;
    protected final boolean ignorable;

    /**
     * if this field is non-null,
     * this field will receive assigned DataType object.
     */
    public DatatypeRef refType;
    protected boolean saturated = false;

    private static final Datatype[] ignoredType = new Datatype[0];

    public StringToken(REDocumentDeclaration docDecl, String literal, IDContextProvider2 context) {
        this(docDecl.resCalc, literal, context, null);
    }

    public StringToken(REDocumentDeclaration docDecl, String literal, IDContextProvider2 context, DatatypeRef refType) {
        this(docDecl.resCalc, literal, context, refType);
    }

    public StringToken(ResidualCalculator resCalc, String literal, IDContextProvider2 context, DatatypeRef refType) {
        this.resCalc = resCalc;
        this.literal = literal;
        this.context = context;
        this.refType = refType;
        this.ignorable = literal.trim().length() == 0;

        if (ignorable && refType != null)
            refType.types = ignoredType;
    }

    /** DataExp can consume this token if its datatype can accept this string */
    public boolean match(DataExp exp) {

        if (!exp.dt.isValid(literal, context))
            return false; // not accepted.

        if (exp.except != Expression.nullSet) {
            if (resCalc.calcResidual(exp.except, this).isEpsilonReducible())
                // handling whitespace correcly requires isEpsilonReducible()
                // with the following test case
                // <data type="string"><except><value/></except></data>
                // with "".
                return false; // this token is accepted by its 'except' clause
        }

        // this type accepts me.
        if (refType != null)
            assignType(exp.dt);

        // if the type has ID semantics, report it.
        if (exp.dt.getIdType() != Datatype.ID_TYPE_NULL && context != null)
            // context can be legally null when this datatype is not context dependent.
            context.onID(exp.dt, this);

        return true;
    }

    public boolean match(ValueExp exp) {

        Object thisValue = exp.dt.createValue(literal, context);
        if (!exp.dt.sameValue(thisValue, exp.value))
            return false;

        // this type accepts me.
        if (refType != null)
            assignType(exp.dt);

        // if the type has ID semantics, report it.
        if (exp.dt.getIdType() != Datatype.ID_TYPE_NULL && context != null)
            // context can be legally null when this datatype is not context dependent.
            context.onID(exp.dt, this);

        return true;
    }

    /** ListExp can consume this token if its pattern accepts this string */
    public boolean match(ListExp exp) {
        StringTokenizer tokens = new StringTokenizer(literal);
        Expression residual = exp.exp;

        // if the application needs type information,
        // collect them from children.
        DatatypeRef dtRef = null;
        Datatype[] childTypes = null;
        int cnt = 0;

        if (this.refType != null) {
            dtRef = new DatatypeRef();
            childTypes = new Datatype[tokens.countTokens()];
        }

        while (tokens.hasMoreTokens()) {
            StringToken child = createChildStringToken(tokens.nextToken(), dtRef);
            residual = resCalc.calcResidual(residual, child);

            if (residual == Expression.nullSet)
                // the expression is failed to accept this item.
                return false;

            if (dtRef != null) {
                if (dtRef.types == null) {
                    // failed to assign type. bail out.
                    saturated = true;
                    refType.types = null;
                    dtRef = null;
                } else {
                    // type is successfully assigned for this child.
                    if (dtRef.types.length != 1)
                        // the current RELAX NG prohibits to nest <list> patterns.
                        // Thus it's not possible for this child to return more than one type.
                        throw new Error();

                    childTypes[cnt++] = dtRef.types[0];
                }
            }
        }

        if (!residual.isEpsilonReducible())
            // some expressions are still left. failed to accept this string.
            return false;

        // this <list> accepts this string.

        if (childTypes != null) {
            // assign datatype
            if (saturated)
                // a type is already assigned. That means this string has more than one type.
                // so bail out.
                refType.types = null;
            else
                refType.types = childTypes;
            saturated = true;
        }

        return true;
    }

    protected StringToken createChildStringToken(String literal, DatatypeRef dtRef) {
        return new StringToken(resCalc, literal, context, dtRef);
    }

    // anyString can match any string
    public boolean matchAnyString() {
        if (refType != null)
            assignType(StringType.theInstance);
        return true;
    }

    private void assignType(Datatype dt) {
        if (saturated) {
            if (refType.types != null && (refType.types[0] != dt || refType.types.length != 1))
                // different types are assigned. roll back to null
                refType.types = null;
        } else {
            // this is the first assignment. remember this value.
            refType.types = new Datatype[] { dt };
            saturated = true;
        }
    }

    /** checks if this token is ignorable.
     * 
     * StringToken is ignorable when it matches [ \t\r\n]*
     */
    boolean isIgnorable() {
        return ignorable;
    }
}
