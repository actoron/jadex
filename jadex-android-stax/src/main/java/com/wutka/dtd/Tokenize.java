package com.wutka.dtd;

import java.io.*;
import java.util.*;
import java.net.URL;

/** Example program to read a DTD and print out its object model
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */

class Tokenize
{
	public static void main(String[] args)
	{
		try
		{
            DTDParser parser = null;
// MAW Version 1.17
// If it looks like the filename may be a URL, use the URL class
            if (args[0].indexOf("://") > 0)
            {
                parser = new DTDParser(new URL(args[0]), true);
            }
            else
            {
                parser = new DTDParser(new File(args[0]), true);
            }

// Parse the DTD and ask the parser to guess the root element
            DTD dtd = parser.parse(true);

            if (dtd.rootElement != null)
            {
                System.out.println("Root element is probably: "+
                    dtd.rootElement.name);
            }

            Enumeration e = dtd.elements.elements();

            while (e.hasMoreElements())
            {
                DTDElement elem = (DTDElement) e.nextElement();

                System.out.println("Element: "+elem.name);
                System.out.print("   Content: ");
                dumpDTDItem(elem.content);
                System.out.println();

                if (elem.attributes.size() > 0)
                {
                    System.out.println("   Attributes: ");
                    Enumeration attrs = elem.attributes.elements();
                    while (attrs.hasMoreElements())
                    {
                        System.out.print("        ");
                        DTDAttribute attr = (DTDAttribute) attrs.nextElement();
                        dumpAttribute(attr);
                    }
                    System.out.println();
                }
            }

            e = dtd.entities.elements();

            while (e.hasMoreElements())
            {
                DTDEntity entity = (DTDEntity) e.nextElement();

                if (entity.isParsed) System.out.print("Parsed ");

                System.out.println("Entity: "+entity.name);
                
                if (entity.value != null)
                {
                    System.out.println("    Value: "+entity.value);
                }

                if (entity.externalID != null)
                {
                    if (entity.externalID instanceof DTDSystem)
                    {
                        System.out.println("    System: "+
                            entity.externalID.system);
                    }
                    else
                    {
                        DTDPublic pub = (DTDPublic) entity.externalID;

                        System.out.println("    Public: "+
                            pub.pub+" "+pub.system);
                    }
                }

                if (entity.ndata != null)
                {
                    System.out.println("    NDATA "+entity.ndata);
                }
            }
            e = dtd.notations.elements();

            while (e.hasMoreElements())
            {
                DTDNotation notation = (DTDNotation) e.nextElement();

                System.out.println("Notation: "+notation.name);
                
                if (notation.externalID != null)
                {
                    if (notation.externalID instanceof DTDSystem)
                    {
                        System.out.println("    System: "+
                            notation.externalID.system);
                    }
                    else
                    {
                        DTDPublic pub = (DTDPublic) notation.externalID;

                        System.out.print("    Public: "+
                            pub.pub+" ");
                        if (pub.system != null)
                        {
                            System.out.println(pub.system);
                        }
                        else
                        {
                            System.out.println();
                        }
                    }
                }
            }
		}
		catch (Exception exc)
		{
			exc.printStackTrace(System.out);
		}
	}

    public static void dumpDTDItem(DTDItem item)
    {
        if (item == null) return;

        if (item instanceof DTDAny)
        {
            System.out.print("Any");
        }
        else if (item instanceof DTDEmpty)
        {
            System.out.print("Empty");
        }
        else if (item instanceof DTDName)
        {
            System.out.print(((DTDName) item).value);
        }
        else if (item instanceof DTDChoice)
        {
            System.out.print("(");
            DTDItem[] items = ((DTDChoice) item).getItems();

            for (int i=0; i < items.length; i++)
            {
                if (i > 0) System.out.print("|");
                dumpDTDItem(items[i]);
            }
            System.out.print(")");
        }
        else if (item instanceof DTDSequence)
        {
            System.out.print("(");
            DTDItem[] items = ((DTDSequence) item).getItems();

            for (int i=0; i < items.length; i++)
            {
                if (i > 0) System.out.print(",");
                dumpDTDItem(items[i]);
            }
            System.out.print(")");
        }
        else if (item instanceof DTDMixed)
        {
            System.out.print("(");
            DTDItem[] items = ((DTDMixed) item).getItems();

            for (int i=0; i < items.length; i++)
            {
                if (i > 0) System.out.print(",");
                dumpDTDItem(items[i]);
            }
            System.out.print(")");
        }
        else if (item instanceof DTDPCData)
        {
            System.out.print("#PCDATA");
        }

        if (item.cardinal == DTDCardinal.OPTIONAL)
        {
            System.out.print("?");
        }
        else if (item.cardinal == DTDCardinal.ZEROMANY)
        {
            System.out.print("*");
        }
        else if (item.cardinal == DTDCardinal.ONEMANY)
        {
            System.out.print("+");
        }
    }

    public static void dumpAttribute(DTDAttribute attr)
    {
        System.out.print(attr.name+" ");
        if (attr.type instanceof String)
        {
            System.out.print(attr.type);
        }
        else if (attr.type instanceof DTDEnumeration)
        {
            System.out.print("(");
            String[] items = ((DTDEnumeration) attr.type).getItems();

            for (int i=0; i < items.length; i++)
            {
                if (i > 0) System.out.print(",");
                System.out.print(items[i]);
            }
            System.out.print(")");
        }
        else if (attr.type instanceof DTDNotationList)
        {
            System.out.print("Notation (");
            String[] items = ((DTDNotationList) attr.type).getItems();

            for (int i=0; i < items.length; i++)
            {
                if (i > 0) System.out.print(",");
                System.out.print(items[i]);
            }
            System.out.print(")");
        }

        if (attr.decl != null)
        {
            System.out.print(" "+attr.decl.name);
        }

        if (attr.defaultValue != null)
        {
            System.out.print(" "+attr.defaultValue);
        }

        System.out.println();
    }
}
