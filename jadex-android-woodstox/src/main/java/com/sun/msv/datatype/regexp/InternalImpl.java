package com.sun.msv.datatype.regexp;

import com.sun.msv.datatype.xsd.regex.RegExp;
import com.sun.msv.datatype.xsd.regex.RegExpFactory;
import java.text.ParseException;

/**
 * {@link RegExpFactory} by the internal copy of Xerces.
 *
 * @author Kohsuke Kawaguchi
 */
public final class InternalImpl extends RegExpFactory {
    public RegExp compile(String exp) throws ParseException {
        final RegularExpression re;

        try {
            re = new RegularExpression(exp,"X");
        } catch ( com.sun.msv.datatype.regexp.ParseException e ) {
            throw new ParseException(e.getMessage(),e.getLocation());
        }

        return new RegExp() {
            public boolean matches(String text) {
                return re.matches(text);
            }
        };
    }

}
