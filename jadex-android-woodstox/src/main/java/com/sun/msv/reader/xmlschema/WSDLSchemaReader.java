package com.sun.msv.reader.xmlschema;

import java.util.*;

import javaxx.xml.XMLConstants;
import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.parsers.SAXParserFactory;
import javaxx.xml.transform.Source;
import javaxx.xml.transform.TransformerConfigurationException;
import javaxx.xml.transform.TransformerException;
import javaxx.xml.transform.TransformerFactory;
import javaxx.xml.transform.dom.DOMResult;
import javaxx.xml.transform.dom.DOMSource;
import javaxx.xml.xpath.XPath;
import javaxx.xml.xpath.XPathConstants;
import javaxx.xml.xpath.XPathExpressionException;
import javaxx.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReaderController2;

/**
 * A utility class that reads all the schemas from a WSDL.
 */
public final class WSDLSchemaReader {
    private static final class SimpleNamespaceContext implements NamespaceContext {
        public String getNamespaceURI(String prefix) {
            if ("xs".equals(prefix)) {
                return XMLConstants.W3C_XML_SCHEMA_NS_URI;
            } else {
                return null;
            }
        }

        public String getPrefix(String namespaceURI) {
            if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(namespaceURI)) {
                return "xs";
            } else {
                return null;
            }

        }

        public Iterator<?> getPrefixes(String namespaceURI) {
            List<String> prefixes = new ArrayList<String>();
            if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(namespaceURI)) {
                prefixes.add("xs");
            }
            return prefixes.iterator();
        }
    }

    private WSDLSchemaReader() {
    }

    /**
     * Read the schemas from a WSDL.
     * 
     * @param wsdlSource the WSDL, in any of the TRaX sources.
     * @param factory a SAX parser factory, used to obtain a SAX parser used internally in the reading
     *            process.
     * @param controller Object to handle errors, warnings, and provide a resolver for non-local schemas.
     * @return the MSV grammar.
     * @throws XPathExpressionException
     * @throws TransformerException
     * @throws TransformerConfigurationException
     */
    public static XMLSchemaGrammar read(Source wsdlSource, SAXParserFactory factory,
                                        GrammarReaderController2 controller) throws XPathExpressionException,
        TransformerConfigurationException, TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        DOMResult wsdlDom = new DOMResult();
        transformerFactory.newTransformer().transform(wsdlSource, wsdlDom);
        Node wsdl = wsdlDom.getNode();

        // for the xml schema a:b references to work,
        // we have to push the wsdl mappings down when not already overriden
        Map<String, String> wsdlNamespaceMappings = new HashMap<String, String>();
        Document wsdlDoc = (Document)wsdl;
        NamedNodeMap attrMap = wsdlDoc.getDocumentElement().getAttributes();
        if (attrMap != null) {
            for (int x = 0; x < attrMap.getLength(); x++) {
                Attr attr = (Attr)attrMap.item(x);
                String ns = attr.getNamespaceURI();
                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(ns)) {
                    String localName = attr.getLocalName();
                    String uri = attr.getValue();
                    wsdlNamespaceMappings.put(localName, uri);
                }
            }
        }

        String wsdlSystemId = wsdlSource.getSystemId();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new SimpleNamespaceContext());

        Map<String, EmbeddedSchema> schemas = new HashMap<String, EmbeddedSchema>();

        NodeList schemaNodes = (NodeList)xpath.evaluate("//xs:schema", wsdl, XPathConstants.NODESET);
        for (int x = 0; x < schemaNodes.getLength(); x++) {
            Element schema = (Element)schemaNodes.item(x);
            String targetNamespace = schema.getAttribute("targetNamespace");
            String systemId = wsdlSystemId + "#" + x;
            EmbeddedSchema embeddedWSDLSchema = new EmbeddedSchema(systemId, schema);
            schemas.put(targetNamespace, embeddedWSDLSchema);
        }

        WSDLGrammarReaderController wsdlController = new WSDLGrammarReaderController(controller,
                                                                                     wsdlSystemId, schemas);

        XMLSchemaReader reader = new XMLSchemaReader(wsdlController);
        reader.setAdditionalNamespaceMap(wsdlNamespaceMappings);
        MultiSchemaReader multiSchemaReader = new MultiSchemaReader(reader);
        for (EmbeddedSchema schema : schemas.values()) {
            DOMSource source = new DOMSource(schema.getSchemaElement());
            source.setSystemId(schema.getSystemId());
            multiSchemaReader.parse(source);
        }
        return multiSchemaReader.getResult();
    }
}
