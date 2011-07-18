package com.wutka.dtd;

import java.io.*;
import java.util.*;

/** Represents a mixed Element content (PCDATA + choice/sequence).
 * Mixed Element can contain #PCDATA, or it can contain
 * #PCDATA followed by a list of pipe-separated names.
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
public class DTDMixed extends DTDContainer
{
    public DTDMixed()
    {
    }

/** Writes out a declaration for mixed content */
    public void write(PrintWriter out)
        throws IOException
    {
        out.print("(");

        Enumeration e = getItemsVec().elements();
        boolean isFirst = true;

        while (e.hasMoreElements())
        {
            if (!isFirst) out.print(" | ");
            isFirst = false;

            DTDItem item = (DTDItem) e.nextElement();
            item.write(out);
        }
        out.print(")");
        cardinal.write(out);
    }

    public boolean equals(Object ob)
    {
        if (ob == this) return true;
        if (!(ob instanceof DTDMixed)) return false;

        return super.equals(ob);
    }
}
