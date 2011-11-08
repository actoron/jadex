package com.sun.msv.datatype.xsd.regex;

import java.text.ParseException;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class RegExpFactory {
    public abstract RegExp compile( String exp ) throws ParseException ;

    public static RegExpFactory createFactory() {
        String[] classList = new String[] {
            "com.sun.msv.datatype.regexp.InternalImpl",
            "com.sun.msv.datatype.xsd.regex.XercesImpl",
            "com.sun.msv.datatype.xsd.regex.JDK50Impl"
        };

        for( int i=0; i<classList.length; i++ ) {
            String name = classList[i];

            try {
                return (RegExpFactory)RegExpFactory.class.getClassLoader().loadClass(name).newInstance();
            } catch (Throwable e) {
                ; // ignore any error and try the next one
            }
        }

        throw new Error("no implementation of regexp was found.");
    }
}
