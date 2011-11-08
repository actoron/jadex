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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.DataOrValueExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.util.IDContextProviderWrapper;
import com.sun.msv.util.DatatypeRef;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.verifier.Acceptor;

/**
 * {@link Acceptor} implementation.
 * 
 * <p>
 * When you are using <code>REDocumentDeclaration</code>, then the acceptor
 * is always guaranteed to be a subclass of this class.
 * 
 * Therefore, by using this regexp implementation of VGM, you can always downcast
 * {@link Acceptor} to this class and access its contents to get more information.
 * 
 * <p>
 * If you consider VGM as an automaton,
 * this class can be thought as a lazy automaton acceptor.
 * 
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionAcceptor implements Acceptor {
    
    private Expression    expression;
    /**
     * gets the residual content model.
     * 
     * <p>
     * This method returns the expression that represents the expected content model
     * it will read.
     * For example, if the original content model is (A,(B|C)) and this acceptor
     * has already read A, then this method returns (B|C).
     * 
     * <p>
     * The returned residual is useful to find out what elements can appear next.
     * 
     * <p>
     * If you consider VGM as an automaton, the residual content model
     * can be thought as the current state. Also,
     * At the same time, right language (a regular expression that represents
     * the language it can accept from now on).
     */
    public Expression getExpression() { return expression; }
    
    
    
    /** this object provides various function objects */
    protected final REDocumentDeclaration docDecl;
    
    /**
     * If true, this acceptor will ignore all undeclared attributes.
     * If false, this acceptor will signal an error for an undeclared attribute.
     * 
     * <p>
     * This flag is used to implement the semantics of RELAX Core, where
     * undeclared attributes are allowed.
     */
    protected final boolean ignoreUndeclaredAttributes;
    
    public ExpressionAcceptor( REDocumentDeclaration docDecl, Expression exp, boolean ignoreUndeclaredAttributes ) {
        this.docDecl    = docDecl;
        this.expression    = exp;
        this.ignoreUndeclaredAttributes = ignoreUndeclaredAttributes;
    }
    
    
    /**
     * creates combined child acceptor and primitive child acceptors (if necessary).
     * 
     * be careful not to keep returned object too long because
     * it is reused whenever the method is called.
     * 
     * @return null
     *        if errRef is null and this expression cannot accept given start tag.
     *        if errRef is non-null and error recovery is not possible.
     */
    public Acceptor createChildAcceptor( StartTagInfo tag, StringRef errRef ) {
        final CombinedChildContentExpCreator cccc = docDecl.cccec;
        
        // obtains fully combined child content pattern
        CombinedChildContentExpCreator.ExpressionPair e = cccc.get(expression,tag);
        if( e.content!=Expression.nullSet ) {
            // successful.
        
            return createAcceptor( e.content, e.continuation,
                cccc.getMatchedElements(), cccc.numMatchedElements() );
        }
        // no element declaration is satisfied by this start tag.
        //    this must be an error of input document.
            
        if( errRef==null )
            // bail out now to notify the caller that an error was found.
            return null;
            
    
        // no ElementExp accepts this tag name
        // (actually, some ElementExp may have possibly accepted this tag name,
        // but as a result of <concur>, no expression left ).
                    
        errRef.str = diagnoseBadTagName(tag);
        if( errRef.str==null )
            // no detailed error message was prepared.
            // use some generic one.
            errRef.str = docDecl.localizeMessage( REDocumentDeclaration.DIAG_BAD_TAGNAME_GENERIC, tag.qName );
            
        // prepare child acceptor.
        return createRecoveryAcceptors();
    }
    
    protected abstract Acceptor createAcceptor(
        Expression contentModel, Expression continuation/*can be null*/,
        ElementExp[] primitives, int numPrimitives );
        

    /**
     * @deprecated
     */
    public final boolean onAttribute(
        String namespaceURI, String localName, String qName, String value,
        com.sun.msv.grammar.IDContextProvider context, StringRef refErr, DatatypeRef refType ) {
        
        return onAttribute2( namespaceURI, localName, qName, value,
            IDContextProviderWrapper.create(context), refErr, refType );
    }
    
    public final boolean onAttribute2(
        String namespaceURI, String localName, String qName, String value,
        IDContextProvider2 context, StringRef refErr, DatatypeRef refType ) {
        
        // instead of creating a new object each time,
        // use a cached copy.
        docDecl.attToken.reinit( namespaceURI,localName,qName,
           new StringToken(docDecl,value,context,refType) );
        
        return onAttribute( docDecl.attToken, refErr );
    }
    
    protected boolean onAttribute( AttributeToken token, StringRef refErr ) {
        Expression r = docDecl.attFeeder.feed( this.expression, token, ignoreUndeclaredAttributes );
        
        if( r!=Expression.nullSet ) {
            // this attribute is properly consumed.
            expression = r;
            
            return true;
        }
        
        if( refErr==null ) {
            // refErr was not provided. bail out now.
            return false;
        }
    
        //
        // diagnose the error
        //
        
        // this attribute was not accepted.
        // its value may be wrong.
        // try feeding wild card and see if it's accepted.
        AttributeRecoveryToken rtoken = token.createRecoveryAttToken();
        r = docDecl.attFeeder.feed( this.expression, rtoken, ignoreUndeclaredAttributes );
                    
        if( r==Expression.nullSet ) {
            // even the wild card was rejected.
            
            // now there are two possibilities.
            // the first is that this attribute name is not allowed to appear,
            // which is the most typical case. (e.g., type miss of the attribute name, etc).
            // the second is that the content model of the element is equal to the
            // nullSet, thus nothing can be accepted. This is usually
            // a problem of the schema.
            
            if( this.expression==Expression.nullSet ) {
                // the content model is equal to the nullSet.
                refErr.str = docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_CONTENT_MODEL_IS_NULLSET, null );
            } else {
                // the content model is not equal to the nullSet.
                
                // this means that this attribute
                // is not specified by the grammar.
                refErr.str = docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_UNDECLARED_ATTRIBUTE, token.qName );
            }
            
            // recover by using the current expression.
            // TODO: possibly we can make all attributes optional or something.
            // (because this might be a caused by the typo.)
            return true;
        } else {
            
            // wild card was accepted, so the value must be wrong.
            refErr.str = diagnoseBadAttributeValue( rtoken );
            if( refErr.str==null ) {
                // no detailed error message can be provided
                // so use generic one.
                refErr.str = docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_GENERIC, token.qName );
            }
            
            // now we know the reason.
            // recover by assuming that the valid value was specified for this attribute.
            this.expression = r;
            return true;
        }
    }
    

    public boolean onEndAttributes( StartTagInfo sti, StringRef refErr ) {
        
        Expression r = docDecl.attPruner.prune( this.expression );
        if( r!=Expression.nullSet ) {
            // there was no error.
            this.expression = r;
            return true;
        }
        
        // there was an error.
        // specifically, some required attributes are missing.
        
        if( refErr==null )
            return false;    // refErr was not provided. bail out.
        

        if( this.expression==Expression.nullSet ) {
            // the content model is equal to the nullSet.
            refErr.str = docDecl.localizeMessage(
                REDocumentDeclaration.DIAG_CONTENT_MODEL_IS_NULLSET, null );
        } else {
            refErr.str = diagnoseMissingAttribute(sti);
            if( refErr.str==null )
                // no detailed error message can be provided
                // so use generic one.
                refErr.str = docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_GENERIC,
                    sti.qName );
        }
        
        // remove unconsumed attributes
        this.expression = this.expression.visit( docDecl.attRemover );
        return true;
    }
    
    
    
    
    protected boolean stepForward( Token token, StringRef errRef ) {
        
        Expression residual = docDecl.resCalc.calcResidual( expression, token );
        
        if( residual==Expression.nullSet ) {
            // error: we can't accept this token
            
            if( errRef!=null ) {
                // diagnose error.
                if( token instanceof StringToken )
                    errRef.str = diagnoseUnexpectedLiteral( (StringToken)token );
//                        docDecl.localizeMessage( docDecl.DIAG_BAD_LITERAL_VALUE_WRAPUP,
                // TODO: diagnosis for ElementToken
                
                // recovery by ignoring this token.
                // TODO: should we modify this to choice(expression,EoCR)?
                // we need some measures to prevent redundant choice
            } else {
                // do not mutate any member variables.
                // caller may call stepForward again with error recovery.
            }
            
            return false;
        }
        
        expression = residual;
        return true;
    }
    
    /**
     * @deprecated
     */
    public final boolean onText( String literal, com.sun.msv.grammar.IDContextProvider context, StringRef refErr, DatatypeRef refType ) {
        return onText2( literal, IDContextProviderWrapper.create(context), refErr, refType );
    }
    
    public boolean onText2( String literal, IDContextProvider2 provider, StringRef refErr, DatatypeRef refType ) {
        return stepForward( new StringToken(docDecl,literal,provider,refType), refErr );
    }
    
    public final boolean stepForwardByContinuation( Expression continuation, StringRef errRef ) {
        if( continuation!=Expression.nullSet ) {
            // successful transition
            expression = continuation;
            return true;
        }
        
        if( errRef==null )        return false;    // fail immediately.
        
        // TODO: diagnose uncompleted content model.
        return false;
    }
    
    
    /** checks if this Acceptor is satisifed */
    public boolean isAcceptState( StringRef errRef ) {
        if( errRef==null )
            return expression.isEpsilonReducible();
        else {
            if(expression.isEpsilonReducible())    return true;
            // error. provide diagnosis
            errRef.str = diagnoseUncompletedContent();
            return false;
        }
    }

    public int getStringCareLevel() {
        // if the value is cached, return cached value.
        // otherwise, calculate it now.
        OptimizationTag ot = (OptimizationTag)expression.verifierTag;
        if(ot==null)    expression.verifierTag = ot = new OptimizationTag();
        
        if(ot.stringCareLevel==OptimizationTag.STRING_NOTCOMPUTED)
            ot.stringCareLevel = StringCareLevelCalculator.calc(expression);
        
        return ot.stringCareLevel;
    }
    
    
    

    
