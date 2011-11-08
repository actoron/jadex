package jp.gr.xml.relax.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * UDOMVisitor
 *
 * @since   Oct.  7, 2000
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public final class UDOMVisitor {
    public static void traverse(
	Node node,
	IDOMVisitor visitor
    ) throws DOMVisitorException {
	boolean doContinue;
	switch (node.getNodeType()) {

	case Node.ELEMENT_NODE:
	    doContinue = visitor.enter((Element)node);
	    break;
	case Node.ATTRIBUTE_NODE:
	    doContinue = visitor.enter((Attr)node);
	    break;
	case Node.TEXT_NODE:
	    doContinue = visitor.enter((Text)node);
	    break;
	case Node.CDATA_SECTION_NODE:
	    doContinue = visitor.enter((CDATASection)node);
	    break;
	case Node.ENTITY_REFERENCE_NODE:
	    doContinue = visitor.enter((EntityReference)node);
	    break;
	case Node.ENTITY_NODE:
	    doContinue = visitor.enter((Entity)node);
	    break;
	case Node.PROCESSING_INSTRUCTION_NODE:
	    doContinue = visitor.enter((ProcessingInstruction)node);
	    break;
	case Node.COMMENT_NODE:
	    doContinue = visitor.enter((Comment)node);
	    break;
	case Node.DOCUMENT_NODE:
	    doContinue = visitor.enter((Document)node);
	    break;
	case Node.DOCUMENT_TYPE_NODE:
	    doContinue = visitor.enter((DocumentType)node);
	    break;
	case Node.DOCUMENT_FRAGMENT_NODE:
	    doContinue = visitor.enter((DocumentFragment)node);
	    break;
	case Node.NOTATION_NODE:
	    doContinue = visitor.enter((Notation)node);
	    break;
	default:
	    doContinue = visitor.enter(node);
	    break;
	}
	if (doContinue) {
	    traverseChildren(node, visitor);
	    switch (node.getNodeType()) {

	    case Node.ELEMENT_NODE:
		visitor.leave((Element)node);
		break;
	    case Node.ATTRIBUTE_NODE:
		visitor.leave((Attr)node);
		break;
	    case Node.TEXT_NODE:
		visitor.leave((Text)node);
		break;
	    case Node.CDATA_SECTION_NODE:
		visitor.leave((CDATASection)node);
		break;
	    case Node.ENTITY_REFERENCE_NODE:
		visitor.leave((EntityReference)node);
		break;
	    case Node.ENTITY_NODE:
		visitor.leave((Entity)node);
		break;
	    case Node.PROCESSING_INSTRUCTION_NODE:
		visitor.leave((ProcessingInstruction)node);
		break;
	    case Node.COMMENT_NODE:
		visitor.leave((Comment)node);
		break;
	    case Node.DOCUMENT_NODE:
		visitor.leave((Document)node);
		break;
	    case Node.DOCUMENT_TYPE_NODE:
		visitor.leave((DocumentType)node);
		break;
	    case Node.DOCUMENT_FRAGMENT_NODE:
		visitor.leave((DocumentFragment)node);
		break;
	    case Node.NOTATION_NODE:
		visitor.leave((Notation)node);
		break;
	    default:
		visitor.leave(node);
		break;
	    }
	}
    }

    public static void traverseChildren(Node node, IDOMVisitor visitor) {
	NodeList children = node.getChildNodes();
	int size = children.getLength();
	for (int i = 0;i < size;i++) {
	    traverse(children.item(i), visitor);
	}
    }
}
