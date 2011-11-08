package com.sun.msv.datatype.xsd.regex;

/**
 * @author Kohsuke Kawaguchi
 */
public interface RegExp {
    boolean matches(String text);
}
