package org.codehaus.stax2.io;

import java.io.*;

/**
 * Simple implementation of {@link Stax2ReferentialResult}, which refers
 * to the specific file.
 */
public class Stax2FileResult
    extends Stax2ReferentialResult
{
    final File mFile;

    public Stax2FileResult(File f) {
        mFile = f;
    }

    /*
    /////////////////////////////////////////
    // Implementation of the Public API
    /////////////////////////////////////////
     */

    public Writer constructWriter()
        throws IOException
    {
        String enc = getEncoding();
        if (enc != null && enc.length() > 0) {
            return new OutputStreamWriter(constructOutputStream(), enc);
        }
        // Sub-optimal; really shouldn't use the platform default encoding
        return new FileWriter(mFile);
    }

    public OutputStream constructOutputStream()
        throws IOException
    {
        return new FileOutputStream(mFile);
    }

    /*
    /////////////////////////////////////////
    // Additional API for this Result
    /////////////////////////////////////////
     */

    public File getFile() {
        return mFile;
    }
}
