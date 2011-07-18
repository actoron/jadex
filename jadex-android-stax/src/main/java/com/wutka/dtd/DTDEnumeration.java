package com.wutka.dtd;

import java.util.*;
import java.io.*;

/** Represents an enumeration of attribute values
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
public class DTDEnumeration implements DTDOutput
{
    protected Vector items;

/** Creates a new enumeration */
    public DTDEnumeration()
    {
        items = new Vector();
    }

/** Adds a new value to the list of values */
    public void add(String item)
    {
        items.addElement(item);
    }

/** Removes a value from the list of values */
    public void remove(String item)
    {
        items.removeElement(item);
    }

/** Returns the values as an array */
    public String[] getItems()
    {
        String[] retval = new String[items.size()];
        items.copyInto(retval);

        return retval;
    }

/** Returns the values as a vector (not a clone!) */
    public Vector getItemsVec()
    {
        return items;
    }

/** Writes out a declaration for this enumeration */
    public void write(PrintWriter out)
        throws IOException
    {
        out.print("( ");
        Enumeration e = getItemsVec().elements();

        boolean isFirst = true;
        while (e.hasMoreElements())
        {
            if (!isFirst) out.print(" | ");
            isFirst = false;

            out.print(e.nextElement());
        }
        out.print(")");
    }

    public boolean equals(Object ob)
    {
        if (ob == this) return true;
        if (!(ob instanceof DTDEnumeration)) return false;

        DTDEnumeration other = (DTDEnumeration) ob;
        return items.equals(other.items);
    }

/** Returns the items in the enumeration */
    public String[] getItem() { return getItems(); }

/** Sets the items in the enumeration */
    public void setItem(String[] newItems)
    {
        items = new Vector(newItems.length);
        for (int i=0; i < newItems.length; i++)
        {
            items.addElement(newItems[i]);
        }
    }

/** Stores an item in the enumeration */
    public void setItem(String item, int i)
    {
        items.setElementAt(item, i);
    }

/** Retrieves an item from the enumeration */
    public String getItem(int i)
    {
        return (String) items.elementAt(i);
    }
}
