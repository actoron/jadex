package org.codehaus.stax2.ri;

import java.util.Iterator;

import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.*;
import javaxx.xml.stream.events.*;

import org.codehaus.stax2.evt.XMLEventFactory2;
import org.codehaus.stax2.evt.DTD2;

import org.codehaus.stax2.ri.evt.*;

/**
 * This is an abstract almost complete plain vanilla implementation of
 * {@link XMLEventFactory2}.
 * It can be used as a building block for concrete implementations:
 * the minimal requirement is to implement <code>createQName</code>
 * methods.
 *
 * @author Tatu Saloranta
 *
 * @since 3.0
 */
public abstract class Stax2EventFactoryImpl
    extends XMLEventFactory2
{
    protected Location mLocation;

    public Stax2EventFactoryImpl() { }

    /*
    /////////////////////////////////////////////////////////////
    // XMLEventFactory API
    /////////////////////////////////////////////////////////////
     */

    public Attribute createAttribute(QName name, String value) {
        return new AttributeEventImpl(mLocation, name, value, true);
    }

    public Attribute createAttribute(String localName, String value) {
        return new AttributeEventImpl(mLocation, localName, null, null, value, true);
    }

    public Attribute createAttribute(String prefix, String nsURI,
                                     String localName, String value)
    {
        return new AttributeEventImpl(mLocation, localName, nsURI, prefix, value, true);
    }

    public Characters createCData(String content) {
        return new CharactersEventImpl(mLocation, content, true);
    }

    public Characters createCharacters(String content) {
        return new CharactersEventImpl(mLocation, content, false);
    }

    public Comment createComment(String text) {
        return new CommentEventImpl(mLocation, text);
    }

    /**
     * Note: constructing DTD events this way means that there will be no
     * internal presentation of actual DTD; no parsing is implied by
     * construction.
     */
    public DTD createDTD(String dtd) {
        return new DTDEventImpl(mLocation, dtd);
    }

    public EndDocument createEndDocument() {
        return new EndDocumentEventImpl(mLocation);
    }

    public EndElement createEndElement(QName name, Iterator namespaces) {
        return new EndElementEventImpl(mLocation, name, namespaces);
    }

    public EndElement createEndElement(String prefix, String nsURI,
                                       String localName)
    {
        return createEndElement(createQName(nsURI, localName), null);
    }

    public EndElement createEndElement(String prefix, String nsURI,
                                       String localName, Iterator ns)
    {
        return createEndElement(createQName(nsURI, localName, prefix), ns);
    }

    public EntityReference createEntityReference(String name, EntityDeclaration decl)
    {
        return new EntityReferenceEventImpl(mLocation, decl);
    }

    public Characters createIgnorableSpace(String content) {
        return CharactersEventImpl.createIgnorableWS(mLocation, content);
    }

    public Namespace createNamespace(String nsURI) {
        return NamespaceEventImpl.constructDefaultNamespace(mLocation, nsURI);
    }
    
    public Namespace createNamespace(String prefix, String nsURI) {
        return NamespaceEventImpl.constructNamespace(mLocation, prefix, nsURI);
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return new ProcInstrEventImpl(mLocation, target, data);
    }
    
    public Characters createSpace(String content) {
        return CharactersEventImpl.createNonIgnorableWS(mLocation, content);
    }

    public StartDocument createStartDocument() {
        return new StartDocumentEventImpl(mLocation);
    }

    public StartDocument createStartDocument(String encoding) {
        return new StartDocumentEventImpl(mLocation, encoding);
    }

    public StartDocument createStartDocument(String encoding, String version) {
        return new StartDocumentEventImpl(mLocation, encoding, version);
    }

    public StartDocument createStartDocument(String encoding, String version, boolean standalone)
    {
        return new StartDocumentEventImpl(mLocation, encoding, version,
                                          true, standalone);
    }

    public StartElement createStartElement(QName name, Iterator attr, Iterator ns)
    {
        return createStartElement(name, attr, ns, null);
    }

    public StartElement createStartElement(String prefix, String nsURI, String localName)
    {
        return createStartElement(createQName(nsURI, localName, prefix),
                                  null, null, null);
    }

    public StartElement createStartElement(String prefix, String nsURI,
                                           String localName, Iterator attr,
                                           Iterator ns)
    {
        return createStartElement(createQName(nsURI, localName, prefix), attr, ns,
                                  null);
    }

    public StartElement createStartElement(String prefix, String nsURI,
                                           String localName, Iterator attr,
                                           Iterator ns, NamespaceContext nsCtxt)
    {
        return createStartElement(createQName(nsURI, localName, prefix),
                                  attr, ns, nsCtxt);
    }

    public void setLocation(Location loc)
    {
        mLocation = loc;
    }

    /*
    /////////////////////////////////////////////////////////////
    // XMLEventFactory2 methods
    /////////////////////////////////////////////////////////////
     */

    public DTD2 createDTD(String rootName, String sysId, String pubId,
                          String intSubset)
    {
        return new DTDEventImpl(mLocation, rootName, sysId, pubId, intSubset, null);
    }

    public DTD2 createDTD(String rootName, String sysId, String pubId,
                          String intSubset, Object processedDTD)
    {
        return new DTDEventImpl(mLocation, rootName, sysId, pubId, intSubset,
                                processedDTD);
    }

    /*
    /////////////////////////////////////////////////////////////
    // Helper methods, overridable
    /////////////////////////////////////////////////////////////
     */

    protected abstract QName createQName(String nsURI, String localName);

    protected abstract QName createQName(String nsURI, String localName, String prefix);

    protected StartElement createStartElement(QName name, Iterator attr,
                                              Iterator ns, NamespaceContext ctxt)
    {
        return StartElementEventImpl.construct(mLocation, name, attr, ns, ctxt);
    }
}
