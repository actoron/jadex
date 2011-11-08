package org.codehaus.stax2.ri;

import javaxx.xml.stream.Location;

import org.codehaus.stax2.XMLStreamLocation2;

/**
 * Simple implementation of {@link XMLStreamLocation2}, which just
 * wraps Stax 1.0 {@link Location} and adds no-operation implementation
 * of the additions.
 */
public class Stax2LocationAdapter
    implements XMLStreamLocation2
{
    protected final Location mWrappedLocation;

    protected final Location mParentLocation;

    public Stax2LocationAdapter(Location loc)
    {
        this(loc, null);
    }

    public Stax2LocationAdapter(Location loc, Location parent)
    {
        mWrappedLocation = loc;
        mParentLocation = parent;
    }

    // // // Basic Stax 1.0 implementation

    public int getCharacterOffset()
    {
        return mWrappedLocation.getCharacterOffset();
    }

    public int getColumnNumber()
    {
        return mWrappedLocation.getColumnNumber();
    }

    public int getLineNumber()
    {
        return mWrappedLocation.getLineNumber();
    }

    public String getPublicId()
    {
        return mWrappedLocation.getPublicId();
    }

    public String getSystemId()
    {
        return mWrappedLocation.getSystemId();
    }

    // // // And stax2 additions

    public XMLStreamLocation2 getContext()
    {
        if (mParentLocation == null) {
            return null;
        }
        if (mParentLocation instanceof XMLStreamLocation2) {
            return (XMLStreamLocation2) mParentLocation;
        }
        return new Stax2LocationAdapter(mParentLocation);
    }
}
