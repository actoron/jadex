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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashSet;

import javaxx.xml.stream.EventFilter;
import javaxx.xml.stream.StreamFilter;
import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLReporter;
import javaxx.xml.stream.XMLResolver;
import javaxx.xml.stream.util.XMLEventAllocator;

public class ConfigurationContextBase

{
  private static HashSet supportedFeatures;

  private static String EVENT_FILTER = "RI_EVENT_FILTER";
  private static String STREAM_FILTER = "RI_STREAM_FILTER";
  private static String NOTATIONS = "javax.xml.stream.notations";
  private static String ENTITIES = "javax.xml.stream.entities";
  static final String REPORT_CDATA = "http://java.sun.com/xml/stream/properties/report-cdata-event";
  static {
    supportedFeatures = new HashSet();
    supportedFeatures.add(XMLInputFactory.IS_VALIDATING);
    supportedFeatures.add(XMLInputFactory.IS_COALESCING);
    supportedFeatures.add(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES);
    supportedFeatures.add(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES);
    supportedFeatures.add(XMLOutputFactory.IS_REPAIRING_NAMESPACES);
    supportedFeatures.add(XMLInputFactory.IS_NAMESPACE_AWARE);
    supportedFeatures.add(XMLInputFactory.SUPPORT_DTD);
    supportedFeatures.add(XMLInputFactory.REPORTER);
    supportedFeatures.add(XMLInputFactory.RESOLVER);
    supportedFeatures.add(XMLInputFactory.ALLOCATOR);
    supportedFeatures.add(NOTATIONS);
    supportedFeatures.add(ENTITIES);
    supportedFeatures.add(REPORT_CDATA);
  };

  private Hashtable features = new Hashtable();
  public ConfigurationContextBase() {
    features.put(XMLInputFactory.IS_VALIDATING,
                 Boolean.FALSE);
    features.put(XMLInputFactory.IS_COALESCING,
                 Boolean.FALSE);
    features.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
                 Boolean.TRUE);
    features.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
                 Boolean.FALSE);
    features.put(XMLInputFactory.IS_NAMESPACE_AWARE,
                 Boolean.TRUE);
    features.put(XMLInputFactory.SUPPORT_DTD,
                 Boolean.FALSE);
    features.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES,
                 Boolean.FALSE);

  }

  public void setEventAllocator(XMLEventAllocator a) {
    features.put(XMLInputFactory.ALLOCATOR,a);
  }
  public XMLEventAllocator getEventAllocator() {
    return (XMLEventAllocator) features.get(XMLInputFactory.ALLOCATOR);
  }

  public void setProperty(String name, Object feature) {
    if (name.equals(XMLInputFactory.IS_VALIDATING)) {
        setValidating(((Boolean) feature).booleanValue());
    } else if (name.equals(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES)) {
        setSupportExternalEntities(((Boolean) feature).booleanValue());
    } else if (name.equals(XMLInputFactory.IS_NAMESPACE_AWARE)) {
        setNamespaceAware(((Boolean) feature).booleanValue());
    } else {
        check(name);
        features.put(name,feature);
    }
  }

  public void check(String name) {
    if (!supportedFeatures.contains(name))
      throw new IllegalArgumentException("Unable to access unsupported "+
                                         "property "+name);
  }

  public  Object getProperty(String name) {
    check(name);
    return features.get(name);
  }

  public void setXMLReporter(XMLReporter r) {
    features.put(XMLInputFactory.REPORTER,r);
  }

  public  XMLReporter getXMLReporter() {
    return (XMLReporter) features.get(XMLInputFactory.REPORTER);
  }

  public void setXMLResolver(XMLResolver r) {
    features.put(XMLInputFactory.RESOLVER,r);
  }
  public XMLResolver getXMLResolver() {
    return (XMLResolver) features.get(XMLInputFactory.RESOLVER);
  }

  public boolean getBool(String name) {
    check(name);
    Boolean val = (Boolean) features.get(name);
    return val.booleanValue();
  }

  public void setBool(String name, boolean val) {
    check(name);
    features.put(name, val ? Boolean.TRUE : Boolean.FALSE);
  }
  public void setCoalescing(boolean val) {
    setBool(XMLInputFactory.IS_COALESCING,val);
  }
  public boolean isCoalescing() {
    return getBool(XMLInputFactory.IS_COALESCING);
  }


  public void setValidating(boolean val) {
      if (val) {
        throw new IllegalArgumentException("This implementation does not " +
                "support validation");
      }
      // No need to re-set to the same value...
      //setBool(XMLInputFactory.IS_VALIDATING,val);
  }

  public boolean isValidating() {
    return getBool(XMLInputFactory.IS_VALIDATING);
  }

  public void setReplacingEntities(boolean val) {
    setBool(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,val);
  }

  public boolean isReplacingEntities() {
    return getBool(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES);
  }


  public void setSupportExternalEntities(boolean val) {
      if (val) {
        throw new IllegalArgumentException("This implementation does not " +
                "resolve external entities ");
      }
      // Already false...
      //setBool(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,val);
  }

  public boolean isSupportingExternalEntities() {
    return getBool(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES);
  }

  public void setPrefixDefaulting(boolean val) {
    setBool(XMLOutputFactory.IS_REPAIRING_NAMESPACES,val);
  }

  public boolean isPrefixDefaulting() {
    return getBool(XMLOutputFactory.IS_REPAIRING_NAMESPACES);
  }

  public void setNamespaceAware(boolean val) {
      /* 07-Sep-2005, TSa: since implementation does not really support
       *   non-ns-aware mode, let's throw an exception:
       */
      if (!val) {
          throw new IllegalArgumentException("This implementation does not allow disabling namespace processing");
      }
      // Already true
      //setBool(XMLInputFactory.IS_NAMESPACE_AWARE,val);
  }

  public boolean isNamespaceAware() {
    return getBool(XMLInputFactory.IS_NAMESPACE_AWARE);
  }


  public String getVersion() {
    return "1.0";
  }

  public Enumeration getProperties() {
    return features.keys();
  }

  public boolean isPropertySupported(String name) {
    return supportedFeatures.contains(name);
  }

}
