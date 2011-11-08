package org.iso_relax.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javaxx.xml.parsers.ParserConfigurationException;
import javaxx.xml.parsers.SAXParser;
import javaxx.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * RELAXCatalog
 *
 * @since   Feb. 23, 2001
 * @version Feb. 23, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class RELAXCatalog {
    private Map grammars_ = new HashMap();

    public RELAXCatalog()
	throws ParserConfigurationException, SAXException, IOException {
	this ("http://www.iso-relax.org/catalog");
    }

    public RELAXCatalog(String rootURI)
	throws ParserConfigurationException, SAXException, IOException {

	String catalogFile = rootURI + "/catalog.xml";
	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser saxParser = factory.newSAXParser();
	saxParser.parse(catalogFile, new CatalogHandler());
    }

    public InputSource getGrammar(String uri) {
	String location = (String)grammars_.get(uri);
	if (location == null) {
	    return (null);
	}
	return (new InputSource(location));
    }

    class CatalogHandler extends DefaultHandler {
	public void startElement(
	    String namespaceURI,
	    String localName,
	    String qName,
	    Attributes atts
	) {
	    String uri = atts.getValue("uri");
	    String grammar = atts.getValue("grammar");
	    grammars_.put(uri, grammar);
	}
    }
}
