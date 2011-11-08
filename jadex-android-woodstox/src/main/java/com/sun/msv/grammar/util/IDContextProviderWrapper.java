/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.util;

import org.relaxng.datatype.Datatype;

import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.verifier.regexp.StringToken;

/**
 * Wraps {@link IDContextProvider} so that it can be used
 * where {@link IDContextProvider2} is expected.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
@SuppressWarnings("deprecation")
public final class IDContextProviderWrapper implements IDContextProvider2 {
    private final IDContextProvider core;
    
    public static IDContextProvider2 create( IDContextProvider core ) {
        if(core==null) return null;
        else            return new IDContextProviderWrapper(core);
    }
    
    private IDContextProviderWrapper( IDContextProvider _core ) {
        this.core = _core;
    }
    
    public String getBaseUri() {
        return core.getBaseUri();
    }

    public boolean isNotation(String arg0) {
        return core.isNotation(arg0);
    }

    public boolean isUnparsedEntity(String arg0) {
        return core.isUnparsedEntity(arg0);
    }

    public void onID(Datatype datatype, StringToken token) {
        core.onID(datatype, token.literal);
    }

    public String resolveNamespacePrefix(String arg0) {
        return core.resolveNamespacePrefix(arg0);
    }

}
