package com.bea.xml.stream;

import javaxx.xml.namespace.QName;

import com.bea.xml.stream.util.ElementTypeNames;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;


public class EventState {
  private int type;
  private QName qname;
  private List attributes;
  private List namespaces;
  private String data;
  private String extraData;

  public EventState(){}
  public EventState(int type) {
    this.type = type;
    attributes = new ArrayList();
    namespaces = new ArrayList();
  }

  public void clear() {
    qname = null;
    attributes = new ArrayList();
    namespaces = new ArrayList();
    data = null;
    extraData = null;
  }
  public void setType(int type) { this.type = type; }
  public int getType() { return type; }
  public QName getName() { return qname; }
  public String getLocalName() { return qname.getLocalPart();}
  public String getPrefix() { return qname.getPrefix(); }
  public String getNamespaceURI() { 
    return qname.getNamespaceURI();
  }
  public void setName(QName n) { qname = n; }
  public void setAttributes(List atts) { attributes = atts; }
  public void addAttribute(Object obj) {
    attributes.add(obj);
  }
  public void addNamespace(Object obj) {
    namespaces.add(obj);
  }
  public List getAttributes() { return attributes; }
  public void setNamespaces(List ns) { namespaces = ns; }
  public List getNamespaces() { return namespaces; }
  public String getData() { return data; }
  public void setData(String data) { this.data = data; }
  public String getExtraData() { return extraData; }
  public void setExtraData(String d) { this.extraData = d; }
  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("["+ElementTypeNames.getEventTypeString(type)+
             "]");
    if (qname != null)
      b.append("[name='"+qname+"']");
    Iterator i = namespaces.iterator();
    while(i.hasNext()) b.append(i.next()+" ");
    i = attributes.iterator();
    while(i.hasNext()) b.append(i.next()+" ");
    if (data != null)
      b.append(",data=["+data+"]");
    if (extraData != null)
      b.append(",extradata=["+extraData+"]");
    return b.toString();    
  }
}
