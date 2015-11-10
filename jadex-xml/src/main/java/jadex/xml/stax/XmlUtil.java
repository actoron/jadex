package jadex.xml.stax;

/**
 *  Class holding stax-compatible constants and
 *  utility functions.
 *
 */
public class XmlUtil
{
	/**
	   * Indicates an event is a start element.
	   */
	  public static final int START_ELEMENT = 1; // = javax.xml.stream.XMLStreamConstants.START_ELEMENT
	  
	  /**
	   * Indicates an event is an end element.
	   */
	  public static final int END_ELEMENT = 2; // = javax.xml.stream.XMLStreamConstants.END_ELEMENT
	  
	  /**
	   * Indicates an event is characters.
	   */
	  public static final int CHARACTERS = 4; // = javax.xml.stream.XMLStreamConstants.CHARACTERS

	  public static final int COMMENT = 5; // = javax.xml.stream.XMLStreamConstants.COMMENT

	  public static final int CDATA = 12; // = javax.xml.stream.XMLStreamConstants.CDATA

	/**
		 *  Unescapes strings for xml.
		 */
		public static final String unescapeString(String string)
		{
			StringBuilder ret = new StringBuilder();
			boolean escapemode = false;
			for (int i = 0; i < string.length(); ++i)
			{
				String c = string.substring(i, i + 1);
				if (escapemode)
				{
					if (c.equals("n"))
					{
						ret.append("\n");
					}
					else
					{
						ret.append(c);
					}
					escapemode = false;
				}
				else
				{
					if (c.equals("\\"))
					{
						escapemode = true;
					}
					else
					{
						ret.append(c);
					}
				}
			}
			return ret.toString();
		}
}
