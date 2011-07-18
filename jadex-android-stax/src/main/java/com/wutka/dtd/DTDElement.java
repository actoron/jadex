package com.wutka.dtd;

import java.util.*;
import java.io.*;

/** Represents an element defined with the ELEMENT DTD tag
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
public class DTDElement implements DTDOutput
{
/** The name of the element */
    public String name;

/** The element's attributes */
    public Hashtable attributes;

/** The element's content */
    public DTDItem content;

    public DTDElement()
    {
        attributes = new Hashtable();
    }

    public DTDElement(String aName)
    {
        name = aName;

        attributes = new Hashtable();
    }

/** Writes out an element declaration and an attlist declaration (if necessary)
    for this element */
    public void write(PrintWriter out)
        throws IOException
    {
        out.print("<!ELEMENT ");
        out.print(name);
        out.print(" ");
        if (content != null)
        {
            content.write(out);
        }
        else
        {
            out.print("ANY");
        }
        out.println(">");
        out.println();

/*
        if (attributes.size() > 0)
        {
            out.print("<!ATTLIST ");
            out.println(name);
	    TreeMap tm=new TreeMap(attributes);
	    Collection values=tm.values();
	    Iterator iterator=values.iterator();
	    while (iterator.hasNext())
	    {
                out.print("           ");
                DTDAttribute attr = (DTDAttribute) iterator.next();
                attr.write(out);
		if (iterator.hasNext())
                	out.println();
		else
	                out.println(">");
	    }
        }
*/
    }

    public boolean equals(Object ob)
    {
        if (ob == this) return true;
        if (!(ob instanceof DTDElement)) return false;

        DTDElement other = (DTDElement) ob;

        if (name == null)
        {
            if (other.name != null) return false;
        }
        else
        {
            if (!name.equals(other.name)) return false;
        }

        if (attributes == null)
        {
            if (other.attributes != null) return false;
        }
        else
        {
            if (!attributes.equals(other.attributes)) return false;
        }

        if (content == null)
        {
            if (other.content != null) return false;
        }
        else
        {
            if (!content.equals(other.content)) return false;
        }

        return true;
    }

/** Sets the name of this element */
    public void setName(String aName)
    {
        name = aName;
    }

/** Returns the name of this element */
    public String getName()
    {
        return name;
    }

/** Stores an attribute in this element */
    public void setAttribute(String attrName, DTDAttribute attr)
    {
        attributes.put(attrName, attr);
    }

/** Gets an attribute for this element */
    public DTDAttribute getAttribute(String attrName)
    {
        return (DTDAttribute) attributes.get(attrName);
    }

/** Sets the content type of this element */
    public void setContent(DTDItem theContent)
    {
        content = theContent;
    }

/** Returns the content type of this element */
    public DTDItem getContent()
    {
        return content;
    }
}
