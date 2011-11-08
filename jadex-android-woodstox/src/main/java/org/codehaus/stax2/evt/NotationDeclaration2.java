package org.codehaus.stax2.evt;

import javaxx.xml.stream.events.NotationDeclaration;

/**
 * Interface that extends basic {@link NotationDeclaration} to add
 * support for handling Base URI needed to resolve Notation references.
 * This 
 */
public interface NotationDeclaration2
    extends NotationDeclaration
{
    public String getBaseURI();
}
