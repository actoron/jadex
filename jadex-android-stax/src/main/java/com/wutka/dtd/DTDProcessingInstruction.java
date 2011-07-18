package com.wutka.dtd;

import java.io.*;

/** Represents a processing instruction in the DTD
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
public class DTDProcessingInstruction implements DTDOutput
{
/** The processing instruction text */
    public String text;

    public DTDProcessingInstruction()
    {
    }

    public DTDProcessingInstruction(String theText)
    {
        text = theText;
    }
    
    public String toString()
    {
	    return text;
    }

    public void write(PrintWriter out)
        throws IOException
    {
        out.print("<?");
        out.print(text);
        out.println("?>");
    }

    public boolean equals(Object ob)
    {
        if (ob == this) return true;
        if (!(ob instanceof DTDProcessingInstruction)) return false;

        DTDProcessingInstruction other = (DTDProcessingInstruction) ob;

        if (text == null)
        {
            if (other.text != null) return false;
        }
        else
        {
            if (!text.equals(other.text)) return false;
        }

        return true;
    }

/** Sets the instruction text */
    public void setText(String theText)
    {
        text = theText;
    }

/** Retrieves the instruction text */
    public String getText()
    {
        return text;
    }
}
