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

package com.bea.xml.stream.util;

import javaxx.xml.namespace.NamespaceContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Iterator;

public class NamespaceContextImpl 
  implements NamespaceContext
{
  SymbolTable prefixTable = new SymbolTable();
  SymbolTable uriTable = new SymbolTable();
  NamespaceContext rootContext;
  public NamespaceContextImpl() {
    init();
  }
  public NamespaceContextImpl(NamespaceContext rootContext) {
    this.rootContext = null;
    init();
  }
  public void init() {
    bindNamespace("xml","http://www.w3.org/XML/1998/namespace");
    bindNamespace("xmlns","http://www.w3.org/XML/1998/namespace");
  }
  public void openScope() {
    prefixTable.openScope();
    uriTable.openScope();
  }
  public void closeScope() {
    prefixTable.closeScope();
    uriTable.closeScope();
  }

  public void bindNamespace(String prefix, String uri) {
    prefixTable.put(prefix,uri);
    uriTable.put(uri,prefix);
  }

  public int getDepth() {
    return prefixTable.getDepth();
  }

  public String getNamespaceURI(String prefix) {
    String value = prefixTable.get(prefix);
    if (value == null && rootContext != null)
      return rootContext.getNamespaceURI(prefix);
    else
      return value;
  }

  public String getPrefix(String uri) {
    String value = uriTable.get(uri);
    if (value == null && rootContext != null)
      return rootContext.getPrefix(uri);
    else
      return value;
  }

  public void bindDefaultNameSpace(String uri) {
    bindNamespace("",uri);
  }
  public void unbindDefaultNameSpace() {
    bindNamespace("",null);
  }

  public void unbindNamespace(String prefix, String uri) {
    prefixTable.put(prefix,null);
    prefixTable.put(uri,null);
  }

  public String getDefaultNameSpace() {
    return getNamespaceURI("");
  }

  public Iterator getPrefixes(String uri) {
    return (uriTable.getAll(uri)).iterator();
  }

  public static void main(String args[]) throws Exception {
    NamespaceContextImpl nci = new NamespaceContextImpl();
    nci.openScope();
    nci.bindNamespace("a","uri");
    nci.bindNamespace("b","uri");
    System.out.println("a="+nci.getNamespaceURI("a"));
    System.out.println("uri="+nci.getPrefix("uri"));
    
    Iterator vals = nci.getPrefixes("uri");
    while(vals.hasNext())
      System.out.println("1 uri->"+vals.next());

    nci.openScope();
    nci.bindNamespace("a","uri2");
    vals = nci.getPrefixes("uri");
    while(vals.hasNext())
      System.out.println("2 uri->"+vals.next());
    nci.closeScope();
    nci.closeScope();
  }
}
