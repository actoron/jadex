package com.sun.msv.writer;

import java.util.Enumeration;

import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * Adapt SAX2 ContentHandler as a SAX1 DocumentHandler.
 * 
 * This class wraps a ContentHandler and makes it act as a DocumentHandler.
 * 
 * @author David Megginson, 
 *         <a href="mailto:sax@megginson.com">sax@megginson.com</a>
 */
@SuppressWarnings("deprecation")
public class ContentHandlerAdaptor implements DocumentHandler {
    
    private final NamespaceSupport nsSupport = new NamespaceSupport();
    private final ContentHandler contentHandler;
    private final AttributeListAdapter attAdapter = new AttributeListAdapter();
    private final AttributesImpl atts = new AttributesImpl();

    private final boolean namespaces = true;
    private final boolean prefixes = false;

    private final String nameParts[] = new String[3];
    
    public ContentHandlerAdaptor( ContentHandler handler ) {
        this.contentHandler = handler;
    }
    
    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.DocumentHandler.
    ////////////////////////////////////////////////////////////////////


    /**
     * Adapt a SAX1 document locator event.
     *
     * @param locator A document locator.
     * @see org.xml.sax.ContentHandler#setDocumentLocator
     */
    public void setDocumentLocator (Locator locator)
    {
        if (contentHandler != null) {
            contentHandler.setDocumentLocator(locator);
        }
    }


    /**
     * Adapt a SAX1 start document event.
     *
     * @exception org.xml.sax.SAXException The client may raise a
     *            processing exception.
     * @see org.xml.sax.DocumentHandler#startDocument
     */
    public void startDocument () throws SAXException
    {
        if (contentHandler != null) {
            contentHandler.startDocument();
        }
    }


    /**
     * Adapt a SAX1 end document event.
     *
     * @exception org.xml.sax.SAXException The client may raise a
     *            processing exception.
     * @see org.xml.sax.DocumentHandler#endDocument
     */
    public void endDocument () throws SAXException
    {
        if (contentHandler != null) {
            contentHandler.endDocument();
        }
    }


    /**
     * Adapt a SAX1 startElement event.
     *
     * <p>If necessary, perform Namespace processing.</p>
     *
     * @param qName The qualified (prefixed) name.
     * @param qAtts The XML 1.0 attribute list (with qnames).
     */
    public void startElement (String qName, AttributeList qAtts) throws SAXException
    {
        // If we're not doing Namespace
        // processing, dispatch this quickly.
        if (!namespaces) {
            if (contentHandler != null) {
            attAdapter.setAttributeList(qAtts);
            contentHandler.startElement("", "", qName.intern(),
                            attAdapter);
            }
            return;
        }

        // OK, we're doing Namespace processing.
        nsSupport.pushContext();
        boolean seenDecl = false;
        atts.clear();
    
        // Take a first pass and copy all
        // attributes into the SAX2 attribute
        // list, noting any Namespace 
        // declarations.
        int length = qAtts.getLength();
        for (int i = 0; i < length; i++) {
            String attQName = qAtts.getName(i);
            String type = qAtts.getType(i);
            String value = qAtts.getValue(i);

                    // Found a declaration...
            if (attQName.startsWith("xmlns")) {
                String prefix;
                int n = attQName.indexOf(':');
                if (n == -1) {
                    prefix = "";
                } else {
                    prefix = attQName.substring(n+1);
                }
                if (!nsSupport.declarePrefix(prefix, value)) {
                    reportError("Illegal Namespace prefix: " + prefix);
                }
                if (contentHandler != null) {
                    contentHandler.startPrefixMapping(prefix, value);
                }
                // We may still have to add this to
                // the list.
                if (prefixes) {
                    atts.addAttribute("", "", attQName.intern(),
                              type, value);
                }
                seenDecl = true;
                
                // This isn't a declaration.
            } else {
                String attName[] = processName(attQName, true);
                atts.addAttribute(attName[0], attName[1], attName[2],
                      type, value);
            }
        }
    
        // If there was a Namespace declaration,
        // we have to make a second pass just
        // to be safe -- this will happen very
        // rarely, possibly only once for each
        // document.
        if (seenDecl) {
            length = atts.getLength();
            for (int i = 0; i < length; i++) {
                String attQName = atts.getQName(i);
                if (!attQName.startsWith("xmlns")) {
                    String attName[] = processName(attQName, true);
                    atts.setURI(i, attName[0]);
                    atts.setLocalName(i, attName[1]);
                }
            }
        }

        // OK, finally report the event.
        if (contentHandler != null) {
            String name[] = processName(qName, false);
            contentHandler.startElement(name[0], name[1], name[2], atts);
        }
    }


