package com.bea.xml.stream.samples;

import java.io.FileReader;

import javaxx.xml.stream.XMLEventReader;
import javaxx.xml.stream.XMLEventWriter;
import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.*;
import javaxx.xml.stream.events.*;

import com.bea.xml.stream.util.ElementTypeNames;
/**
 * @author Copyright (c) 2002 by BEA Systems. All Rights Reserved.
 */

public class EventWrite {

  private static String filename = null;
  
  private static void printUsage() {
    System.out.println("usage: java com.bea.xml.stream.samples.EventWrite <xmlfile>");
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
    System.setProperty("javax.xml.stream.XMLOutputFactory", 
                       "com.bea.xml.stream.XMLOutputFactoryBase");
    System.setProperty("javax.xml.stream.XMLEventFactory",
                       "com.bea.xml.stream.EventFactory");
    
    XMLInputFactory xmlif = XMLInputFactory.newInstance();
    XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
    xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
                      Boolean.TRUE);


    XMLEventReader xmlr = xmlif.createXMLEventReader(new FileReader(filename));
    XMLEventWriter xmlw = xmlof.createXMLEventWriter(System.out);
    
    xmlw.add(xmlr);
    xmlw.flush();
  }
}




