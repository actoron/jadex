package org.activecomponents.shortmessages;

import java.util.Date;

/**
 *  Short message struct.
 */
public class ShortMessage
{
	/** The text. */
	protected String text;
	
	/** The sender. */
	protected User sender;
	
	/** The publication date. */
	protected Date date;

	/**
	 *  Create a new ShortMessage.
	 */
	public ShortMessage()
	{
	}
	
	/**
	 *  Create a new ShortMessage.
	 */
	public ShortMessage(String text, User sender)
	{
		this(text, sender, new Date());
	}
	
	/**
	 *  Create a new ShortMessage.
	 */
	public ShortMessage(String text, User sender, Date date)
	{
		this.text = text;
		this.sender = sender;
		this.date = date;
	}

	/**
	 *  Get the text.
	 *  @return the text
	 */
	public String getText()
	{
		return text;
	}

	/**
	 *  Set the text.
	 *  @param text The text to set
	 */
	public void setText(String text)
	{
		this.text = text;
	}

	/**
	 *  Get the sender.
	 *  @return the sender
	 */
	public User getSender()
	{
		return sender;
	}

	/**
	 *  Set the sender.
	 *  @param sender The sender to set
	 */
	public void setSender(User sender)
	{
		this.sender = sender;
	}

	/**
	 *  Get the date.
	 *  @return the date
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 *  Set the date.
	 *  @param date The date to set
	 */
	public void setDate(Date date)
	{
		this.date = date;
	}
	
}
