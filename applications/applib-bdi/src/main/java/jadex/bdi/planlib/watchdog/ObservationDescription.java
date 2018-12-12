package jadex.bdi.planlib.watchdog;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *  The observation description
 */
public class ObservationDescription
{
	//-------- attributes --------

	/** The identifier of the component to observe. */
	protected IComponentIdentifier cid;

	/** The delay between pings. */
	protected long pingdelay;

	/** The contact data. */
	protected ContactData[] contacts;

	//-------- constructors --------

	/**
	 *  Create a new description.
	 */
	public ObservationDescription()
	{
	}

	/**
	 *  Create a new description.
	 */
	public ObservationDescription(IComponentIdentifier cid, long pingdelay, ContactData[] contact)
	{
		this.cid = cid;
		this.pingdelay = pingdelay;
		this.contacts = (ContactData[])contact.clone();
	}

	//-------- methods --------

	/**
	 *  Get the component id.
	 *  @return The component id;
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}

	/**
	 *  Set the component id.
	 *  @param cid The component id;
	 */
	public void setComponentIdentifier(IComponentIdentifier cid)
	{
		this.cid = cid;
	}

	/**
	 *  Get the ping delay.
	 *  @return The ping delay.
	 */
	public long getPingDelay()
	{
		return pingdelay;
	}

	/**
	 *  Set the ping delay.
	 *  @param pingdelay The ping delay.
	 */
	public void setPingDelay(long pingdelay)
	{
		this.pingdelay = pingdelay;
	}

	/**
	 *  Get the contact data.
	 *  @return The contact data.
	 */
	public ContactData[] getContacts()
	{
		return contacts;
	}

	/**
	 *  Set the contact data.
	 *  @param contacts The contact data.
	 */
	public void setContacts(ContactData[] contacts)
	{
		this.contacts = (ContactData[])contacts.clone();
	}

	/**
	 * Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommended that all subclasses override this method.
	 * <p>
	 * The <code>toString</code> method for class <code>Object</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object. In other words, this method returns a string equal to the
	 * value of:
	 * <blockquote>
	 * <pre>
	 * getClass().getName() + '@' + Integer.toHexString(hashCode())
	 * </pre></blockquote>
	 *
	 * @return a string representation of the object.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getClass());
		sb.append("(");
		sb.append(cid);
		sb.append(", ");
		sb.append(pingdelay);
		sb.append(", ");
		sb.append(SUtil.arrayToString(contacts));
		sb.append(")");
		return sb.toString();
	}
}
