package com.wutka.dtd;

import java.io.*;

/** Represents an external Public ID in an entity declaration
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */

public class DTDPublic extends DTDExternalID
{
    public String pub;

    public DTDPublic()
    {
    }

/** Writes out a public external ID declaration */
    public void write(PrintWriter out)
        throws IOException
    {
        out.print("PUBLIC \"");
        out.print(pub);
        out.print("\"");
        if (system != null)
        {
            out.print(" \"");
            out.print(system);
            out.print("\"");
        }
    }

    public boolean equals(Object ob)
    {
        if (ob == this) return true;
        if (!(ob instanceof DTDPublic)) return false;

        if (!super.equals(ob)) return false;

        DTDPublic other = (DTDPublic) ob;

        if (pub == null)
        {
            if (other.pub != null) return false;
        }
        else
        {
            if (!pub.equals(other.pub)) return false;
        }

        return true;
    }

/** Sets the public identifier */
    public void setPub(String aPub)
    {
        pub = aPub;
    }

/** Retrieves the public identifier */
    public String getPub()
    {
        return pub;
    }
}
