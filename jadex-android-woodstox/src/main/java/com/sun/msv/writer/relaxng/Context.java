package com.sun.msv.writer.relaxng;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.writer.XMLWriter;

public interface Context
{
    void writeNameClass( NameClass nc );
    String getTargetNamespace();
    XMLWriter getWriter();
}
