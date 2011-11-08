package org.codehaus.stax2.evt;

import javaxx.xml.stream.events.DTD;

/**
 * Interface that extends basic {@link DTD} with methods that are
 * necessary to completely reproduce actual DOCTYPE declaration
 * constructs in xml documents.
 */
public interface DTD2
    extends DTD
{
    public String getRootName();

    public String getSystemId();

    public String getPublicId();

    public String getInternalSubset();
}