    /**
     * Adapt a SAX1 end element event.
     *
     * @param qName The qualified (prefixed) name.
     * @exception org.xml.sax.SAXException The client may raise a
     *            processing exception.
     * @see org.xml.sax.DocumentHandler#endElement
     */
    public void endElement (String qName) throws SAXException
    {
        // If we're not doing Namespace
        // processing, dispatch this quickly.
        if (!namespaces) {
            if (contentHandler != null) {
            contentHandler.endElement("", "", qName.intern());
            }
            return;
        }

        // Split the name.
        String names[] = processName(qName, false);
        if (contentHandler != null) {
            contentHandler.endElement(names[0], names[1], names[2]);
            Enumeration<?> prefixes = nsSupport.getDeclaredPrefixes();
            while (prefixes.hasMoreElements()) {
                String prefix = (String)prefixes.nextElement();
                contentHandler.endPrefixMapping(prefix);
            }
        }
        nsSupport.popContext();
    }


    /**
     * Adapt a SAX1 characters event.
     *
     * @param ch An array of characters.
     * @param start The starting position in the array.
     * @param length The number of characters to use.
     * @exception org.xml.sax.SAXException The client may raise a
     *            processing exception.
     * @see org.xml.sax.DocumentHandler#characters
     */
    public void characters (char ch[], int start, int length)
        throws SAXException
    {
        if (contentHandler != null) {
            contentHandler.characters(ch, start, length);
        }
    }


    /**
     * Adapt a SAX1 ignorable whitespace event.
     *
     * @param ch An array of characters.
     * @param start The starting position in the array.
     * @param length The number of characters to use.
     * @exception org.xml.sax.SAXException The client may raise a
     *            processing exception.
     * @see org.xml.sax.DocumentHandler#ignorableWhitespace
     */
    public void ignorableWhitespace (char ch[], int start, int length)
        throws SAXException
    {
        if (contentHandler != null) {
            contentHandler.ignorableWhitespace(ch, start, length);
        }
    }


    /**
     * Adapt a SAX1 processing instruction event.
     *
     * @param target The processing instruction target.
     * @param data The remainder of the processing instruction
     * @exception org.xml.sax.SAXException The client may raise a
     *            processing exception.
     * @see org.xml.sax.DocumentHandler#processingInstruction
     */
    public void processingInstruction (String target, String data)
        throws SAXException
    {
        if (contentHandler != null) {
            contentHandler.processingInstruction(target, data);
        }
    }


    
    
    private String [] processName (String qName, boolean isAttribute) throws SAXException
    {
        String parts[] = nsSupport.processName(qName, nameParts, isAttribute);
        if (parts == null) {
            parts = new String[3];
            parts[2] = qName.intern();
            reportError("Undeclared prefix: " + qName);
        }
        return parts;
    }

    /**
     * Report a non-fatal error.
     *
     * @param message The error message.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception.
     */
    void reportError (String message) throws SAXException {
        throw new SAXParseException(message, null, null, -1, -1 );
    }
    
    
    

    ////////////////////////////////////////////////////////////////////
    // Inner class to wrap an AttributeList when not doing NS proc.
    ////////////////////////////////////////////////////////////////////


