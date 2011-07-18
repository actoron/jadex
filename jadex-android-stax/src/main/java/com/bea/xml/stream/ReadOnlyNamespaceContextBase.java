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

import java.util.Iterator;
import java.util.HashSet;

import javaxx.xml.XMLConstants;
import javaxx.xml.namespace.NamespaceContext;

/**
 * <p> This class provides a ReadOnlyNamespace context that 
 * takes a snapshot of the current namespaces in scope </p>
 */

public class ReadOnlyNamespaceContextBase 
  implements NamespaceContext
{
  private String[] prefixes;
  private String[] uris;

  public ReadOnlyNamespaceContextBase(String[] prefixArray,
                                      String[] uriArray,
                                      int size) 
  {
    prefixes = new String[size];
    uris = new String[size];
    System.arraycopy(prefixArray, 0, prefixes, 0, prefixes.length);
    System.arraycopy(uriArray, 0, uris, 0, uris.length);
   }

  public String getNamespaceURI(String prefix) {
    if (prefix == null)
      throw new IllegalArgumentException("Prefix may not be null.");
    if(prefix.length() > 0) { // explicit prefix (not default ns)
      for( int i = uris.length -1; i >= 0; i--) {
        if( prefix.equals( prefixes[ i ] ) ) {
          return uris[ i ];
        }
      }
      if("xml".equals( prefix )) {
        return XMLConstants.XML_NS_URI;
      } else if("xmlns".equals( prefix )) {
        return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
      }
    } else {
      for( int i = uris.length -1; i >= 0; i--) { // default NS
        if( prefixes[ i ]  == null ) {
          return uris[ i ];
        }
      }
    }
    return null;
  }
  public String getPrefix(String uri) {
    if (uri == null)
      throw new IllegalArgumentException("uri may not be null");
    if (uri.length() == 0)
      throw new IllegalArgumentException("uri may not be empty string");
    
    main_loop:
    for( int i = uris.length -1; i >= 0; i--) {
        if( uri.equals( uris[ i ] ) ) {
            /* 21-Mar-2006, TSa: Possible match; but we have to ensure that
             *   the prefix is not masked by a later declaration:
             */
            String prefix = prefixes[i];
            if (prefix == null) { // default NS
                for (int j = uris.length-1; j > i; --j) {
                    if (prefixes[j] == null) {
                        continue main_loop;
                    }
                }
                return "";
            }
            // nope, explicit prefix
            for (int j = uris.length-1; j > i; --j) {
                if (prefix.equals(prefixes[j])) {
                    continue main_loop;
                }
            }
            return prefix;
        }
    }
	if(XMLConstants.XML_NS_URI.equals(uri)) {
        return "xml";
    }
	if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(uri)) {
		return "xmlns";
    }
    return null;
  }

  public String getDefaultNameSpace() {
    for( int i = uris.length -1; i >= 0; i--) {
      if( prefixes[ i ]  == null ) {
        return uris[ i ];
      }
    }
    return null;
  }
  
  private String checkNull(String s) {
    if (s == null) return "";
    return s;
  }

  public Iterator getPrefixes(String uri) {
    if (uri == null)
      throw new IllegalArgumentException("uri may not be null");
    if ("".equals(uri))
      throw new IllegalArgumentException("uri may not be empty string");
    HashSet s = new HashSet();

    main_loop:
    for( int i = uris.length -1; i >= 0; i--) {
      String prefix = checkNull(prefixes[i]);
      if (!uri.equals(uris[i]) || s.contains(prefix)) {
          continue;
      }
      
      /* 21-Mar-2006, TSa: Match, but the prefix may be masked by a later
       *    declaration
       */
      if (prefix.length() == 0) { // default NS
          for (int j = uris.length-1; j > i; --j) {
              if (prefixes[j] == null) {
                  continue main_loop;
              }
          }
      } else {
          // nope, explicit prefix
          for (int j = uris.length-1; j > i; --j) {
              if (prefix.equals(prefixes[j])) {
                  continue main_loop;
              }
          }
      }
    
      s.add(prefix);
    }
    return s.iterator();
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    for (int i=0; i < uris.length; i++) {
      b.append("["+checkNull(prefixes[i])+"<->"+uris[i]+"]");
    }
    return b.toString();
  }

  public static void main(String[] args) throws Exception {
    MXParser p = new MXParser();
    p.setInput(new java.io.FileReader(args[0]));
    while (p.hasNext()) {
      if (p.isStartElement()) {
        System.out.println("context["+p.getNamespaceContext()+"]");
        Iterator i = p.getNamespaceContext().getPrefixes("a");
        while (i.hasNext())
          System.out.println("Found prefix:"+i.next());
      }
      p.next();
    }
  }
}
