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

import java.io.InvalidObjectException;
import java.io.Serializable;

/**
 * processes white space normalization
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class WhiteSpaceProcessor implements Serializable {

    /**
     * returns whitespace normalized text.
     * behavior varies on what normalization mode is used.
     */
    public abstract String process(String text);

    /** higher return value indicates tigher constraint */
    abstract int tightness();

    /**
     * gets the name of the white space processing mode.
     * It is one of "preserve","collapse", or "replace".
     */
    public abstract String getName();

    /**
     * returns a WhiteSpaceProcessor object if "whiteSpace" facet is specified.
     * Otherwise returns null.
     */
    protected static WhiteSpaceProcessor get(String name) throws DatatypeException {
        name = theCollapse.process(name);
        if (name.equals("preserve"))    return thePreserve;
        if (name.equals("collapse"))    return theCollapse;
        if (name.equals("replace"))     return theReplace;

        throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_INVALID_WHITESPACE_VALUE, name));
    }

    /** returns true if the specified char is a white space character. */
    protected static final boolean isWhiteSpace(char ch) {
        return ch == 0x9 || ch == 0xA || ch == 0xD || ch == 0x20;
    }

    protected Object readResolve() throws InvalidObjectException {
        // return the singleton instead of deserialized object.
        try {
            // backward compatibility with JDK1.3. we can't rely on the
            // order of inner classes
            if( this.getClass()==backwardCompatibiliyHook1.getClass() )
                return thePreserve;
            if( this.getClass()==backwardCompatibiliyHook2.getClass() )
                return theCollapse;
            if( this.getClass()==backwardCompatibiliyHook3.getClass() )
                return theReplace;
            
            return get(getName());
        } catch (DatatypeException bte) {
            throw new InvalidObjectException("Unknown Processing Mode");
        }
    }

    // short-cut methods
    public static String replace(String str) {
        return theReplace.process(str);
    }
    public static String collapse(String str) {
        return theCollapse.process(str);
    }

    public final static WhiteSpaceProcessor thePreserve = new Preserve();
    public final static WhiteSpaceProcessor theReplace = new Replace();
    public final static WhiteSpaceProcessor theCollapse = new Collapse();

    /*
        Actual processor implementation
    */
    private static class Preserve extends WhiteSpaceProcessor
    {
        public String process(String text) {
            return text;
        }
        int tightness() {
            return 0;
        }
        public String getName() {
            return "preserve";
        }
        
        // serialization support
        private static final long serialVersionUID = 1;
    };
    

    private static class Replace extends WhiteSpaceProcessor
    {
        public String process(String text) {
            final int len = text.length();
            StringBuffer result = new StringBuffer(len);

            for (int i = 0; i < len; i++) {
                char ch = text.charAt(i);
                if (super.isWhiteSpace(ch))
                    result.append(' ');
                else
                    result.append(ch);
            }

            return result.toString();
        }
        int tightness() {
            return 1;
        }
        public String getName() {
            return "replace";
        }
        
        // serialization support
        private static final long serialVersionUID = 1;
    };

    private static class Collapse extends WhiteSpaceProcessor
    {
        public String process(String text) {
            int len = text.length();
            StringBuffer result = new StringBuffer(len /**enough size*/
            );

            boolean inStripMode = true;

            for (int i = 0; i < len; i++) {
                char ch = text.charAt(i);
                boolean b = WhiteSpaceProcessor.isWhiteSpace(ch);
                if (inStripMode && b)
                    continue; // skip this character

                inStripMode = b;
                if (inStripMode)
                    result.append(' ');
                else
                    result.append(ch);
            }

            // remove trailing whitespaces
            len = result.length();
            if (len > 0 && result.charAt(len - 1) == ' ')
                result.setLength(len - 1);
            // whitespaces are already collapsed,
            // so all we have to do is to remove the last one character
            // if it's a whitespace.

            return result.toString();
        }
        int tightness() {
            return 2;
        }
        public String getName() {
            return "collapse";
        }

        // serialization support
        private static final long serialVersionUID = 1;
    };
    
    
    
    
    /**
     * Older version of XSDLib was using an anonymous class. 
     * @deprecated
     */
    private static final WhiteSpaceProcessor backwardCompatibiliyHook1 =
        new WhiteSpaceProcessor() {
            public String process(String text) {
                throw new UnsupportedOperationException();
             }
             int tightness() {
                 return 0;
             }
             public String getName() {
                 return "preserve";
             }
        };
    /**
     * Older version of XSDLib was using an anonymous class. 
     * @deprecated
     */
    private static final WhiteSpaceProcessor backwardCompatibiliyHook2 =
        new WhiteSpaceProcessor() {
            public String process(String text) {
                throw new UnsupportedOperationException();
            }
            int tightness() {
                return 2;
            }
            public String getName() {
                return "collapse";
            }
        };
    /**
     * Older version of XSDLib was using an anonymous class. 
     * @deprecated
     */
    private static final WhiteSpaceProcessor backwardCompatibiliyHook3 =
        new WhiteSpaceProcessor() {
            public String process(String text) {
                throw new UnsupportedOperationException();
            }
            int tightness() {
                return 1;
            }
            public String getName() {
                return "replace";
            }
        };

    // serialization support
    private static final long serialVersionUID = 1;
}
