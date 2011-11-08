package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import javaxx.xml.namespace.QName;
import javaxx.xml.stream.*;
import javaxx.xml.stream.events.*;

import org.codehaus.stax2.*;
import org.codehaus.stax2.evt.XMLEvent2;

/**
 * This abstract base class implements common functionality for
 * Stax2 reference implementation's event API part.
 *
 * @author Tatu Saloranta
 */
public abstract class BaseEventImpl
    implements XMLEvent2
{
    /**
     * Location where token started; exact definition may depends
     * on event type.
     */
    protected final Location mLocation;

    protected BaseEventImpl(Location loc)
    {
        mLocation = loc;
    }

    /*
    //////////////////////////////////////////////
    // Skeleton XMLEvent API
    //////////////////////////////////////////////
     */

    public Characters asCharacters() {
        return (Characters) this;
    }

    public EndElement asEndElement() {
        return (EndElement) this;
    }

    public StartElement asStartElement() {
        return (StartElement) this;
    }

    public abstract int getEventType();

    public Location getLocation() {
        return mLocation;
    }

    public QName getSchemaType() {
        return null;
    }

    public boolean isAttribute()
    {
        return false;
    }

    public boolean isCharacters()
    {
        return false;
    }

    public boolean isEndDocument()
    {
        return false;
    }

    public boolean isEndElement()
    {
        return false;
    }

    public boolean isEntityReference()
    {
        return false;
    }

    public boolean isNamespace()
    {
        return false;
    }

    public boolean isProcessingInstruction()
    {
        return false;
    }

    public boolean isStartDocument()
    {
        return false;
    }

    public boolean isStartElement()
    {
        return false;
    }

    public abstract void writeAsEncodedUnicode(Writer w)
        throws XMLStreamException;

    /*
    //////////////////////////////////////////////
    // XMLEvent2 (StAX2)
    //////////////////////////////////////////////
     */

    public abstract void writeUsing(XMLStreamWriter2 w) throws XMLStreamException;

    /*
    ///////////////////////////////////////////
    // Overridden standard methods
    ///////////////////////////////////////////
     */

    /**
     * Declared abstract to force redefinition by sub-classes
     */
    public abstract boolean equals(Object o);

    /**
     * Declared abstract to force redefinition by sub-classes
     */
    public abstract int hashCode();

    public String toString() {
        return "[Stax Event #"+getEventType()+"]";
    }

    /*
    //////////////////////////////////////////////
    // Helper methods
    //////////////////////////////////////////////
     */

    protected void throwFromIOE(IOException ioe)
        throws XMLStreamException
    {
        throw new XMLStreamException(ioe.getMessage(), ioe);
    }

    /**
     * Comparison method that will consider null Strings to be
     * equivalent to empty Strings for comparison purposes; and
     * compare equality with that caveat.
     */
    protected static boolean stringsWithNullsEqual(String s1, String s2)
    {
        if (s1 == null || s1.length() == 0) {
            return (s2 == null) || (s2.length() == 0);
        }
        return (s2 != null) && s1.equals(s2);
    }

    protected static boolean iteratedEquals(Iterator it1, Iterator it2)
    {
        if (it1 == null || it2 == null) { // if one is null, both have to be
            return (it1 == it2);
        }
        // Otherwise, loop-de-loop...
        while (it1.hasNext()) {
            if (!it2.hasNext()) {
                return false;
            }
            Object o1 = it1.next();
            Object o2 = it2.next();

            if (!o1.equals(o2)) {
                return false;
            }
        }
        return true;
    }

    protected static int addHash(Iterator it, int baseHash)
    {
        int hash = baseHash;
        if (it != null) {
            while (it.hasNext()) {
                hash ^= it.next().hashCode();
            }
        }
        return hash;
    }
}
