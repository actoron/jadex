package com.wutka.dtd;

import java.io.*;

/** Represents a DTD Attribute in an ATTLIST declaration
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */

public class DTDAttribute implements DTDOutput
{
/** The name of the attribute */
    public String name;

/** The type of the attribute (either String, DTDEnumeration or
    DTDNotationList) */
    public Object type;

/** The attribute's declaration (required, fixed, implied) */
    public DTDDecl decl;

/** The attribute's default value (null if not declared) */
    public String defaultValue;

    public DTDAttribute()
    {
    }

    public DTDAttribute(String aName)
    {
        name = aName;
    }

/** Writes this attribute to an output stream */
    public void write(PrintWriter out)
        throws IOException
    {
        out.print(name+" ");
        if (type instanceof String)
        {
            out.print(type);
        }
        else if (type instanceof DTDEnumeration)
        {
            DTDEnumeration dtdEnum = (DTDEnumeration) type;
            dtdEnum.write(out);
        }
        else if (type instanceof DTDNotationList)
        {
            DTDNotationList dtdnl = (DTDNotationList) type;
            dtdnl.write(out);
        }

        if (decl != null)
        {
            decl.write(out);
        }

        if (defaultValue != null)
        {
            out.print(" \"");
            out.print(defaultValue);
            out.print("\"");
        }
        //out.println(">");                            Bug!
    }

    public boolean equals(Object ob)
    {
        if (ob == this) return true;
        if (!(ob instanceof DTDAttribute)) return false;

        DTDAttribute other = (DTDAttribute) ob;

        if (name == null)
        {
            if (other.name != null) return false;
        }
        else
        {
            if (!name.equals(other.name)) return false;
        }

        if (type == null)
        {
            if (other.type != null) return false;
        }
        else
        {
            if (!type.equals(other.type)) return false;
        }

        if (decl == null)
        {
            if (other.decl != null) return false;
        }
        else
        {
            if (!decl.equals(other.decl)) return false;
        }

        if (defaultValue == null)
        {
            if (other.defaultValue != null) return false;
        }
        else
        {
            if (!defaultValue.equals(other.defaultValue)) return false;
        }

        return true;
    }

/** Sets the name of the attribute */
    public void setName(String aName)
    {
        name = aName;
    }

/** Returns the attribute name */
    public String getName()
    {
        return name;
    }

/** Sets the type of the attribute */
    public void setType(Object aType)
    {
        if (!(aType instanceof String) &&
            !(aType instanceof DTDEnumeration) &&
            !(aType instanceof DTDNotationList))
        {
            throw new IllegalArgumentException(
                "Must be String, DTDEnumeration or DTDNotationList");
        }

        type = aType;
    }

/** Gets the type of the attribute */
    public Object getType()
    {
        return type;
    }

/** Sets the declaration (fixed, required, implied) */
    public void setDecl(DTDDecl aDecl)
    {
        decl = aDecl;
    }

/** Returns the declaration */
    public DTDDecl getDecl()
    {
        return decl;
    }

/** Sets the default value */
    public void setDefaultValue(String aDefaultValue)
    {
        defaultValue = aDefaultValue;
    }

/** Returns the default value */
    public String getDefaultValue()
    {
        return defaultValue;
    }
}
