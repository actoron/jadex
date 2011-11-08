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
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * IDOMVisitor
 *
 * @since   Oct.  7, 2000
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public interface IDOMVisitor {
    boolean enter(Element element) throws DOMVisitorException;
    boolean enter(Attr attr) throws DOMVisitorException;
    boolean enter(Text text) throws DOMVisitorException;
    boolean enter(CDATASection cdata) throws DOMVisitorException;
    boolean enter(EntityReference entityRef) throws DOMVisitorException;
    boolean enter(Entity entity) throws DOMVisitorException;
    boolean enter(ProcessingInstruction pi) throws DOMVisitorException;
    boolean enter(Comment comment) throws DOMVisitorException;
    boolean enter(Document doc) throws DOMVisitorException;
    boolean enter(DocumentType doctype) throws DOMVisitorException;
    boolean enter(DocumentFragment docfrag) throws DOMVisitorException;
    boolean enter(Notation notation) throws DOMVisitorException;
    boolean enter(Node node) throws DOMVisitorException;
    void leave(Element element) throws DOMVisitorException;
    void leave(Attr attr) throws DOMVisitorException;
    void leave(Text text) throws DOMVisitorException;
    void leave(CDATASection cdata) throws DOMVisitorException;
    void leave(EntityReference entityRef) throws DOMVisitorException;
    void leave(Entity entity) throws DOMVisitorException;
    void leave(ProcessingInstruction pi) throws DOMVisitorException;
    void leave(Comment comment) throws DOMVisitorException;
    void leave(Document doc) throws DOMVisitorException;
    void leave(DocumentType doctype) throws DOMVisitorException;
    void leave(DocumentFragment docfrag) throws DOMVisitorException;
    void leave(Notation notation) throws DOMVisitorException;
    void leave(Node node) throws DOMVisitorException;
}

