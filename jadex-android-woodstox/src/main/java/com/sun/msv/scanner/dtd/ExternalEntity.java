/*
 * @(#)XmlChars.java    1.1 00/08/05
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.sun.msv.scanner.dtd;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
class ExternalEntity extends EntityDecl
{
    String    systemId;    // resolved URI (not relative)
    String    publicId;    // "-//xyz//....//en"
    String    notation;
    
    public ExternalEntity (InputEntity in) { }
    
    public InputSource getInputSource (EntityResolver r)
                       throws IOException, SAXException {

        InputSource    retval;
    
        retval = r.resolveEntity (publicId, systemId);
        // SAX sez if null is returned, use the URI directly
        if (retval == null)
            retval = Resolver.createInputSource (new URL (systemId), false);
        return retval;
    }
}
