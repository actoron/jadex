/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses name class that has child name classes
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClassWithChildState extends NameClassState implements NameClassOwner
{
    /**
     * name class object that is being created.
     * See {@link #castNameClass(NameClass, NameClass)} and {@link #annealNameClass(NameClass)} methods
     * for how a pattern will be created.
     */
    protected NameClass nameClass = null;

    /**
     * if this flag is true, then it is OK not to have any children.
     */
    protected boolean allowNullChild = false;
    
    /**
     * receives a Pattern object that is contained in this element.
     */
    public final void onEndChild( NameClass childNameClass ) {
        nameClass = castNameClass( nameClass, childNameClass );
    }
    
    protected final NameClass makeNameClass() {
        if( nameClass==null && !allowNullChild ) {
            reader.reportError( TREXBaseReader.ERR_MISSING_CHILD_NAMECLASS );
            nameClass = NameClass.ALL;
            // recover by assuming some name class.
        }
        return annealNameClass(nameClass);
    }
    
    protected State createChildState( StartTagInfo tag ) {
        return ((TREXBaseReader)reader).createNameClassChildState(this,tag);
    }

        
    /**
     * combines half-made name class and newly found child name class into the name class.
     * 
     * <p>
     * Say this container has three child name class n1,n2, and n3.
     * Then, the name class of this container will be made by the following method
     * invocations.
     * 
     * <pre>
     *   annealNameClass( castNameClass( castNameClass( castNameClass(null,p1), p2), p3 ) )
     * </pre>
     */
    protected abstract NameClass castNameClass(
        NameClass halfCastedNameClass, NameClass newChildNameClass );
    
    /**
     * performs final wrap-up and returns a fully created NameClass object
     * that represents this element.
     */
    protected NameClass annealNameClass( NameClass nameClass ) {
        // default implementation does nothing.
        return nameClass;
    }
}
