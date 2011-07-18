package com.bea.xml.stream.events;

import javaxx.xml.stream.events.EntityDeclaration;
import javaxx.xml.stream.events.XMLEvent;

/**
 * Simple implementation of {@link EntityDeclaration}. Since no external
 * or unparsed entities are supported (yet?), this is quite simplistic
 * implementation.
 *
 * @author Tatu Saloranta
 */
public class EntityDeclarationEvent 
  extends BaseEvent 
  implements EntityDeclaration
{
  protected final String name;
  protected final String replacementText;

    public EntityDeclarationEvent(String name, String replText)
    {
        super(XMLEvent.ENTITY_DECLARATION);
        this.name = name;
        replacementText = replText;
    }

  public String getReplacementText() {
      return replacementText;
  }
  public String getName() {
    return name;
  }

  public String getBaseURI() {
    return null;
  }
  public String getPublicId() {
    return null;
  }
  public String getSystemId() {
    return null;
  }
  public String getNotationName() {
      return null;
  }

  protected void doWriteAsEncodedUnicode(java.io.Writer writer) 
      throws java.io.IOException
  {
      writer.write("<!ENTITY ");
      writer.write(getName());
      writer.write('"');
      // !!! should escape quotes, lt and amps in there
      writer.write(getReplacementText());
      writer.write("\">");
  }
}
