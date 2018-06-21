package jadex.bridge.service.types.email;

import java.util.Arrays;

/**
 * 
 */
public class Email
{
	/** The plain content. */
	protected String content; 
	
	/** The content type. */
	protected String contenttype;

	/** The subject. */
	protected String subject;
	
	/** The sender. */
	protected String sender;
	
	/** The receivers. */
	protected String[] receivers;
	
	/** The ccs. */
	protected String[] ccs;
	
	/** The bccs. */
	protected String[] bccs;
	
	/** The attachments. */
	protected Object[] attachments;

	/**
	 *  Create an email.
	 */
	public Email()
	{
	}
	
	/**
	 *  Create an email.
	 */
	public Email(String sender, String content, String subject, String receiver)
	{
		this(sender, content, "text/plain", subject, new String[]{receiver}, null, null);
	}
	
	/**
	 *  Create an email.
	 */
	public Email(String sender, String content, String subject, String[] receivers)
	{
		this(sender, content, "text/plain", subject, receivers, null, null);
	}
	
	/**
	 *  Create an email.
	 */
	public Email(String sender, String content, String contenttype, String subject, String[] receivers,
		String[] ccs, String[] bccs)
	{
		this.sender = sender;
		this.content = content;
		this.contenttype = contenttype==null? "text/plain": contenttype;
		this.subject = subject;
		this.receivers = receivers;
		this.ccs = ccs;
		this.bccs = bccs;
	}

	/**
	 *  Get the content.
	 *  @return The content.
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 *  Set the content.
	 *  @param content The content to set.
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 *  Get the subject.
	 *  @return The subject.
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 *  Set the subject.
	 *  @param subject The subject to set.
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 *  Get the receivers.
	 *  @return The receivers.
	 */
	public String[] getReceivers()
	{
		return receivers;
	}

	/**
	 *  Set the receivers.
	 *  @param receivers The receivers to set.
	 */
	public void setReceivers(String[] receivers)
	{
		this.receivers = receivers;
	}

	/**
	 *  Get the ccs.
	 *  @return The ccs.
	 */
	public String[] getCcs()
	{
		return ccs;
	}

	/**
	 *  Set the ccs.
	 *  @param ccs The ccs to set.
	 */
	public void setCcs(String[] ccs)
	{
		this.ccs = ccs;
	}

	/**
	 *  Get the bccs.
	 *  @return The bccs.
	 */
	public String[] getBccs()
	{
		return bccs;
	}

	/**
	 *  Set the bccs.
	 *  @param bccs The bccs to set.
	 */
	public void setBccs(String[] bccs)
	{
		this.bccs = bccs;
	}

	/**
	 *  Get the sender.
	 *  @return The sender.
	 */
	public String getSender()
	{
		return sender;
	}

	/**
	 *  Set the sender.
	 *  @param sender The sender to set.
	 */
	public void setSender(String sender)
	{
		this.sender = sender;
	}

	/**
	 *  Get the attachments.
	 *  @return The attachments.
	 */
	public Object[] getAttachments()
	{
		return attachments;
	}

	/**
	 *  Set the attachments.
	 *  @param attachments The attachments to set.
	 */
	public void setAttachments(Object[] attachments)
	{
		this.attachments = attachments;
	}
	
	/**
	 *  Get the contenttype.
	 *  return The contenttype.
	 */
	public String getContentType()
	{
		return contenttype;
	}

	/**
	 *  Set the contenttype. 
	 *  @param contenttype The contenttype to set.
	 */
	public void setContentType(String contenttype)
	{
		this.contenttype = contenttype;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(attachments);
		result = prime * result + Arrays.hashCode(bccs);
		result = prime * result + Arrays.hashCode(ccs);
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((contenttype == null) ? 0 : contenttype.hashCode());
		result = prime * result + Arrays.hashCode(receivers);
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	/**
	 *  Test if is equal.
	 */
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Email other = (Email)obj;
		if(!Arrays.equals(attachments, other.attachments))
			return false;
		if(!Arrays.equals(bccs, other.bccs))
			return false;
		if(!Arrays.equals(ccs, other.ccs))
			return false;
		if(content == null)
		{
			if(other.content != null)
				return false;
		}
		else if(!content.equals(other.content))
			return false;
		if(contenttype == null)
		{
			if(other.contenttype != null)
				return false;
		}
		else if(!contenttype.equals(other.contenttype))
			return false;
		if(!Arrays.equals(receivers, other.receivers))
			return false;
		if(sender == null)
		{
			if(other.sender != null)
				return false;
		}
		else if(!sender.equals(other.sender))
			return false;
		if(subject == null)
		{
			if(other.subject != null)
				return false;
		}
		else if(!subject.equals(other.subject))
			return false;
		return true;
	}

	
//	/**
//	 *  Get the string representation.
//	 */
//	public String toString()
//	{
//		return "Email(content=" + content + ", subject=" + subject
//			+ ", sender=" + sender + ", receivers="
//			+ Arrays.toString(receivers) + ", ccs=" + Arrays.toString(ccs)
//			+ ", bccs=" + Arrays.toString(bccs) + ")";
//	}
	
	
	
	
}
