package jp.gr.xml.relax.sax;

import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * DTDSkipper
 *
 * @since   May. 28, 2001
 * @version May. 28, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class DTDSkipper implements EntityResolver {
    public InputSource resolveEntity(String publicId, String systemId) {
        if (!systemId.endsWith(".dtd")) {
            return (null);
        }
        StringReader reader = new StringReader("");
        InputSource is = new InputSource(reader);
        return (is);
    }
}
