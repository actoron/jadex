package com.bea.xml.stream.samples;

import com.bea.xml.stream.util.ElementTypeNames;
import java.io.FileReader;
import java.util.Iterator;

import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.*;
import javaxx.xml.stream.events.*;

/**
 * @author Copyright (c) 2002 by BEA Systems. All Rights Reserved.
 */

public class Parse {

  private static String filename = null;
  
  private static void printUsage() {
    System.out.println("usage: java com.bea.xml.stream.samples.Parse <xmlfile>");
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

    XMLStreamReader xmlr = xmlif.createXMLStreamReader(new FileReader(filename));
    System.out.println("READER:  " + xmlr + "\n");
    
    int eventType = xmlr.getEventType();
    System.out.println("PARSER STATE BEFORE FIRST next(): ");
    printEventType(eventType);
    printName(xmlr);
    printValue(xmlr);
    System.out.println("-----------------------------");

    while(xmlr.hasNext()){
      eventType = xmlr.next();
      printEventType(eventType);
      printName(xmlr);
      printValue(xmlr);
      if(xmlr.isStartElement()){
        printAttributes(xmlr);
        printNamespaces(xmlr);
      }
      System.out.println("-----------------------------");
    }
    
    
  }

  public final static String getEventTypeString(int eventType) {
    return ElementTypeNames.getEventTypeString(eventType);
  }

  private static void printEventType(int eventType) {
    System.out.print("EVENT TYPE("+eventType+"):");
    System.out.println(getEventTypeString(eventType));
  }

  private static void printName(XMLStreamReader xmlr){
    if(xmlr.hasName()){
      System.out.println("HAS NAME: " + xmlr.getLocalName());
    } else {
      System.out.println("HAS NO NAME");
    }
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
      System.out.println("\nHAS ATTRIBUTES: ");
      Iterator ai = 
        com.bea.xml.stream.XMLEventAllocatorBase.getAttributes(xmlr);
      while(ai.hasNext()){
        Attribute a = (Attribute) ai.next();
        System.out.println("");
        printAttribute((Attribute) a);
      }            
    } else {
      System.out.println("HAS NO ATTRIBUTES");
    }
  }
  
  private static void printAttribute(Attribute a) {
    System.out.println("PREFIX: " + a.getName().getPrefix());
    System.out.println("NAMESP: " + a.getName().getNamespaceURI());
    System.out.println("NAME:   " + a.getName().getLocalPart());
    System.out.println("VALUE:  " + a.getValue());
    System.out.println("TYPE:   " + a.getDTDType());
  }
  
 private static void printNamespaces(XMLStreamReader xmlr){
    if(xmlr.getNamespaceCount()>0){
      System.out.println("\nHAS NAMESPACES: ");
      Iterator ni = 
        com.bea.xml.stream.XMLEventAllocatorBase.getNamespaces(xmlr);
      while(ni.hasNext()){
        Namespace n = (Namespace) ni.next();
        System.out.println("");
        printNamespace((Namespace)n);
      }            
    } else {
      System.out.println("HAS NO NAMESPACES");
    }
  }
  
  private static void printNamespace(Namespace a) {
    System.out.println("PREFIX: " + a.getName().getPrefix());
    System.out.println("NAMESP: " + a.getName().getNamespaceURI());
    System.out.println("NAME:   " + a.getName().getLocalPart());
    System.out.println("VALUE:  " + a.getValue());
    System.out.println("TYPE:   " + a.getDTDType());
  }
}




