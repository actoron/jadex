package com.sun.msv.reader;

import org.w3c.dom.Element;

/**
 * A GrammarReaderController2 may already have a DOM element for an imported schema. In that case,
 * we do not want to force the caller to serialize to bytes so that we can parse them again.
 * If the LSInput object implements this interface, it can return the Element.
 */
public interface DOMLSInput {
    Element getElement();
}
