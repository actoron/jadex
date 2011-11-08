package org.codehaus.stax2.evt;

import javaxx.xml.stream.XMLEventFactory;

/**
 * Interface that adds missing (but required) methods to
 * {@link XMLEventFactory}; especially ones for creating actual
 * well-behaving DOCTYPE events.
 */
public abstract class XMLEventFactory2
    extends XMLEventFactory
{
    protected XMLEventFactory2() {
        super();
    }

    public abstract DTD2 createDTD(String rootName, String sysId, String pubId,
                                   String intSubset);

    public abstract DTD2 createDTD(String rootName, String sysId, String pubId,
                                  String intSubset, Object processedDTD);
}

