package org.codehaus.stax2.io;

import java.io.*;

/**
 * Simple implementation of {@link Stax2BlockSource} that encapsulates
 * a simple {@link String}.
 */
public class Stax2StringSource
    extends Stax2BlockSource
{
    final String mText;

    public Stax2StringSource(String text) {
        mText = text;
    }

    /*
    /////////////////////////////////////////
    // Implementation of the Public API
    /////////////////////////////////////////
     */

    public Reader constructReader()
        throws IOException
    {
        return new StringReader(mText);
    }

    public InputStream constructInputStream()
        throws IOException
    {
        /* No obvious/easy way; if caller really wants an InputStream, it
         * can get a Reader, add an encoders, and so on.
         */
        return null;
    }

    /*
    /////////////////////////////////////////
    // Additional API for this source
    /////////////////////////////////////////
     */

    public String getText() {
        return mText;
    }
}
