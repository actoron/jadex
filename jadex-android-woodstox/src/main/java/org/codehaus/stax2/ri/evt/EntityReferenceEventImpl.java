package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;

import javaxx.xml.stream.*;
import javaxx.xml.stream.events.EntityReference;
import javaxx.xml.stream.events.EntityDeclaration;

import org.codehaus.stax2.XMLStreamWriter2;

public class EntityReferenceEventImpl
    extends BaseEventImpl
    implements EntityReference
{
    protected final EntityDeclaration mDecl;

    public EntityReferenceEventImpl(Location loc, EntityDeclaration decl)
    {
        super(loc);
        mDecl = decl;
    }

    public EntityReferenceEventImpl(Location loc, String name)
    {
        super(loc);
        // note: location will be incorrect...
        mDecl = new EntityDeclarationEventImpl(loc, name);
    }

    public EntityDeclaration getDeclaration()
    {
        return mDecl;
    }

    public String getName()
    {
        return mDecl.getName();
    }

    /*
    ///////////////////////////////////////////
    // Implementation of abstract base methods
    ///////////////////////////////////////////
     */

    public int getEventType() {
        return ENTITY_REFERENCE;
    }

    public boolean isEntityReference() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer w)
        throws XMLStreamException
    {
        try {
            w.write('&');
            w.write(getName());
            w.write(';');
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException
    {
        w.writeEntityRef(getName());
    }

    /*
    ///////////////////////////////////////////
    // Standard method impl
    ///////////////////////////////////////////
     */

    public boolean equals(Object o)
    {
        if (o == this) return true;
        if (o == null) return false;

        if (!(o instanceof EntityReference)) return false;

        EntityReference other = (EntityReference) o;
        return getName().equals(other.getName());
    }

    public int hashCode()
    {
        return getName().hashCode();
    }
}
