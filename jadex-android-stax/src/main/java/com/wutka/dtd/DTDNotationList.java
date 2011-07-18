package com.wutka.dtd;

import java.util.*;
import java.io.*;

/** Represents a notation declaration for an attribute
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
public class DTDNotationList implements DTDOutput
{
    protected Vector items;

/** Creates a new notation */
    public DTDNotationList()
    {
        items = new Vector();
    }

/** Adds a item to the list of notation values */
    public void add(String item)
    {
        items.addElement(item);
    }

/** Removes an item from the list of notation values */
    public void remove(String item)
    {
        items.removeElement(item);
    }

/** Returns the list of notation values as an array */
    public String[] getItems()
    {
        String[] retval = new String[items.size()];
        items.copyInto(retval);

        return retval;
    }

/** Returns the list of notation values as a vector */
    public Vector getItemsVec()
    {
        return items;
    }

/** Writes a declaration for this notation */
    public void write(PrintWriter out)
        throws IOException
    {
        out.print("NOTATION ( ");
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
        if (!(ob instanceof DTDNotationList)) return false;

        DTDNotationList other = (DTDNotationList) ob;
        return items.equals(other.items);
    }

/** Returns the items in the notation list */
    public String[] getItem() { return getItems(); }

/** Sets the items in the notation list */
    public void setItem(String[] newItems)
    {
        items = new Vector(newItems.length);
        for (int i=0; i < newItems.length; i++)
        {
            items.addElement(newItems[i]);
        }
    }

/** Stores an item in the notation list */
    public void setItem(String item, int i)
    {
        items.setElementAt(item, i);
    }

/** Retrieves an item from the notation list */
    public String getItem(int i)
    {
        return (String) items.elementAt(i);
    }
}
