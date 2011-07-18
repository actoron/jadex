/*   Copyright 2004 BEA Systems, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.bea.xml.stream;

import com.bea.xml.stream.util.NamespaceContextImpl;
import com.bea.xml.stream.util.Stack;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashSet;
import java.util.Iterator;

import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;

/**
 * <p> The base output class.</p>
 */

public class XMLWriterBase
    extends ReaderToWriter
    implements XMLStreamWriter
{
    protected static final String DEFAULTNS="";
    private Writer writer;
    private boolean startElementOpened=false;
    private boolean isEmpty=false;
    private ConfigurationContextBase config;
    private CharsetEncoder encoder;
    
    // these two stacks are used to implement the
    // writeEndElement() method
    private Stack localNameStack = new Stack();
    private Stack prefixStack = new Stack();
    private Stack uriStack = new Stack();
    protected NamespaceContextImpl context = new NamespaceContextImpl();
    
    private HashSet needToWrite;
    private boolean isPrefixDefaulting;
    private int defaultPrefixCount=0;
    public XMLWriterBase() {}
    public XMLWriterBase(Writer writer) {
        this.writer = writer;
        setWriter(writer);
    }
    
    public void setWriter(Writer writer) {
        this.writer = writer;
        setStreamWriter(this);
        if (writer instanceof OutputStreamWriter) {
            String charsetName = ((OutputStreamWriter) writer).getEncoding();
            this.encoder = Charset.forName(charsetName).newEncoder();
        } else {
            this.encoder = null;
        }
    }
    
    public void setConfigurationContext(ConfigurationContextBase c) {
        config = c;
        isPrefixDefaulting = config.isPrefixDefaulting();
    }
    
    protected void write(String s)
        throws XMLStreamException
    {
        try {
            writer.write(s);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    protected void write (char c)
        throws XMLStreamException
    {
        try {
            writer.write(c);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    protected void write (char[] c)
        throws XMLStreamException
    {
        try {
            writer.write(c);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    protected void write (char[] c, int start, int len)
        throws XMLStreamException
    {
        try {
            writer.write(c,start,len);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    protected void writeCharactersInternal(char characters[],
                                           int start,
                                           int length,
                                           boolean isAttributeValue)
        throws XMLStreamException
    {
        if(length == 0) return;
        
        // We expect the common case to be that people do not have these
        // characters in their XML so we make a pass through the char
        // array and check.  If they're all good, we can call the
        // underlying Writer once with the entire char array.  If we find
        // a bad character then we punt it to the slow routine.
        
        int i = 0;

        loop:
        for(; i<length; i++) {
            char c = characters[i+start];
            switch(c) {
            case '\"':
                if (!isAttributeValue) { // no need to escape in regular content
                    continue;
                }
            case '&':
            case '<':
            case '>':
                break loop;
            }
            if (c < 32) {
                /* All non-space white space needs to be encoded in attribute
                 * values; in normal text tabs and LFs can be left as is,
                 * but everything else (including CRs) needs to be quoted
                 */
                if (isAttributeValue || (c != '\t' && c != '\n')) {
                    break loop;
                }
            } else if (c > 127) { // Above ascii range?
                if (encoder != null && !encoder.canEncode(c)) {
                    break loop;
                }
            }
        }
        
        if (i < length) { // slow path
            slowWriteCharacters(characters,start,length, isAttributeValue);
        } else { // fast path
            write(characters,start,length);
        }
    }
    
    private void slowWriteCharacters(char chars[],
                                     int start,
                                     int length,
                                     boolean isAttributeValue)
        throws XMLStreamException
    {
        loop:
        for (int i=0; i < length; i++) {
            final char c = chars[i+start];
            switch (c) {
            case '&':
                write("&amp;");
                continue loop;
            case '<':
                write("&lt;");
                continue loop;
            case '>':
                write("&gt;");
                continue loop;
            case '\"':
                if (isAttributeValue) {
                    write("&quot;");
                    continue loop;
                }
                break;
            default:
                if (c < 32) {
                    if (isAttributeValue || (c != '\t' && c != '\n')) {
                        write("&#");
                        write(Integer.toString(c));
                        write(';');
                        continue loop;
                    }
                } else if (c > 127 && encoder != null
                           && !encoder.canEncode(c)) {
                    write("&#");
                    write(Integer.toString(c));
                    write(';');
                    continue loop;
                }
            }
            write(c);
        }
    }
    
    protected void closeStartElement()
        throws XMLStreamException
    {
        if (startElementOpened) {
            closeStartTag();
            startElementOpened = false;
        }
    }
    
    protected boolean isOpen() {
        return startElementOpened;
    }
    
    protected void closeStartTag()
        throws XMLStreamException
    {
        flushNamespace();
        clearNeedsWritingNs();
        if (isEmpty) {
            write ("/>");
            isEmpty = false;
        }
        else
            write(">");
    }
    
    private void openStartElement()
        throws XMLStreamException
    {
        if (startElementOpened)
            closeStartTag();
        else
            startElementOpened = true;
    }
    
    
    protected String writeName(String prefix,String namespaceURI, String localName)
        throws XMLStreamException
    {
        if (!("".equals(namespaceURI)))
            prefix = getPrefixInternal(namespaceURI);
        if (!("".equals(prefix))) {
            write(prefix);
            write(":");
        }
        write(localName);
        return prefix;
        
    }
    
    private String getPrefixInternal(String namespaceURI) {
        String prefix = context.getPrefix(namespaceURI);
        if (prefix == null) {
            return "";
        }
        return prefix;
    }
    protected String getURIInternal(String prefix) {
        String uri = context.getNamespaceURI(prefix);
        if (uri == null) {
            return "";
        }
        return uri;
    }
    
    
    protected void openStartTag()
        throws XMLStreamException
    {
        write("<");
    }
    
    private boolean needToWrite(String uri) {
        if (needToWrite == null) {
            needToWrite = new HashSet();
        }
        boolean needs = needToWrite.contains(uri);
        needToWrite.add(uri);
        return needs;
    }
    
    private void prepareNamespace(String uri)
        throws XMLStreamException
    {
        if (!isPrefixDefaulting) return;
        if ("".equals(uri)) return;
        String prefix = getPrefix(uri);
        // if the prefix is bound then we can ignore and return
        if (prefix != null) return;
        
        defaultPrefixCount++;
        prefix = "ns"+defaultPrefixCount;
        setPrefix(prefix,uri);
    }
    
    private void removeNamespace(String uri) {
        if (!isPrefixDefaulting || needToWrite == null) return;
        needToWrite.remove(uri);
    }
    
    private void flushNamespace()
        throws XMLStreamException
    {
        if (!isPrefixDefaulting || needToWrite == null) return;
        Iterator i = needToWrite.iterator();
        while (i.hasNext()) {
            String uri = (String) i.next();
            String prefix = context.getPrefix(uri);
            if (prefix == null) {
                throw new XMLStreamException("Unable to default prefix with uri:"+
                                             uri);
            }
            writeNamespace(prefix,uri);
        }
        needToWrite.clear();
    }
    
    
    protected void writeStartElementInternal(String namespaceURI, String localName)
        throws XMLStreamException
    {
        if (namespaceURI == null)
            throw new IllegalArgumentException("The namespace URI may not be null");
        if (localName == null)
            throw new IllegalArgumentException("The local name  may not be null");
        
        openStartElement();
        openStartTag();
        prepareNamespace(namespaceURI);
        prefixStack.push(writeName("",namespaceURI, localName));
        localNameStack.push(localName);
        uriStack.push(namespaceURI);
    }
    
    public void writeStartElement(String namespaceURI, String localName)
        throws XMLStreamException
    {
        context.openScope();
        writeStartElementInternal(namespaceURI,localName);
    }
    
    
    
    public void writeStartElement(String prefix,
                                  String localName,
                                  String namespaceURI)
        throws XMLStreamException
    {
        if (namespaceURI == null)
            throw new IllegalArgumentException("The namespace URI may not be null");
        if (localName == null)
            throw new IllegalArgumentException("The local name may not be null");
        if (prefix == null)
            throw new IllegalArgumentException("The prefix may not be null");
        context.openScope();
        prepareNamespace(namespaceURI);
        context.bindNamespace(prefix,namespaceURI);
        writeStartElementInternal(namespaceURI,localName);
    }
    
    public void writeStartElement(String localName)
        throws XMLStreamException
    {
        context.openScope();
        writeStartElement("",localName);
    }
    
    public void writeEmptyElement(String namespaceURI, String localName)
        throws XMLStreamException
    {
        openStartElement();
        prepareNamespace(namespaceURI);
        isEmpty = true;
        write("<");
        writeName("",namespaceURI,localName);
    }
    
    public void writeEmptyElement(String prefix,
                                  String localName,
                                  String namespaceURI)
        throws XMLStreamException
    {
        openStartElement();
        prepareNamespace(namespaceURI);
        isEmpty = true;
        write("<");
        write(prefix);
        write(":");
        write(localName);
    }
    
    public void writeEmptyElement(String localName)
        throws XMLStreamException
    {
        writeEmptyElement("",localName);
    }
    
    protected void openEndTag()
        throws XMLStreamException
    {
        write("</");
    }
    protected void closeEndTag()
        throws XMLStreamException
    {
        write(">");
    }
    public void writeEndElement()
        throws XMLStreamException
    {
        /* 07-Mar-2006, TSa: Empty elements do not need special handling,
         *    since this call only 'closes' them as a side effect: real
         *    effect is for the open non-empty start element (non-empty
         *    just meaning it was written using full writeStartElement()
         *    as opposed to writeEmptyElement()).
         */
        //boolean wasEmpty = isEmpty;
        if (isOpen()) {
            closeStartElement();
        }
        String prefix = (String) prefixStack.pop();
        String local = (String) localNameStack.pop();
        uriStack.pop();
        //if(!wasEmpty) {
            openEndTag();
            writeName(prefix,"",local);
            closeEndTag();
        //}
        context.closeScope();
    }
    
    public void writeRaw(String data)
        throws XMLStreamException
    {
        closeStartElement();
        write(data);
    }
    
    public void close() throws XMLStreamException {
        flush();
    }
    public void flush() throws XMLStreamException {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void writeEndDocument()
        throws XMLStreamException
    {
        while(!localNameStack.isEmpty())
            writeEndElement();
    }
    
    public void writeAttribute(String localName, String value)
        throws XMLStreamException
    {
        writeAttribute("",localName,value);
    }
    public void writeAttribute(String namespaceURI,
                               String localName,
                               String value)
        throws XMLStreamException
    {
        if (!isOpen())
            throw new XMLStreamException("A start element must be written before an attribute");
        prepareNamespace(namespaceURI);
        write(" ");
        writeName("",namespaceURI,localName);
        write("=\"");
        writeCharactersInternal(value.toCharArray(),0,value.length(),true);
        write("\"");
    }
    
    public void writeAttribute(String prefix,
                               String namespaceURI,
                               String localName,
                               String value)
        throws XMLStreamException
    {
        if (!isOpen())
            throw new XMLStreamException("A start element must be written before an attribute");
        prepareNamespace(namespaceURI);
        context.bindNamespace(prefix,namespaceURI);
        write(" ");
        writeName(prefix,namespaceURI,localName);
        write("=\"");
        writeCharactersInternal(value.toCharArray(),0,value.length(),true);
        write("\"");
    }
    
    public void writeNamespace(String prefix, String namespaceURI)
        throws XMLStreamException
    {
        if(!isOpen())
            throw new XMLStreamException("A start element must be written before a namespace");
        if (prefix == null || "".equals(prefix) || "xmlns".equals(prefix)) {
            writeDefaultNamespace(namespaceURI);
            return;
        }
        if( needsWritingNs(prefix) ) {
            write(" xmlns:");
            write(prefix);
            write("=\"");
            write(namespaceURI);
            write("\"");
            setPrefix(prefix,namespaceURI);
        }
    }
    
    private HashSet setNeedsWritingNs = new HashSet();
    private void clearNeedsWritingNs() {
        setNeedsWritingNs.clear();
    }
    private boolean needsWritingNs(String prefix) {
        boolean needs = ! setNeedsWritingNs.contains(prefix);
        if(needs) {
            setNeedsWritingNs.add(prefix);
        }
        return needs;
    }
    
    public void writeDefaultNamespace(String namespaceURI)
        throws XMLStreamException
    {
        if(!isOpen())
            throw new XMLStreamException("A start element must be written before the default namespace");
        if( needsWritingNs(DEFAULTNS) ) {
            write(" xmlns");
            write("=\"");
            write(namespaceURI);
            write("\"");
            setPrefix(DEFAULTNS,namespaceURI);
        }
    }
    
    public void writeComment(String data)
        throws XMLStreamException
    {
        closeStartElement();
        write("<!--");
        if (data != null)
            write(data);
        write("-->");
    }
    
    public void writeProcessingInstruction(String target)
        throws XMLStreamException
    {
        closeStartElement();
        writeProcessingInstruction(target,null);
    }
    
    public void writeProcessingInstruction(String target,
                                           String text)
        throws XMLStreamException
    {
        closeStartElement();
        write("<?");
        if (target != null) { // isn't passing null an error, actually?
            write(target);
        }
        if (text != null) {
            // Need a separating white space
            write(' ');
            write(text);
        }
        write("?>");
    }
    
    public void writeDTD(String dtd)
        throws XMLStreamException
    {
        write(dtd);
    }
    public void writeCData(String data)
        throws XMLStreamException
    {
        closeStartElement();
        write("<![CDATA[");
        if (data != null)
            write(data);
        write("]]>");
    }
    
    public void writeEntityRef(String name)
        throws XMLStreamException
    {
        closeStartElement();
        write("&");
        write(name);
        write(";");
    }
    
    public void writeStartDocument()
        throws XMLStreamException
    {
        write("<?xml version='1.0' encoding='utf-8'?>");
    }
    
    public void writeStartDocument(String version)
        throws XMLStreamException
    {
        write("<?xml version='");
        write(version);
        write("'?>");
    }
    
    public void writeStartDocument(String encoding,
                                   String version)
        throws XMLStreamException
    {
        write("<?xml version='");
        write(version);
        write("' encoding='");
        write(encoding);
        write("'?>");
    }
    
    public void writeCharacters(String text)
        throws XMLStreamException
    {
        closeStartElement();
        writeCharactersInternal(text.toCharArray(),0,text.length(),false);
    }
    
    public void writeCharacters(char[] text, int start, int len)
        throws XMLStreamException
    {
        closeStartElement();
        writeCharactersInternal(text,start,len,false);
    }
    
    public String getPrefix(String uri)
        throws XMLStreamException
    {
        return context.getPrefix(uri);
    }
    
    public void setPrefix(String prefix, String uri)
        throws XMLStreamException
    {
        //if() {
        needToWrite(uri);
        context.bindNamespace(prefix,uri);
        //}
    }
    
    public void setDefaultNamespace(String uri)
        throws XMLStreamException
    {
        needToWrite(uri);
        context.bindDefaultNameSpace(uri);
    }
    
    public void setNamespaceContext(NamespaceContext context)
        throws XMLStreamException
    {
        if (context == null) throw new NullPointerException("The namespace "+
                                                                " context may"+
                                                                " not be null.");
        this.context = new NamespaceContextImpl(context);
    }
    
    public NamespaceContext getNamespaceContext() {
        return context;
    }
    
    public Object getProperty(String name)
        throws IllegalArgumentException
    {
        return config.getProperty(name);
    }
    
    public static void main(String args[]) throws Exception {
        
        /*******
         Writer w = new java.io.OutputStreamWriter(System.out);
         XMLWriterBase writer =
         new XMLWriterBase(w);
         writer.writeStartDocument();
         writer.setPrefix("c","http://c");
         writer.setDefaultNamespace("http://c");
         writer.writeStartElement("http://c","a");
         writer.writeAttribute("b","blah");
         writer.writeNamespace("c","http://c");
         writer.writeDefaultNamespace("http://c");
         writer.setPrefix("d","http://c");
         writer.writeEmptyElement("http://c","d");
         writer.writeAttribute("http://c","chris","fry");
         writer.writeNamespace("d","http://c");
         writer.writeCharacters("foo bar foo");
         writer.writeEndElement();
         writer.flush();
         ********/
        XMLOutputFactory output = XMLOutputFactoryBase.newInstance();
        output.setProperty(javaxx.xml.stream.XMLOutputFactory.IS_REPAIRING_NAMESPACES,new Boolean(true));
        Writer myWriter = new java.io.OutputStreamWriter(
            new java.io.FileOutputStream("tmp"),"us-ascii");
        XMLStreamWriter writer2 = output.createXMLStreamWriter(myWriter);
        writer2.writeStartDocument();
        writer2.setPrefix("c","http://c");
        writer2.setDefaultNamespace("http://d");
        writer2.writeStartElement("http://c","a");
        writer2.writeAttribute("b","blah");
        writer2.writeEmptyElement("http://c","d");
        writer2.writeEmptyElement("http://d","e");
        writer2.writeEmptyElement("http://e","f");
        writer2.writeEmptyElement("http://f","g");
        writer2.writeAttribute("http://c","chris","fry");
        writer2.writeCharacters("foo bar foo");
        writer2.writeCharacters("bad char coming[");
        char c = 0x1024;
        char[] array = new char[1];
        array[0]=c;
        writer2.writeCharacters(new String(array));
        writer2.writeCharacters("]");
        writer2.writeEndElement();
        writer2.flush();
        
        
    }
}
