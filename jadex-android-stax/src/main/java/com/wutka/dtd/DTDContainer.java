package com.wutka.dtd;

import java.io.*;
import java.util.*;

/** Represents an item that may contain other items (such as a
 * DTDChoice or a DTDSequence)
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
public abstract class DTDContainer extends DTDItem
{
    protected Vector items;

/** Creates a new DTDContainer */
    public DTDContainer()
    {
        items = new Vector();
    }

/** Adds an element to the container */
    public void add(DTDItem item)
    {
        items.addElement(item);
    }

/** Removes an element from the container */
    public void remove(DTDItem item)
    {
        items.removeElement(item);
    }

/** Returns the elements as a vector (not a clone!) */
    public Vector getItemsVec()
    {
        return items;
    }

/** Returns the elements as an array of items */
    public DTDItem[] getItems()
    {
        DTDItem[] retval = new DTDItem[items.size()];
        items.copyInto(retval);
        return retval;
    }

    public boolean equals(Object ob)
    {
       if (ob == this) return true;
        if (!(ob instanceof DTDContainer)) return false;

        if (!super.equals(ob)) return false;

        DTDContainer other = (DTDContainer) ob;

        return items.equals(other.items);
    }

/** Stores items in the container */
    public void setItem(DTDItem[] newItems)
    {
        items = new Vector(newItems.length);
        for (int i=0; i < newItems.length; i++)
        {
            items.addElement(newItems[i]);
        }
    }

/** Retrieves the items in the container */
    public DTDItem[] getItem()
    {
        DTDItem[] retval  = new DTDItem[items.size()];
        items.copyInto(retval);

        return retval;
    }

/** Stores an item in the container */
    public void setItem(DTDItem anItem, int i)
    {
        items.setElementAt(anItem, i);
    }

/** Retrieves an item from the container */
    public DTDItem getItem(int i)
    {
        return (DTDItem) items.elementAt(i);
    }

    public abstract void write(PrintWriter out)
        throws IOException;
}
