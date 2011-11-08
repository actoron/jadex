package org.codehaus.stax2.io;

import java.io.*;

/**
 * This is the mid-level abstract base class for {@link Stax2Result}s
 * that refer to a resource in such a way, that an efficient
 * {@link OutputStream} or {@link Writer} can be constructed.
 *
 * @see Stax2FileResult
 */
public abstract class Stax2ReferentialResult
    extends Stax2Result
{
    protected Stax2ReferentialResult() { }

    /*
    /////////////////////////////////////////
    // Public API
    /////////////////////////////////////////
     */

    public abstract Writer constructWriter() throws IOException;

    public abstract OutputStream constructOutputStream() throws IOException;
}
