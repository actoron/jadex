package com.wutka.dtd;

import java.io.*;

/** Defines the method used for writing DTD information to a PrintWriter
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
public interface DTDOutput
{
    public void write(PrintWriter out) throws IOException;
}
