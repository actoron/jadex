package com.bea.xml.stream.events;

import javaxx.xml.stream.events.NotationDeclaration;
import javaxx.xml.stream.events.XMLEvent;

/**
 * Simple implementation of {@link NotationDeclaration}.
 *
 * @author Tatu Saloranta
 */
public class NotationDeclarationEvent 
  extends BaseEvent 
  implements NotationDeclaration
{
  protected final String name;

  protected final String publicId;
  protected final String systemId;

    public NotationDeclarationEvent(String name, String publicId, String systemId)
    {
        super(XMLEvent.NOTATION_DECLARATION);
        this.name = name;
        this.publicId = publicId;
        this.systemId = systemId;
    }

  public String getName() {
    return name;
  }

  public String getPublicId() {
    return publicId;
  }

  public String getSystemId() {
    return systemId;
  }

  protected void doWriteAsEncodedUnicode(java.io.Writer writer) 
      throws java.io.IOException
  {
      writer.write("<!NOTATION ");
      writer.write(getName());
      if (publicId != null) {
          writer.write(" PUBLIC \"");
          writer.write(publicId);
          writer.write('"');
      } else if (systemId != null) {
          writer.write(" SYSTEM");
      }
      if (systemId != null) {
          writer.write(" \"");
          writer.write(systemId);
          writer.write('"');
      }
      writer.write('>');
  }
}
