package jp.gr.xml.relax.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * UDOM
 *
 * @since   Jan. 29, 2000
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public final class UDOM {
    // text generation
    public static String getXMLText(Document doc) {
	return (getXMLText(doc, "UTF-8"));
    }

    public static String getXMLText(Document doc, String encoding) {
	XMLMaker maker = new XMLMaker();
	UDOMVisitor.traverse(doc, maker);
	return (maker.getText());
    }

    public static String getXMLText(Node node) {
	XMLMaker maker = new XMLMaker();
	UDOMVisitor.traverse(node, maker);
	return (maker.getText());
    }	
}
