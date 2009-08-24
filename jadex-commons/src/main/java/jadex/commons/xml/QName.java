package jadex.commons.xml;

import java.io.Serializable;

/**
 * Copy of javax.xml.namespace.QName as it is Java 1.5.
 */
public class QName implements Serializable
{
	//-------- attributes --------
	
	/** Namespace URI of this QName. */
	protected String	namespaceuri;

	/** Local part of this QName. */
	protected String	localpart;

	/** Prefix of this QNAme. */
	protected String	prefix;

	//-------- constructors --------
	
	/**
	 * Create a new qname.
	 */
	public QName(String namespaceuri, String localpart)
	{
		this(namespaceuri, localpart, SXML.DEFAULT_NS_PREFIX);
	}

	/**
	 * Create a new qname.
	 */
	public QName(String namespaceuri, String localpart, String prefix)
	{
		// map null Namespace URI to default
		// to preserve compatibility with QName 1.0
		this.namespaceuri = namespaceuri == null ? SXML.NULL_NS_URI
			: namespaceuri;

		// local part is required.
		// "" is allowed to preserve compatibility with QName 1.0
		if(localpart == null)
			throw new IllegalArgumentException(
				"local part cannot be \"null\" when creating a QName");
		this.localpart = localpart;

		// prefix is required
		if(prefix == null)
			throw new IllegalArgumentException(
				"prefix cannot be \"null\" when creating a QName");
		this.prefix = prefix;
	}

	/**
	 * Create a new qname.
	 */
	public QName(String localpart)
	{
		this(SXML.NULL_NS_URI, localpart,
			SXML.DEFAULT_NS_PREFIX);
	}

	//-------- methods --------
	
	/**
	 * Get the Namespace URI of this QName
	 * @return Namespace URI of this QName.
	 */
	public String getNamespaceURI()
	{
		return namespaceuri;
	}

	/**
	 * Get the local part of this QName.
	 * @return local part of this QName.
	 */
	public String getLocalPart()
	{
		return localpart;
	}

	/**
	 * Get the prefix of this QName.
	 */
	public String getPrefix()
	{
		return prefix;
	}

	/**
	 *  Test of equality.
	 */
	public final boolean equals(Object objectToTest)
	{
		if(!(objectToTest instanceof QName))
			return false;
		QName qname = (QName)objectToTest;

		return namespaceuri.equals(qname.namespaceuri)
				&& localpart.equals(qname.localpart);
	}

	/**
	 *  Generate the hash code for this <code>QName</code>.
	 */
	public final int hashCode()
	{
		return namespaceuri.hashCode() ^ localpart.hashCode();
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return namespaceuri.equals(SXML.NULL_NS_URI) ? localpart : "{"
			+ namespaceuri + "}" + localpart;
	}

	/**
	 * QName derived from parsing the formatted String.
	 */
	public static QName valueOf(String qnameasstring)
	{
		// null is not valid
		if(qnameasstring == null)
		{
			throw new IllegalArgumentException(
					"cannot create QName from \"null\" or \"\" String");
		}

		// "" local part is valid to preserve compatible behavior with QName 1.0
		if(qnameasstring.length() == 0)
		{
			return new QName(SXML.NULL_NS_URI, qnameasstring,
					SXML.DEFAULT_NS_PREFIX);
		}

		// local part only?
		if(qnameasstring.charAt(0) != '{')
		{
			return new QName(SXML.NULL_NS_URI, qnameasstring,
					SXML.DEFAULT_NS_PREFIX);
		}

		// Namespace URI improperly specified?
		if(qnameasstring.startsWith("{" + SXML.NULL_NS_URI + "}"))
		{
			throw new IllegalArgumentException(
					"Namespace URI .equals(SXML.NULL_NS_URI), "
							+ ".equals(\""
							+ SXML.NULL_NS_URI
							+ "\"), "
							+ "only the local part, "
							+ "\""
							+ qnameasstring
									.substring(2 + SXML.NULL_NS_URI
											.length()) + "\", "
							+ "should be provided.");
		}

		// Namespace URI and local part specified
		int endOfNamespaceURI = qnameasstring.indexOf('}');
		if(endOfNamespaceURI == -1)
		{
			throw new IllegalArgumentException("cannot create QName from \""
					+ qnameasstring + "\", missing closing \"}\"");
		}
		return new QName(qnameasstring.substring(1, endOfNamespaceURI),
				qnameasstring.substring(endOfNamespaceURI + 1),
				SXML.DEFAULT_NS_PREFIX);
	}
}
