package com.sun.msv.writer;

import org.xml.sax.SAXException;

/**
 * This class is used to wrap SAXException by RuntimeException.
 * 
 * we can't throw Exception from visitor, so it has to be wrapped
 * by RuntimeException. This exception is catched outside of visitor
 * and nested exception is re-thrown.
 */
@SuppressWarnings("serial")
public class SAXRuntimeException extends RuntimeException {
    public final SAXException e;
    public SAXRuntimeException( SAXException e ) { this.e=e; }
}