    /**
     * Adapt a SAX1 AttributeList as a SAX2 Attributes object.
     *
     * <p>This class is in the Public Domain, and comes with NO
     * WARRANTY of any kind.</p>
     *
     * <p>This wrapper class is used only when Namespace support
     * is disabled -- it provides pretty much a direct mapping
     * from SAX1 to SAX2, except that names and types are 
     * interned whenever requested.</p>
     */
    final class AttributeListAdapter implements Attributes
    {

        /**
         * Construct a new adapter.
         */
        AttributeListAdapter ()
        {
        }


        /**
         * Set the embedded AttributeList.
         *
         * <p>This method must be invoked before any of the others
         * can be used.</p>
         *
         * @param The SAX1 attribute list (with qnames).
         */
        void setAttributeList (AttributeList qAtts)
        {
            this.qAtts = qAtts;
        }


        /**
         * Return the length of the attribute list.
         *
         * @return The number of attributes in the list.
         * @see org.xml.sax.Attributes#getLength
         */
        public int getLength ()
        {
            return qAtts.getLength();
        }


        /**
         * Return the Namespace URI of the specified attribute.
         *
         * @param The attribute's index.
         * @return Always the empty string.
         * @see org.xml.sax.Attributes#getURI
         */
        public String getURI (int i)
        {
            return "";
        }


        /**
         * Return the local name of the specified attribute.
         *
         * @param The attribute's index.
         * @return Always the empty string.
         * @see org.xml.sax.Attributes#getLocalName
         */
        public String getLocalName (int i)
        {
            return "";
        }


        /**
         * Return the qualified (prefixed) name of the specified attribute.
         *
         * @param The attribute's index.
         * @return The attribute's qualified name, internalized.
         */
        public String getQName (int i)
        {
            return qAtts.getName(i).intern();
        }


        /**
         * Return the type of the specified attribute.
         *
         * @param The attribute's index.
         * @return The attribute's type as an internalized string.
         */
        public String getType (int i)
        {
            return qAtts.getType(i).intern();
        }


        /**
         * Return the value of the specified attribute.
         *
         * @param The attribute's index.
         * @return The attribute's value.
         */
        public String getValue (int i)
        {
            return qAtts.getValue(i);
        }


        /**
         * Look up an attribute index by Namespace name.
         *
         * @param uri The Namespace URI or the empty string.
         * @param localName The local name.
         * @return The attributes index, or -1 if none was found.
         * @see org.xml.sax.Attributes#getIndex(java.lang.String,java.lang.String)
         */
        public int getIndex (String uri, String localName)
        {
            return -1;
        }


        /**
         * Look up an attribute index by qualified (prefixed) name.
         *
         * @param qName The qualified name.
         * @return The attributes index, or -1 if none was found.
         * @see org.xml.sax.Attributes#getIndex(java.lang.String)
         */
        public int getIndex (String qName)
        {
            int max = atts.getLength();
            for (int i = 0; i < max; i++) {
            if (qAtts.getName(i).equals(qName)) {
                return i;
            }
            }
            return -1;
        }


        /**
         * Look up the type of an attribute by Namespace name.
         *
         * @param uri The Namespace URI
         * @param localName The local name.
         * @return The attribute's type as an internalized string.
         */
        public String getType (String uri, String localName)
        {
            return null;
        }


        /**
         * Look up the type of an attribute by qualified (prefixed) name.
         *
         * @param qName The qualified name.
         * @return The attribute's type as an internalized string.
         */
        public String getType (String qName)
        {
            return qAtts.getType(qName).intern();
        }


        /**
         * Look up the value of an attribute by Namespace name.
         *
         * @param uri The Namespace URI
         * @param localName The local name.
         * @return The attribute's value.
         */
        public String getValue (String uri, String localName)
        {
            return null;
        }


        /**
         * Look up the value of an attribute by qualified (prefixed) name.
         *
         * @param qName The qualified name.
         * @return The attribute's value.
         */
        public String getValue (String qName)
        {
            return qAtts.getValue(qName);
        }

        private AttributeList qAtts;
    }
}
