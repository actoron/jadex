package jp.gr.xml.relax.sax;

import jp.gr.xml.relax.dom.DOMVisitorException;
import jp.gr.xml.relax.dom.IDOMVisitor;
import jp.gr.xml.relax.dom.UDOM;

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
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * DOMVisitor of SAX event producer from DOM tree
 *
 * @since   Feb. 18, 2001
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class DOMSAXProducerVisitor implements IDOMVisitor {
    private String systemID_;
    private String publicID_;
    private DTDHandler dtd_;
    private ContentHandler content_;
    private DeclHandler decl_;
    private LexicalHandler lexical_;
    private ErrorHandler error_;
    private NamespaceSupport namespace_;
    private boolean throwException_;

    public DOMSAXProducerVisitor() {
        DefaultHandler handler = new DefaultHandler();
        dtd_ = handler;
        content_ = handler;
        error_ = handler;
        lexical_ = new LexicalHandlerBase();
        decl_ = new DeclHandlerBase();
        namespace_ = new NamespaceSupport();
        throwException_ = false;
    }

    public void setSystemID(String id) {
        systemID_ = id;
    }

    public void setPublicID(String id) {
        publicID_ = id;
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

    public void emulateStartDocument() {
        try {
            _handleLocator();
            content_.startDocument();
        } catch (SAXException e) {
            _errorReport(e);
        }
    }

    public void emulateEndDocument() {
        try {
            content_.endDocument();
        } catch (SAXException e) {
            _errorReport(e);
        }
    }

    public void throwException(boolean useException) {
        throwException_ = useException;
    }

    public boolean enter(Element element) {
        try {
            namespace_.pushContext();
            String namespaceURI = element.getNamespaceURI();
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            String localName = element.getLocalName();
            String qName = element.getTagName();
            NamedNodeMap attrMap = element.getAttributes();
            AttributesImpl attrs = new AttributesImpl();
            int size = attrMap.getLength();
            for (int i = 0; i < size; i++) {
                Attr attr = (Attr) attrMap.item(i);
                String attrNamespaceURI = attr.getNamespaceURI();
                if (attrNamespaceURI == null) {
                    attrNamespaceURI = "";
                }
                String attrLocalName = attr.getLocalName();
                String attrQName = attr.getName();
                String attrValue = attr.getValue();
                if (attrQName.startsWith("xmlns")) {
                    String prefix;
                    int index = attrQName.indexOf(':');
                    if (index == -1) {
                        prefix = "";
                    } else {
                        prefix = attrQName.substring(index + 1);
                    }
                    if (!namespace_.declarePrefix(prefix, attrValue)) {
                        _errorReport("bad prefix = " + prefix);
                    } else {
                        content_.startPrefixMapping(prefix, attrValue);
                    }
                } else {
                    attrs.addAttribute(
                        attrNamespaceURI,
                        attrLocalName,
                        attrQName,
                        "CDATA",
                        attrValue);
                }
            }
            content_.startElement(namespaceURI, localName, qName, attrs);
        } catch (SAXException e) {
            _errorReport(e);
        }
        return (true);
    }

    public boolean enter(Attr attr) {
        return (false);
    }

    public boolean enter(Text text) {
        try {
            String data = text.getData();
            content_.characters(data.toCharArray(), 0, data.length());
        } catch (SAXException e) {
            _errorReport(e);
        }
        return (false);
    }

    public boolean enter(CDATASection cdata) {
        try {
            lexical_.startCDATA();
            String data = cdata.getData();
            content_.characters(data.toCharArray(), 0, data.length());
            lexical_.endCDATA();
        } catch (SAXException e) {
            _errorReport(e);
        }
        return (false);
    }

    public boolean enter(EntityReference entityRef) {
        try {
            lexical_.startEntity(entityRef.getNodeName());
        } catch (SAXException e) {
            _errorReport(e);
        }
        return (true);
    }

    public boolean enter(Entity entity) {
        return (false);
    }

    public boolean enter(ProcessingInstruction pi) {
        try {
            content_.processingInstruction(pi.getTarget(), pi.getData());
        } catch (SAXException e) {
            _errorReport(e);
        }
        return (false);
    }

    public boolean enter(Comment comment) {
        try {
            String data = comment.getData();
            lexical_.comment(data.toCharArray(), 0, data.length());
        } catch (SAXException e) {
            _errorReport(e);
        }
        return (false);
    }

    public boolean enter(Document doc) {
        try {
            _handleLocator();
            content_.startDocument();
            _handleDoctype(doc.getDoctype());
        } catch (SAXException e) {
            _errorReport(e);
        }
        return (true);
    }

    private void _handleLocator() {
        if (systemID_ == null && publicID_ == null) {
            return;
        }
        _locatorEvent(systemID_, publicID_);
    }

    private void _locatorEvent(String systemID, String publicID) {
        LocatorImpl locator = new LocatorImpl();
        locator.setSystemId(systemID_);
        locator.setPublicId(publicID_);
        locator.setLineNumber(-1);
        locator.setColumnNumber(-1);
        content_.setDocumentLocator(locator);
    }

    private void _handleDoctype(DocumentType doctype) {
        try {
            if (doctype == null) {
                return;
            }
            String systemID = doctype.getSystemId();
            String publicID = doctype.getPublicId();
            String internalSubset = doctype.getInternalSubset();
            if (systemID != null) {
                lexical_.startDTD(doctype.getName(), publicID, systemID);
                if (internalSubset == null) {
                    lexical_.endDTD();
                    _handleEntities(doctype);
                } else {
                    _handleEntities(doctype);
                    lexical_.endDTD();
                }
            } else {
                _handleEntities(doctype);
            }
        } catch (SAXException e) {
            _errorReport(e);
        }
    }

    private void _handleEntities(DocumentType doctype) {
        try {
            NamedNodeMap entities = doctype.getEntities();
            int nEntities = entities.getLength();
            for (int i = 0; i < nEntities; i++) {
                Entity entity = (Entity) entities.item(i);
                String publicID = entity.getPublicId();
                String systemID = entity.getSystemId();
                String notationName = entity.getNotationName();
                if (publicID != null || systemID != null) {
                    _handleExternalEntity(
                        entity.getNodeName(),
                        publicID,
                        systemID,
                        notationName);
                } else {
                    _handleInternalEntity(entity);
                }
            }
            NamedNodeMap notations = doctype.getNotations();
            int nNotations = notations.getLength();
            for (int i = 0; i < nNotations; i++) {
                Notation notation = (Notation) notations.item(i);
                String publicID = notation.getPublicId();
                String systemID = notation.getSystemId();
                dtd_.notationDecl(notation.getNodeName(), publicID, systemID);
            }
        } catch (SAXException e) {
            _errorReport(e);
        }
    }

    private void _handleExternalEntity(
        String name,
        String publicID,
        String systemID,
        String notationName) {
        try {
            if (notationName == null) {
                decl_.externalEntityDecl(name, publicID, systemID);
            } else {
                dtd_.unparsedEntityDecl(name, publicID, systemID, notationName);
            }
        } catch (SAXException e) {
            _errorReport(e);
        }
    }

    private void _handleInternalEntity(Entity entity) {
        try {
            decl_.internalEntityDecl(
                entity.getNodeName(),
                UDOM.getXMLText(entity));
        } catch (SAXException e) {
            _errorReport(e);
        }
    }

    public boolean enter(DocumentType doctype) {
        return (false);
    }

    public boolean enter(DocumentFragment docfrag) {
        return (true);
    }

    public boolean enter(Notation notation) {
        return (false);
    }

    public boolean enter(Node node) {
        return (false);
    }

    public void leave(Element element) {
        try {
            String namespaceURI = element.getNamespaceURI();
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            String localName = element.getLocalName();
            String qName = element.getTagName();
            content_.endElement(namespaceURI, localName, qName);
            namespace_.popContext();
        } catch (SAXException e) {
            _errorReport(e);
        }
    }

    public void leave(Attr attr) {
    }

    public void leave(Text text) {
    }

    public void leave(CDATASection cdata) {
    }

    public void leave(EntityReference entityRef) {
        try {
            lexical_.endEntity(entityRef.getNodeName());
        } catch (SAXException e) {
            _errorReport(e);
        }
    }

    public void leave(Entity entity) {
    }

    public void leave(ProcessingInstruction pi) {
    }

    public void leave(Comment comment) {
    }

    public void leave(Document doc) {
        try {
            content_.endDocument();
        } catch (SAXException e) {
            _errorReport(e);
        }
    }

    public void leave(DocumentType doctype) {
    }

    public void leave(DocumentFragment docfrag) {
    }

    public void leave(Notation notation) {
    }

    public void leave(Node node) {
    }

    private void _errorReport(String message) throws DOMVisitorException {
        _errorReport(
            new SAXParseException(message, publicID_, systemID_, -1, -1));
    }

    private void _errorReport(SAXException e) throws DOMVisitorException {
        try {
            SAXParseException parseException;
            if (e instanceof SAXParseException) {
                parseException = (SAXParseException) e;
            } else {
                parseException =
                    new SAXParseException(
                        e.getMessage(),
                        publicID_,
                        systemID_,
                        -1,
                        -1,
                        e);
            }
            error_.fatalError(parseException);
            if (throwException_) {
                throw (new DOMVisitorException(e));
            }
        } catch (SAXException ee) {
        }
    }
}
