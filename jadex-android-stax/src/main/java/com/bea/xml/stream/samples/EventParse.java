package com.bea.xml.stream.samples;

import java.io.FileReader;
import java.util.Iterator;

import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.events.XMLEvent;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.*;
import javaxx.xml.stream.events.*;

import com.bea.xml.stream.util.ElementTypeNames;
/**
 * @author Copyright (c) 2002 by BEA Systems. All Rights Reserved.
 */

public class EventParse {

  private static String filename = null;
  
  private static void printUsage() {
    System.out.println("usage: java com.bea.xml.stream.samples.EventParse <xmlfile>");
  }

  public static void main(String[] args) throws Exception {
    try { 
      filename = args[0];
    } catch (ArrayIndexOutOfBoundsException aioobe){
      printUsage();
      System.exit(0);
    }

    System.setProperty("javax.xml.stream.XMLInputFactory", 
                       "com.bea.xml.stream.MXParserFactory");
    
    XMLInputFactory xmlif = XMLInputFactory.newInstance();
    System.out.println("FACTORY: " + xmlif);

    xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
                      Boolean.FALSE);

    XMLStreamReader xmlr = xmlif.createXMLStreamReader(new FileReader(filename));
    System.out.println("READER:  " + xmlr + "\n");
    
    while(xmlr.hasNext()){
      printEvent(xmlr);
      xmlr.next();
    }
  }

  public final static String getEventTypeString(int eventType) {
    return ElementTypeNames.getEventTypeString(eventType);
  }

  private static void printEvent(XMLStreamReader xmlr) {
    System.out.print("EVENT:["+xmlr.getLocation().getLineNumber()+"]["+
                     xmlr.getLocation().getColumnNumber()+"] ");
    System.out.print(getEventTypeString(xmlr.getEventType()));
    System.out.print(" [");
    switch (xmlr.getEventType()) {
    case XMLEvent.START_ELEMENT:
      System.out.print("<");
      printName(xmlr);
      printNamespaces(com.bea.xml.stream.XMLEventAllocatorBase.getNamespaces(xmlr));
      printAttributes(xmlr);
      System.out.print(">");
      break;
    case XMLEvent.END_ELEMENT:
      System.out.print("</");
      printName(xmlr);
      printNamespaces(com.bea.xml.stream.XMLEventAllocatorBase.getNamespaces(xmlr));
      System.out.print(">");
      break;
    case XMLEvent.SPACE:
    case XMLEvent.CHARACTERS:
      //System.out.print(xmlr.getText());
      int start = xmlr.getTextStart();
      int length = xmlr.getTextLength();
      System.out.print(new String(xmlr.getTextCharacters(),
                                  start,
                                  length));
      break;
    case XMLEvent.PROCESSING_INSTRUCTION:
      System.out.print("<?");
      if (xmlr.hasText())
        System.out.print(xmlr.getText());
      System.out.print("?>");
      break;
    case XMLEvent.CDATA:
      System.out.print("<![CDATA[");
      if (xmlr.hasText())
        System.out.print(xmlr.getText());
      System.out.print("]]>");
      break;

    case XMLEvent.COMMENT:
      System.out.print("<!--");
      if (xmlr.hasText())
        System.out.print(xmlr.getText());
      System.out.print("-->");
      break;
    case XMLEvent.ENTITY_REFERENCE:
      System.out.print(xmlr.getLocalName()+"=");
      if (xmlr.hasText())
        System.out.print("["+xmlr.getText()+"]");
      break;
    case XMLEvent.START_DOCUMENT:
      System.out.print("<?xml");
      System.out.print(" version='"+xmlr.getVersion()+"'");
      System.out.print(" encoding='"+xmlr.getCharacterEncodingScheme()+"'");
      if (xmlr.isStandalone())
        System.out.print(" standalone='yes'");
      else
        System.out.print(" standalone='no'");
      System.out.print("?>");
      break;

    }
    System.out.println("]");
  }
  private static void printEventType(int eventType) {
    System.out.print("EVENT TYPE("+eventType+"):");
    System.out.println(getEventTypeString(eventType));
  }

  private static void printName(XMLStreamReader xmlr){
    if(xmlr.hasName()){
      String prefix = xmlr.getPrefix();
      String uri = xmlr.getNamespaceURI();
      String localName = xmlr.getLocalName();
      printName(prefix,uri,localName);
    } 
  }

  private static void printName(String prefix,
                                String uri,
                                String localName) {
    if (uri != null && !("".equals(uri)) ) System.out.print("['"+uri+"']:");
    if (prefix != null) System.out.print(prefix+":");
    if (localName != null) System.out.print(localName);
  }
  
  private static void printValue(XMLStreamReader xmlr){
    if(xmlr.hasText()){
      System.out.println("HAS VALUE: " + xmlr.getText());
    } else {
      System.out.println("HAS NO VALUE");
    }
  }

  private static void printAttributes(XMLStreamReader xmlr){
    if(xmlr.getAttributeCount()>0){
      Iterator ai = com.bea.xml.stream.XMLEventAllocatorBase.getAttributes(xmlr);
      while(ai.hasNext()){
        System.out.print(" ");
        Attribute a = (Attribute) ai.next();
        printAttribute(a);
      }            
    } 
  }
  
  private static void printAttribute(Attribute a) {
    printName(a.getName().getPrefix(),a.getName().getNamespaceURI(),
              a.getName().getLocalPart());
    System.out.print("='"+a.getValue()+"'");
  }
  
 private static void printNamespaces(Iterator ni){
   while(ni.hasNext()){
     System.out.print(" ");
     Namespace n = (Namespace) ni.next();
     printNamespace(n);
   }            
  }
  
  private static void printNamespace(Namespace n) {
    if (n.isDefaultNamespaceDeclaration()) 
      System.out.print("xmlns='"+n.getNamespaceURI()+"'");
    else
      System.out.print("xmlns:"+n.getPrefix()+"='"+n.getNamespaceURI()+"'");
  }
}




