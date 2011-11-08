package jp.gr.xml.relax.sax;

import jp.gr.xml.relax.dom.DOMVisitorException;
import jp.gr.xml.relax.dom.UDOMVisitor;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * SAX event producer from DOM tree
 *
 * @since   Feb. 18, 2001
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class DOMSAXProducer {
    private boolean needDocumentEmulation_ = true;
    private Node root_;
    private String systemID_;
    private String publicID_;
    private DTDHandler dtd_;
    private ContentHandler content_;
    private DeclHandler decl_;
    private LexicalHandler lexical_;
    private ErrorHandler error_;

    public DOMSAXProducer(Node node) {
        root_ = node;
    }

    public void setDocumentEmulation(boolean emulate) {
        needDocumentEmulation_ = emulate;
    }

    public void setDTDHandler(DTDHandler dtd) {
        dtd_ = dtd;
    }

    public void setContentHandler(ContentHandler content) {
        content_ = content;
    }

    public void setLexicalHandler(LexicalHandler lexical) {
        lexical_ = lexical;
    }

    public void setDeclHandler(DeclHandler decl) {
        decl_ = decl;
    }

    public void setErrorHandler(ErrorHandler error) {
        error_ = error;
    }

    public void makeEvent() throws SAXException {
        try {
            DOMSAXProducerVisitor visitor = new DOMSAXProducerVisitor();
            visitor.setSystemID(systemID_);
            visitor.setPublicID(publicID_);
            visitor.setDTDHandler(dtd_);
            visitor.setContentHandler(content_);
            visitor.setLexicalHandler(lexical_);
            visitor.setDeclHandler(decl_);
            visitor.setErrorHandler(error_);
            if (!(root_ instanceof Document) && needDocumentEmulation_) {
                visitor.emulateStartDocument();
                UDOMVisitor.traverse(root_, visitor);
                visitor.emulateEndDocument();
            } else {
                UDOMVisitor.traverse(root_, visitor);
            }
        } catch (DOMVisitorException e) {
            Exception cause = e.getCauseException();
            if (cause == null) {
                throw (new SAXException(e.getMessage()));
            } else if (cause instanceof SAXException) {
                throw ((SAXException) cause);
            } else {
                throw (new SAXException(e.getMessage()));
            }
        }
    }

    public void makeEvent(ContentHandler handler) throws SAXException {
        setContentHandler(handler);
        makeEvent();
    }
}
