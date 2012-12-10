package jadex.bridge.service.types.security;

import java.security.cert.Certificate;
import java.util.Date;

/**
 *  Bean conform info object for a key store entry.
 */
public class KeyStoreEntry
{
	//-------- attributes --------
	
	/** The entry type. */
	protected String type;
	
	/** Flag if entry is password protected. */
	protected boolean prot;
	
	/** The alias. */
	protected String alias;
	
	/** The algorithm. */
	protected String algorithm;
	
	/** The date. */
	protected long date;
	
	/** The details. */
	protected Certificate[] certificates;
	
	/** The validity from date. */
	protected long from;
	
	/** The validity to date. */
	protected long to; 

	//-------- constructors --------

	/**
	 *  Create a new KeyStroreEntry. 
	 */
	public KeyStoreEntry()
	{
	}
	
	/**
	 *  Create a new KeyStroreEntry. 
	 */
	public KeyStoreEntry(String type, String alias, long date, Certificate[] certificates, long from, long to)
	{
		this.type = type;
		this.alias = alias;
		this.date = date;
		this.certificates = certificates;
		this.from = from;
		this.to = to;
	}

	//-------- methods --------

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the alias.
	 *  @return The alias.
	 */
	public String getAlias()
	{
		return alias;
	}

	/**
	 *  Set the alias.
	 *  @param alias The alias to set.
	 */
	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	/**
	 *  Get the date.
	 *  @return The date.
	 */
	public long getDate()
	{
		return date;
	}

	/**
	 *  Set the date.
	 *  @param date The date to set.
	 */
	public void setDate(long date)
	{
		this.date = date;
	}

	/**
	 *  Get the from.
	 *  @return The from.
	 */
	public long getFrom()
	{
		return from;
	}

	/**
	 *  Set the from.
	 *  @param from The from to set.
	 */
	public void setFrom(long from)
	{
		this.from = from;
	}

	/**
	 *  Get the to.
	 *  @return The to.
	 */
	public long getTo()
	{
		return to;
	}

	/**
	 *  Set the to.
	 *  @param to The to to set.
	 */
	public void setTo(long to)
	{
		this.to = to;
	}

	/**
	 *  Get the protected state.
	 *  @return The protected state.
	 */
	public boolean isProtected()
	{
		return prot;
	}

	/**
	 *  Set the protected state.
	 *  @param prot The protected to set.
	 */
	public void setProtected(boolean prot)
	{
		this.prot = prot;
	}
	
	/**
	 *  Get the algorithm.
	 *  @return The algorithm.
	 */
	public String getAlgorithm()
	{
		return algorithm;
	}

	/**
	 *  Set the algorithm.
	 *  @param algorithm The algorithm to set.
	 */
	public void setAlgorithm(String algorithm)
	{
		this.algorithm = algorithm;
	}
	
	/**
	 *  Get the certificates.
	 *  @return The certificates.
	 */
	public Certificate[] getCertificates()
	{
		return certificates;
	}

	/**
	 *  Set the certificates.
	 *  @param certificates The certificates to set.
	 */
	public void setCertificates(Certificate[] certificates)
	{
		this.certificates = certificates;
	}

	/**
	 *  Get the protected state.
	 *  @return The protected state.
	 */
	public boolean isExpired()
	{
		boolean ret = false;
		if(from!=0 && to!=0)
		{
			Date cur = new Date();
			Date start = new Date(from);
			Date end = new Date(to);
			ret = !(cur.after(start) || cur.equals(start)) && (cur.before(end) || cur.equals(end));
		}
		return ret;
	}
}