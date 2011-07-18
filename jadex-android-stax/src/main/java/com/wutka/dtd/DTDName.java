package com.wutka.dtd;

import java.io.*;

/** Represents a named item in the DTD
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
public class DTDName extends DTDItem
{
    public String value;

    public DTDName()
    {
    }

    public DTDName(String aValue)
    {
        value = aValue;
    }

/** Writes out the value of this name */
    public void write(PrintWriter out)
        throws IOException
    {
        out.print(value);
        cardinal.write(out);
    }

    public boolean equals(Object ob)
    {
        if (ob == this) return true;
        if (!(ob instanceof DTDName)) return false;
        if (!super.equals(ob)) return false;

        DTDName other = (DTDName) ob;

        if (value == null)
        {
            if (other.value != null) return false;
        }
        else
        {
            if (!value.equals(other.value)) return false;
        }
        return true;
    }

/** Sets the name value */
    public void setValue(String aValue)
    {
        value = aValue;
    }

/** Retrieves the name value */
    public String getValue()
    {
        return value;
    }
}
