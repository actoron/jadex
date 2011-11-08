package com.sun.msv.datatype.xsd.regex;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

/**
 * {@link RegExpFactory} by a copy of Xerces in Sun's JDK 5.0.
 *
 * @author Kohsuke Kawaguchi
 */
final class JDK50Impl extends RegExpFactory {

    private final Class regexp;
    private final Constructor ctor;
    private final Method matches;

    JDK50Impl() throws Exception {
        regexp = getClass().getClassLoader().loadClass("com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression");
        ctor = regexp.getConstructor(new Class[]{String.class,String.class});
        matches = regexp.getMethod("matches",new Class[]{String.class});
    }

    public RegExp compile(String exp) throws ParseException {
        final Object re;

        try {
            // re = new RegularExpression(exp,"X");
            re = ctor.newInstance(new Object[]{exp, "X"});
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new ParseException(e.getTargetException().getMessage(),-1);
        } catch (InstantiationException e) {
            throw new InstantiationError(e.getMessage());
        }

        return new RegExp() {
            public boolean matches(String text) {
                try {
                    return ((Boolean)matches.invoke(re,new Object[]{text})).booleanValue();
                } catch (IllegalAccessException e) {
                    throw new IllegalAccessError(e.getMessage());
                } catch (InvocationTargetException e) {
                    throw new Error(e);
                }
            }
        };
    }

}
