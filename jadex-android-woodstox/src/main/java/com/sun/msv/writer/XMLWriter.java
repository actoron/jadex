package com.sun.msv.writer;

import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;

/**
 * Helper class that wraps {@link DocumentHandler} and provides utility methods.
 * 
 * <p>
 * Note that this class uses DocumentHandler, not ContentHandler.
 * This generally allows the caller better control.
 * 
 * <p>
 * This class throws {@link SAXRuntimeException}, instead of SAXException.
 */
@SuppressWarnings("deprecation")
public class XMLWriter
{
    protected DocumentHandler handler;
    /** this DocumentHandler will receive XML. */
    public void setDocumentHandler( DocumentHandler handler ) {
        this.handler = handler;
    }
    public DocumentHandler getDocumentHandler() { return handler; }
    
    public void element( String name ) {
        element( name, new String[0] );
    }
    public void element( String name, String[] attributes ) {
        start(name,attributes);
        end(name);
    }
    public void start( String name ) {
        start(name, new String[0] );
    }
    public void start( String name, String[] attributes ) {
        
        // create attributes.
        AttributeListImpl as = new AttributeListImpl();
        for( int i=0; i<attributes.length; i+=2 )
            as.addAttribute( attributes[i], "", attributes[i+1] );
        
        try {
            handler.startElement( name, as );
        } catch( SAXException e ) {
            throw new SAXRuntimeException(e);
        }
    }
    public void end( String name ) {
        try {
            handler.endElement( name );
        } catch( SAXException e ) {
            throw new SAXRuntimeException(e);
        }
    }
    
    public void characters( String str ) {
        try {
            handler.characters( str.toCharArray(), 0, str.length() );
        } catch( SAXException e ) {
            throw new SAXRuntimeException(e);
        }
    }
}
