package org.codehaus.stax2.io;

import java.io.*;
import java.net.URL;

/**
 * Simple implementation of {@link Stax2ReferentialSource}, which refers
 * to the specific file.
 */
public class Stax2URLSource
    extends Stax2ReferentialSource
{
    final URL mURL;

    public Stax2URLSource(URL url) {
        mURL = url;
    }

    /*
    /////////////////////////////////////////
    // Public API, simple accessors/mutators
    /////////////////////////////////////////
     */

    /**
     * @return URL that refers to the reference resource, for the purposes
     *   of resolving a relative reference from content read from the
     *   resource.
     */
    public URL getReference()
    {
        return mURL;
    }

    public Reader constructReader()
        throws IOException
    {
        String enc = getEncoding();
        if (enc != null && enc.length() > 0) {
            return new InputStreamReader(constructInputStream(), enc);
        }
        // Sub-optimal; really shouldn't use the platform default encoding
        return new InputStreamReader(constructInputStream());
    }

    public InputStream constructInputStream()
        throws IOException
    {
        /* A simple optimization: if it's a file reference, can use
         * a more optimal one:
         */
        if ("file".equals(mURL.getProtocol())) {
            return new FileInputStream(mURL.getPath());
        }
        return mURL.openStream();
    }
}
