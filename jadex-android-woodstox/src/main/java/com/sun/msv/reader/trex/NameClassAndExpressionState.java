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
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * Base implementation for ElementState and AttributeState
 * 
 * This class collects one name class and patterns
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClassAndExpressionState extends SequenceState implements NameClassOwner {
    protected NameClass nameClass = null;

    /**
     * gets namespace URI to which this declaration belongs
     */
    protected String getNamespace() {
        // usually, propagated "ns" attribute should be used
        return ((TREXBaseReader) reader).targetNamespace;
    }

    protected void startSelf() {
        super.startSelf();
        // if name attribtue is specified, use it.
        final String name = startTag.getCollapsedAttribute("name");

        if (name == null)
            return;

        final int idx = name.indexOf(':');
        if (idx != -1) {
            // QName is specified. resolve this prefix.
            final String[] s = reader.splitQName(name);
            if (s == null) {
                reader.reportError(TREXBaseReader.ERR_UNDECLARED_PREFIX, name);
                // recover by using a dummy name
                nameClass = new SimpleNameClass("", name);
            } else
                nameClass = new SimpleNameClass(s[0], s[1]);
        } else
            nameClass = new SimpleNameClass(getNamespace(), name);
    }
    
    public void onEndChild(NameClass p) {
        if (nameClass != null) // name class has already specified
            reader.reportError(TREXBaseReader.ERR_MORE_THAN_ONE_NAMECLASS);
        nameClass = p;
    }

    protected State createChildState(StartTagInfo tag) {
        if (nameClass == null) // nameClass should be specified before content model.
            {
            State nextState = ((TREXBaseReader) reader).createNameClassChildState(this, tag);
            if (nextState != null)
                return nextState;

            // to provide better error message, analyze the situation further.
            // users tend to forget to supply nameClass and name attribute.

            nextState = reader.createExpressionChildState(this, tag);
            if (nextState != null) {
                // OK. tag is recognized as an content model.
                // so probably this user forgot to specify name class.
                // report so and recover by assuming some NameClass
                reader.reportError(TREXBaseReader.ERR_MISSING_CHILD_NAMECLASS);
                nameClass = NameClass.ALL;
                return nextState;
            } else
                // probably this user made a typo. let the default handler reports an error
                return null;
        } else
            return reader.createExpressionChildState(this, tag);
    }

    protected void endSelf() {
        if (nameClass == null) {
            // name class is missing
            reader.reportError(TREXBaseReader.ERR_MISSING_CHILD_NAMECLASS);
            nameClass = NameClass.ALL;
        }

        super.endSelf();
    }
}
