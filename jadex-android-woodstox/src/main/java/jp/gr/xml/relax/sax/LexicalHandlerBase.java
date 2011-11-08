package jp.gr.xml.relax.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * Base class of LexicalHandler
 *
 * @since   Feb. 18, 2001
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class LexicalHandlerBase implements LexicalHandler {
    public void startDTD(String name, String publidId, String systemID)
        throws SAXException {
    }

    public void endDTD() throws SAXException {
    }

    public void startEntity(String name) throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
    }

    public void startCDATA() throws SAXException {
    }

    public void endCDATA() throws SAXException {
    }

    public void comment(char ch[], int start, int length) {
    }
}
