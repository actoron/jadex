package org.codehaus.stax2.ri;

import java.util.Iterator;

import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.*;
import javaxx.xml.stream.events.*;

import org.codehaus.stax2.*;
import org.codehaus.stax2.evt.XMLEvent2;

/**
 * Simple implementation of {@link XMLEventWriter}.
 */
public class Stax2EventWriterImpl
    implements XMLEventWriter,
               XMLStreamConstants
{
    final XMLStreamWriter2 mWriter;

    /*
    ////////////////////////////////////////////////////
    // Construction, init
    ////////////////////////////////////////////////////
     */

    public Stax2EventWriterImpl(XMLStreamWriter2 sw)
    {
        mWriter = sw;
    }

    /*
    ////////////////////////////////////////////////////
    // XMLEventWriter API
    ////////////////////////////////////////////////////
     */

    /**
     * Basic implementation of the method which will use event implementations
     * available as part of the reference implementation.
     *<p>
     * Note: ALL events (except for custom ones ref. impl. itself doesn't
     * produce, and thus may not always be able to deal with) are routed
     * through stream writer. This because it may want to do
     * different kinds of validation
     */
    public void add(XMLEvent event)
        throws XMLStreamException
    {
        switch (event.getEventType()) {
            /* First events that we have to route via stream writer, to
             * get and/or update namespace information:
             */

        case ATTRIBUTE: // need to pass to stream writer, to get namespace info
            {
                Attribute attr = (Attribute) event;
                QName name = attr.getName();
                mWriter.writeAttribute(name.getPrefix(), name.getNamespaceURI(),
                                       name.getLocalPart(), attr.getValue());
            }
            break;

        case END_DOCUMENT:
            mWriter.writeEndDocument();
            break;

        case END_ELEMENT:
            mWriter.writeEndElement();
            break;
            
        case NAMESPACE:
            {
                Namespace ns = (Namespace) event;
                mWriter.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
            }
            break;
            
        case START_DOCUMENT:
            {
                StartDocument sd = (StartDocument) event;
                if (!sd.encodingSet()) { // encoding defined?
                    mWriter.writeStartDocument(sd.getVersion());
                } else {
                    mWriter.writeStartDocument(sd.getCharacterEncodingScheme(),
                                               sd.getVersion());
                }
            }
            break;
            
        case START_ELEMENT:

            {
                StartElement se = event.asStartElement();
                QName n = se.getName();
                mWriter.writeStartElement(n.getPrefix(), n.getLocalPart(),
                                          n.getNamespaceURI());
                Iterator it = se.getNamespaces();
                while (it.hasNext()) {
                    Namespace ns = (Namespace) it.next();
                    add(ns);
                }
                it = se.getAttributes();
                while (it.hasNext()) {
                    Attribute attr = (Attribute) it.next();
                    add(attr);
                }
            }
            break;
            
            /* Then events we could output directly if necessary... but that
             * make sense to route via stream writer, for validation
             * purposes.
             */
            
        case CHARACTERS: // better pass to stream writer, for prolog/epilog validation
            {
                Characters ch = event.asCharacters();
                String text = ch.getData();
                if (ch.isCData()) {
                    mWriter.writeCData(text);
                } else {
                    mWriter.writeCharacters(text);
                }
            }
            break;

        case CDATA:
            mWriter.writeCData(event.asCharacters().getData());
            break;
            
        case COMMENT:
            mWriter.writeComment(((Comment) event).getText());
            break;
            
        case DTD:
            mWriter.writeDTD(((DTD) event).getDocumentTypeDeclaration());
            break;

        case ENTITY_REFERENCE:
            mWriter.writeEntityRef(((EntityReference) event).getName());
            break;

        case PROCESSING_INSTRUCTION: // let's just write directly
            {
                ProcessingInstruction pi = (ProcessingInstruction) event;
                mWriter.writeProcessingInstruction(pi.getTarget(), pi.getData());
            }
            break;

        case ENTITY_DECLARATION: // not yet produced by Wstx
        case NOTATION_DECLARATION: // not yet produced by Wstx
        case SPACE: // usually only CHARACTERS events exist...
        default:

            // Easy, if stax2 enabled
            if (event instanceof XMLEvent2) {
                ((XMLEvent2) event).writeUsing(mWriter);
            } else {
                // Otherwise... well, no real way to do it in generic manner
                throw new XMLStreamException("Don't know how to output event "+event);
            }
        }
    }

    public void add(XMLEventReader reader)
        throws XMLStreamException
    {
        while (reader.hasNext()) {
            add(reader.nextEvent());
        }
    }

    public void close()
        throws XMLStreamException
    {
        mWriter.close();
    }

    public void flush()
        throws XMLStreamException
    {
        mWriter.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return mWriter.getNamespaceContext();
    }

    public String getPrefix(String uri)
        throws XMLStreamException
    {
        return mWriter.getPrefix(uri);
    }

    public void setDefaultNamespace(String uri)
        throws XMLStreamException
    {
        mWriter.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext ctxt)
        throws XMLStreamException
    {
        mWriter.setNamespaceContext(ctxt);
    }

    public void setPrefix(String prefix, String uri)
        throws XMLStreamException
    {
        mWriter.setPrefix(prefix, uri);
    }
}