// error recovery
//==================================================

    /*
    private final Expression mergeContinuation( Expression exp1, Expression exp2 ) {
        if(exp1==null && exp2==null)    return null;
        if(exp1==null || exp1==Expression.nullSet)    return exp2;
        if(exp2==null || exp2==Expression.nullSet)    return exp1;
        
        return docDecl.pool.createChoice(exp1,exp2);
    }
    */
    
    /**
     * creates Acceptor that recovers from errors.
     * 
     * This method also modifies the current expression in preparation to
     * accept newly created child acceptor.
     * 
     * Recovery will be done by preparing to accept two possibilities.
     * 
     * <ol>
     *  <li>We may get back to sync by ignoring the newly found illegal element.
     *      ( this is for mistake like "abcXdefg")
     *  <li>We may get back to sync by replacing newly found illegal element
     *      by one of the valid elements.
     *      ( this is for mistake like "abcXefg")
     * </ol>
     */
    private final Acceptor createRecoveryAcceptors() {
        
        final CombinedChildContentExpCreator cccc = docDecl.cccec;
        
        CombinedChildContentExpCreator.ExpressionPair combinedEoC =
            cccc.get( expression, null, false );
        
        // get residual of EoC.
        Expression eocr = docDecl.resCalc.calcResidual( expression, AnyElementToken.theInstance );
        
        Expression continuation = docDecl.pool.createChoice(
            expression, eocr );
        Expression contentModel = combinedEoC.content;
        
        // by passing null as elements of concern and
        // using continuation, we are effectively "generating"
        // the content model for error recovery.
        return createAcceptor( contentModel, continuation, null, 0 );
    }
    
    /**
     * format list of candidates to one string.
     * 
     * this method
     *  (1) inserts separator into appropriate positions
     *  (2) appends "more" message when items are only a portion of candidates.
     */
    private String concatenateMessages( List<String> items, boolean more,
                                              String separatorStr, String moreStr )
    {
        String r="";
        String sep = docDecl.localizeMessage(separatorStr,null);
        
        Collections.sort(items,
            new Comparator<String>(){
                public int compare( String o1, String o2 ) {
                    return o1.compareTo(o2);
                }
            });    // sort candidates.
        
        for( int i=0; i<items.size(); i++ ) {
            if(r.length()!=0)        r+= sep;
            r += items.get(i);
        }
        if( more )
            r += docDecl.localizeMessage(moreStr,null);
        
        return r;
    }

    private String concatenateMessages( Set<String> items, boolean more,
                                              String separatorStr, String moreStr ) {
        return concatenateMessages( new Vector<String>(items), more, separatorStr, moreStr );
    }

    /**
     * gets error diagnosis message from datatype.
     * 
     * @return null
     *        if diagnosis failed.
     */
    private String getDiagnosisFromTypedString( DataOrValueExp exp, StringToken value ) {
        try {
            exp.getType().checkValid(    value.literal, value.context );
            
            // TODO: diagnose errors if exp is ValueExp and 
            // this is a not correct value
            
            // it should throw an exception.
            // but just in case the datatype library has a bug,
            // we recover from this situation
            return null;
        } catch( DatatypeException e ) {
            return e.getMessage();
        }
    }


    /**
     * computes diagnosis message for bad tag name
     * 
     * @return null
     *        if diagnosis fails.
     */
    private String diagnoseBadTagName( StartTagInfo sti ) {
        final CombinedChildContentExpCreator cccc = docDecl.cccec;
        
        
        // try creating combined child content pattern without tag name check.
        Expression r = cccc.get(expression,sti,false).content;
            
        if( r==Expression.nullSet )
            // no element is allowed here at all.
            return docDecl.localizeMessage( REDocumentDeclaration.DIAG_ELEMENT_NOT_ALLOWED, sti.qName );
        
        
        if( cccc.isComplex() ) {
            // probably <concur> is used.
            // there is no easy way to tell which what tag name is expected.
                        
            // TODO: we can reduce strength by treating concur as choice.
            // do it.
            return null;
        }

        // we are now sure that combined child content expression will be
        // the choice of all elements of concern.
        // so if tag name satisfies one of those elements,
        // it can be accepted.
                        
        // therefore we can provide candidates for users.
                        
        Set<String> s = new java.util.LinkedHashSet<String>();
        boolean more = false;

        // if there is a SimpleNameClass with the same localName
        // but with a different namespace URI,
        // this variable will receive that URI.
        String wrongNamespace = null;
        
        final ElementExp[] eocs = cccc.getMatchedElements();
        final int len = cccc.numMatchedElements();
        for( int i=0; i<len; i++ ) {
            
            if( eocs[i].contentModel.getExpandedExp(docDecl.pool)==Expression.nullSet )
                // this element is not allowed to appear.
                continue;
            
            // test some typical name class patterns.
            final NameClass nc = eocs[i].getNameClass();
                        
            if( nc instanceof SimpleNameClass ) {
                SimpleNameClass snc = (SimpleNameClass)nc;
                
                if( snc.localName.equals(sti.localName) ) {
                    // sometimes, people simply forget to add namespace decl,
                    // or declare the wrong name.
                    wrongNamespace = snc.namespaceURI;
                }
                
                s.add( docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_SIMPLE_NAMECLASS, nc.toString() ) );
                continue;
            }
            if( nc instanceof NamespaceNameClass ) {
                s.add( docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_NAMESPACE_NAMECLASS, ((NamespaceNameClass)nc).namespaceURI ) );
                continue;
            }
            if( nc instanceof NotNameClass ) {
                NameClass ncc = ((NotNameClass)nc).child;
                if( ncc instanceof NamespaceNameClass ) {
                    s.add( docDecl.localizeMessage(
                        REDocumentDeclaration.DIAG_NOT_NAMESPACE_NAMECLASS, ((NamespaceNameClass)ncc).namespaceURI ) );
                    continue;
                }
            }
            // this name class is very complex and
            // therefore we were unable to provide appropriate suggestion.
            more = true;
        }
        
        // no candidate was collected. bail out.
        if( s.size()==0 )            return null;
        
        if( wrongNamespace!=null ) {
            if( s.size()==1 )
                // only one candidate.
                return docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_BAD_TAGNAME_WRONG_NAMESPACE, sti.localName, wrongNamespace );
            else
                // probably wrong namespace,
                // but show the user that he/she has other choices.
                return docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_BAD_TAGNAME_PROBABLY_WRONG_NAMESPACE, sti.localName, wrongNamespace );
        }

        
        // there is no clue about user's intention.
        return docDecl.localizeMessage(
            REDocumentDeclaration.DIAG_BAD_TAGNAME_WRAPUP, sti.qName,
            concatenateMessages( s, more,
                REDocumentDeclaration.DIAG_BAD_TAGNAME_SEPARATOR,
                REDocumentDeclaration.DIAG_BAD_TAGNAME_MORE ) );
    }


    /**
     * computes diagnosis message for bad attribute value
     * 
     * @param rtoken
     *        wild card AttributeToken that was used.
     *
     * @return null
     *        if diagnosis fails.
     */
    private String diagnoseBadAttributeValue( AttributeRecoveryToken rtoken ) {

        // if the combined child content expression is not complex,
        // only binary expressions used are choice and sequence.
                            
        // this is the choice of all constraints that made this
        // attribute fail.
        Expression constraint = rtoken.getFailedExp();
                            
        // The problem here is that sti.attributes.getValue(i)
        // didn't satisfy this expression.
                            
        // test some typical expression patterns and
        // provide error messages if it matchs the pattern.
        // otherwise provide a generic error message.
        
        if( constraint instanceof DataOrValueExp ) {
            // if only one AttributeExp is specified for this attribute
            // and if it has a TypedString as its child.                    
            // for RELAX, this is the only possible case
            DataOrValueExp tse = (DataOrValueExp)constraint;
            
            if( tse.getType() == com.sun.msv.grammar.relax.NoneType.theInstance ) {
                // if the underlying datatype is "none",
                // this should be reported as unexpected attribute.
                return docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_UNDECLARED_ATTRIBUTE,
                    rtoken.qName );
            }
            
            String dtMsg = getDiagnosisFromTypedString( tse, rtoken.value );
            if(dtMsg==null)        return null;
            
            return docDecl.localizeMessage(
                        REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_DATATYPE,
                        rtoken.qName, dtMsg );
        }
        if( constraint instanceof ChoiceExp ) {
            // choice of <string>s.
            //
            // this is also a frequently used pattern by TREX.
            // an expression like
            // 
            // <attribute name="export">
            //   <choice>
            //     <string>yes</string><string>no</string>
            //   </choice>
            // </attribute>
            //
            // falls into this pattern.
                                
            final Set<String> items = new java.util.LinkedHashSet<String>();
            boolean more = false;

            ChoiceExp ch = (ChoiceExp)constraint;
            Expression[] children = ch.getChildren();                    
            for( int i=0; i<children.length; i++ ) {
                if( children[i] instanceof ValueExp )
                    items.add( ((ValueExp)children[i]).value.toString() );
                else
                    // this is a fairly complex expression
                    // that we can't provide diagnosis.
                    more = true;
            }
            
            // no candidates was simple. bail out.
            if( items.size()==0 )    return null;
            
            // at least we have one suggestion.
            return docDecl.localizeMessage(
                REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_WRAPUP,
                rtoken.qName,
                concatenateMessages( items, more,
                    REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_SEPARATOR,
                    REDocumentDeclaration.DIAG_BAD_ATTRIBUTE_VALUE_MORE ) );
        }
        
        return null;    // this constraint didn't fall into known patterns.
    }                        



    /**
     * computes diagnosis message for missing attribute
     * 
     * @return null
     *        if diagnosis fails.
     */
    private String diagnoseMissingAttribute( StartTagInfo sti ) {
//        if( cccc.isComplex() )
//            // again if the expression is complex,
//            // hope is remote that we can find required attributes.
//                
//            // TODO: reduce strength by converting concur to choice?
//            return null;
        
        Expression e = expression.visit(docDecl.attPicker);
                
        if( e.isEpsilonReducible() )    throw new Error();    // assertion
        // if attribute expression is epsilon reducible, then
        // AttributePruner must return Expression other than nullSet.
        // In that case, there should have been no error.

        final Set<String> s = new java.util.LinkedHashSet<String>();
        boolean more = false;
                
        while( e instanceof ChoiceExp ) {
            ChoiceExp ch = (ChoiceExp)e;
                    
            NameClass nc = ((AttributeExp)ch.exp2).nameClass;
            if( nc instanceof SimpleNameClass )
                s.add( nc.toString() );
            else
                more = true;
            
            e = ch.exp1;
        }
        
        if( e==Expression.nullSet )
            // we are in the full panic mode.
            // abandon diagnosis.
            return null;
        
        if(!(e instanceof AttributeExp ))    throw new Error(e.toString());    //assertion
        
        NameClass nc = ((AttributeExp)e).nameClass;
        if( nc instanceof SimpleNameClass )
            s.add( nc.toString() );
        else
            more = true;
                
        if( s.size()==0 )        return null;
        
        // at least one candidate is found
        if( s.size()==1 && !more )
        {// only one candidate
            return docDecl.localizeMessage(
                REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_SIMPLE,
                sti.qName,s.iterator().next() );
        }
        else
            // list candidates
            return docDecl.localizeMessage(
                REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_WRAPUP,
                sti.qName,
                concatenateMessages( s, more,
                    REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_SEPARATOR,
                    REDocumentDeclaration.DIAG_MISSING_ATTRIBUTE_MORE ) );
    }
    
    /**
     * diagnoses an error when a StringToken is rejected.
     */
    private String diagnoseUnexpectedLiteral( StringToken token ) {
        final StringRecoveryToken srt = new StringRecoveryToken(token);
        
        // this residual corresponds to the expression we get
        // when we replace thie unexpected token by one of expected tokens.
        Expression recoveryResidual
            = docDecl.resCalc.calcResidual(expression,srt);
        
        if( recoveryResidual==Expression.nullSet )
            // we now know that no string literal was expected at all.
            return docDecl.localizeMessage( REDocumentDeclaration.DIAG_STRING_NOT_ALLOWED, token.literal.trim() );
            // keep this.expression untouched. This is equivalent to ignore this token.
        
        // there are two possible "recovery" for this error.
        //  (1) ignore this token
        //  (2) replace this token by a valid token.
        // the following choice implements both of them.
        expression = docDecl.pool.createChoice( expression, recoveryResidual );
        
        if( srt.failedExps.size()==1 ) {
            
            DataOrValueExp texp = (DataOrValueExp)srt.failedExps.iterator().next();
            try {
                // TODO: handle ValueExp nicely
                texp.getType().checkValid( srt.literal, srt.context );
                
                if(texp instanceof ValueExp) {
                    ValueExp vexp = (ValueExp)texp;
                    
                    if(!vexp.dt.sameValue(vexp.value,
                            vexp.dt.createValue(srt.literal,srt.context))) {
                        // incorrect value
                        return docDecl.localizeMessage(
                            REDocumentDeclaration.DIAG_BAD_LITERAL_INCORRECT_VALUE,
                            vexp.value.toString(), token.literal.trim() );
                    }
                }
            } catch( DatatypeException de ) {
                // this literal is invalid.
                if( de.getMessage()!=null )
                    return de.getMessage();    // return the diagnosis.
                
                // we don't know the exact reason, but the value was wrong.
                return docDecl.localizeMessage( REDocumentDeclaration.DIAG_BAD_LITERAL_GENERIC, token.literal.trim() );
            }
        } else {
            // there are multiple candidates.
            final Set<String> items = new java.util.LinkedHashSet<String>();
            boolean more = false;
            
            Iterator<Object> itr = srt.failedExps.iterator();
                                
            while(itr.hasNext()) {
                DataOrValueExp texp = (DataOrValueExp)itr.next();
                
                if( texp instanceof ValueExp )
                    // we can list this item as one of the candidates
                    items.add( ((ValueExp)texp).value.toString() );
                else
                    // this must be some datatype
                    // that we can't provide diagnosis.
                    more = true;
            }
            
            // no candidates was simple. bail out.
            if( items.size()==0 )    return null;
            
            // at least we have one suggestion.
            return docDecl.localizeMessage(
                REDocumentDeclaration.DIAG_BAD_LITERAL_WRAPUP,
                    concatenateMessages( items, more,
                        REDocumentDeclaration.DIAG_BAD_LITERAL_SEPARATOR,
                        REDocumentDeclaration.DIAG_BAD_LITERAL_MORE ),
                token.literal.trim() );
        }
        
        // unable to diagnose the reason of error.
        return null;
        
        // TODO: ID/IDREF violation diagnosis.
/*                    
                // now the literal is valid.
                // Is this key/keyref constraint violation?
                if( texp instanceof NGTypedStringExp ) {
                    NGTypedStringExp ntexp = (NGTypedStringExp)texp;
                    if( ntexp.keyName!=null
                        && !token.context.onID( ntexp.keyName.namespaceURI, ntexp.keyName.localName, ntexp.dt.createValue(token.literal,token.context) ) ) {
                            
                        if( ntexp.keyName.localName.length()==0 )
                            // empty key name indicates that this is an ID.
                            return docDecl.localizeMessage( docDecl.DIAG_BAD_KEY_VALUE,
                                token.literal.trim() );
                        else
                            return docDecl.localizeMessage( docDecl.DIAG_BAD_KEY_VALUE2,
                                token.literal.trim(), ntexp.keyName );
                    }
                }
*/
    }
    
    /**
     * diagnoses "uncompleted content model" error.
     * It basically provides what we were expected.
     */
    protected String diagnoseUncompletedContent() {
        final CombinedChildContentExpCreator cccc = docDecl.cccec;
        cccc.get( expression, null, false );
        
        Set<String> s = new java.util.LinkedHashSet<String>();    // this set will receive possible tag names.
        boolean more = false;                // this flag is set to true if there are more
                                            // candidate.
        
        final ElementExp[] eocs = cccc.getMatchedElements();
        final int len = cccc.numMatchedElements();
        for( int i=0; i<len; i++ ) {
            // test some typical name class patterns.
            final NameClass nc = eocs[i].getNameClass();
                        
            if( nc instanceof SimpleNameClass ) {
                s.add( docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_SIMPLE_NAMECLASS, nc.toString() ) );
                continue;
            }
            if( nc instanceof NamespaceNameClass ) {
                s.add( docDecl.localizeMessage(
                    REDocumentDeclaration.DIAG_NAMESPACE_NAMECLASS,
                    ((NamespaceNameClass)nc).namespaceURI ) );
                continue;
            }
            if( nc instanceof NotNameClass ) {
                NameClass ncc = ((NotNameClass)nc).child;
                if( ncc instanceof NamespaceNameClass ) {
                    s.add( docDecl.localizeMessage(
                        REDocumentDeclaration.DIAG_NOT_NAMESPACE_NAMECLASS, ((NamespaceNameClass)ncc).namespaceURI ) );
                    continue;
                }
            }
            // this name class is very complex and
            // therefore we were unable to provide appropriate suggestion.
            more = true;
        }
        
        // no candidate was collected. bail out.
        // this happens when we are expecting a string.
        if( s.size()==0 )            return null;
        

        return docDecl.localizeMessage(
            REDocumentDeclaration.DIAG_UNCOMPLETED_CONTENT_WRAPUP, null,
            concatenateMessages( s, more,
                REDocumentDeclaration.DIAG_UNCOMPLETED_CONTENT_SEPARATOR,
                REDocumentDeclaration.DIAG_UNCOMPLETED_CONTENT_MORE ) );
    }
}
