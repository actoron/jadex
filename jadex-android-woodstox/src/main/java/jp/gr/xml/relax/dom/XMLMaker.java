package jp.gr.xml.relax.dom;

import jp.gr.xml.relax.xml.UXML;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * XMLMaker
 *
 * @since   Oct. 27, 2000
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class XMLMaker implements IDOMVisitor {
    protected StringBuffer buffer_;
    protected String encoding_ = "UTF-8";
    protected boolean dom2_ = false;
    protected boolean expandEntityReference_ = false;
    protected boolean emptyElementTag_ = false;

    public XMLMaker() {
	buffer_ = new StringBuffer();
    }

    public void setEncoding(String encoding) {
	encoding_ = encoding;
    }

    public void setDOM2(boolean dom2) {
	dom2_ = dom2;
    }

    public void setExpandEntityReference(boolean expand) {
	expandEntityReference_ = expand;
    }

    public void setEmptyElementTag(boolean empty) {
	emptyElementTag_ = empty;
    }

    public String getText() {
	return (new String(buffer_));
    }

    public boolean enter(Element element) {
	String tag = element.getTagName();
	buffer_.append("<");
	buffer_.append(tag);
	NamedNodeMap attrs = element.getAttributes();
	int nAttrs = attrs.getLength();
	for (int i = 0;i < nAttrs;i++) {
	    Attr attr = (Attr)attrs.item(i);
	    if (attr.getSpecified()) {
		buffer_.append(' ');
		enter(attr);
		leave(attr);
	    }
	}
	buffer_.append(">");
	return (true);
    }

    public void leave(Element element) {
	String tag = element.getTagName();
	buffer_.append("</" + tag + ">");
    }

    public boolean enter(Attr attr) {
	buffer_.append(attr.getName());
	buffer_.append("=\"");
	buffer_.append(UXML.escapeAttrQuot(attr.getValue()));
	buffer_.append('\"');
	return (true);
    }

    public void leave(Attr attr) {
	// do nothing
    }

    public boolean enter(Text text) {
	buffer_.append(UXML.escapeCharData(text.getData()));
	return (true);
    }

    public void leave(Text text) {
	// do nothing
    }

    public boolean enter(CDATASection cdata) {
	buffer_.append("<![CDATA[");
	buffer_.append(cdata.getData());
	buffer_.append("]]>");
	return (true);
    }

    public void leave(CDATASection cdata) {
	// do nothing
    }

    public boolean enter(EntityReference entityRef) {
	buffer_.append("&");
	buffer_.append(entityRef.getNodeName());
	buffer_.append(";");
	return (false);
    }

    public void leave(EntityReference entityRef) {
	// do nothing
    }

    public boolean enter(Entity entity) {
	String name = entity.getNodeName();
	String pid = entity.getPublicId();
	String sid = entity.getSystemId();
	String notation = entity.getNotationName();
	buffer_.append("<!ENTITY ");
	buffer_.append(name);
	if (sid != null) {
	    if (pid != null) {
		buffer_.append(" PUBLIC \"");
		buffer_.append(pid);
		buffer_.append("\" \"");
		buffer_.append(UXML.escapeSystemQuot(sid));
		buffer_.append("\">");
	    } else {
		buffer_.append(" SYSTEM \"");
		buffer_.append(UXML.escapeSystemQuot(sid));
		buffer_.append("\">");
	    }
	    if (notation != null) {
		buffer_.append(" NDATA ");
		buffer_.append(notation);
		buffer_.append(">");
	    }
	} else {
	    buffer_.append(" \"");
	    XMLMaker entityMaker = new XMLMaker();
	    UDOMVisitor.traverseChildren(entity, entityMaker);
	    buffer_.append(UXML.escapeEntityQuot(entityMaker.getText()));
	    buffer_.append("\"");
	    buffer_.append(">");
	}
	return (false);
    }

    public void leave(Entity entity) {
	// do nothing
    }

    public boolean enter(ProcessingInstruction pi) {
	buffer_.append("<?");
	buffer_.append(pi.getTarget());
	buffer_.append(" ");
	buffer_.append(pi.getData());
	buffer_.append("?>");
	return (true);
    }

    public void leave(ProcessingInstruction pi) {
	// do nothing
    }

    public boolean enter(Comment comment) {
	buffer_.append("<!--");
	buffer_.append(comment.getData());
	buffer_.append("-->");
	return (true);
    }

    public void leave(Comment comment) {
	// do nothing
    }

    public boolean enter(Document doc) {
	buffer_.append("<?xml version=\"1.0\" encoding=\"");
	buffer_.append(encoding_);
	buffer_.append("\" ?>\n");
	return (true);
    }

    public void leave(Document doc) {
	// do nothing
    }

    public boolean enter(DocumentType doctype) {
	if (dom2_) {
	    String name = doctype.getName();
	    String publicId = doctype.getPublicId();
	    String systemId = doctype.getSystemId();
	    String internalSubset = doctype.getInternalSubset();
	    buffer_.append("<!DOCTYPE ");
	    buffer_.append(name);
	    if (publicId != null) {
		buffer_.append(" PUBLIC \"");
		buffer_.append(publicId);
		buffer_.append("\"");
	    }
	    if (systemId != null) {
		buffer_.append(" SYSTEM \"");
		buffer_.append(systemId);
		buffer_.append("\"");
	    }
	    if (internalSubset != null) {
		buffer_.append(" [");
		buffer_.append(internalSubset);
		buffer_.append("]");
	    }
	    buffer_.append(">\n");
	    return (true);
	} else {
	    String name = doctype.getName();
	    NamedNodeMap entities = doctype.getEntities();
	    NamedNodeMap notations = doctype.getNotations();
	    buffer_.append("<!DOCTYPE ");
	    buffer_.append(name);
	    if (entities != null && entities.getLength() > 0 ||
		notations != null && notations.getLength() > 0) {
	    
		buffer_.append(" [");
		int nEntities = entities.getLength();
		for (int i = 0;i < nEntities;i++) {
		    XMLMaker entityMaker = new XMLMaker();
		    UDOMVisitor.traverse(entities.item(i), entityMaker);
		    buffer_.append(entityMaker.getText());
		}
		int nNotations = notations.getLength();
		for (int i = 0;i < nNotations;i++) {
		    enter((Notation)notations.item(i));
		    leave((Notation)notations.item(i));
		}
		buffer_.append("]");
	    }
	    buffer_.append(">\n");
	    return (true);
	}
    }

    public void leave(DocumentType doctype) {
	// do nothing
    }

    public boolean enter(DocumentFragment docfrag) {
	// do nothing
	return (true);
    }

    public void leave(DocumentFragment docfrag) {
	// do nothing
    }

    public boolean enter(Notation notation) {
	String name = notation.getNodeName();
	String pid = notation.getPublicId();
	String sid = notation.getSystemId();
	buffer_.append("<!NOTATION ");
	buffer_.append(name);
	if (pid != null) {
	    buffer_.append(" PUBLIC \"");
	    buffer_.append(pid);
	    buffer_.append("\"");
	    if (sid != null) {
		buffer_.append(" \"");
		buffer_.append(UXML.escapeSystemQuot(sid));
		buffer_.append("\"");
	    }
	} else if (sid != null) {
	    buffer_.append(" SYSTEM \"");
	    buffer_.append(UXML.escapeSystemQuot(sid));
	    buffer_.append("\"");
	}
	buffer_.append(">");
	return (true);
    }

    public void leave(Notation notation) {
	// do nothing
    }

    public boolean enter(Node node) {
	throw (new InternalError(node.toString()));
    }

    public void leave(Node node) {
	throw (new InternalError(node.toString()));
    }

    public boolean isParsedEntity(EntityReference entityRef) {
	String name = entityRef.getNodeName();
	Document doc = entityRef.getOwnerDocument();
	DocumentType doctype = doc.getDoctype();
	if (doctype == null) {
	    return (false);
	}
	NamedNodeMap entities = doctype.getEntities();
	Entity entity = (Entity)entities.getNamedItem(name);
	if (entity == null) {
	    return (false);
	}
	return (entity.getNotationName() == null);
    }
}
