package jadex.tools.comanalyzer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jadex.commons.SUtil;
import jadex.commons.collection.SortedList;


/**
 * This is the list of the recorded messages. This class provides methods to
 * notify other classes which are implementing the IMessageListListener
 * interface about adding, changing and removing messages.
 */
public class MessageList implements Iterable, Serializable
{
	//-------- attributes --------

	/** The list of messages */
	protected List elements;

	/** The listener for message events. */
	protected List listeners;;

	//-------- constructor --------

	/**
	 * Default constructor for the message list.
	 */
	public MessageList()
	{
		elements = new SortedList();
		listeners = new ArrayList();
	}

	//-------- MessageList methods --------

	/**
	 * Adds a message to the list.
	 * @param msg The message to add.
	 */
	public void addMessage(Message msg)
	{
		elements.add(msg);
	}

	/**
	 * @param messages The array of messages to add.
	 */
	public void addMessages(Message[] messages)
	{
		elements.addAll(SUtil.arrayToList(messages));
	}

	/**
	 * @param msg The message to remove
	 */
	public void removeMessage(Message msg)
	{
		elements.remove(msg);
	}

	/**
	 * @param messages The array of messages to remove.
	 */
	public void removeMessages(Message[] messages)
	{
		elements.removeAll(SUtil.arrayToList(messages));
	}

	/**
	 * Clear all messages in the list.
	 */
	public void removeAllMessages()
	{
		// create a copy of the messages for notifier
		Message[] messages = (Message[])elements.toArray(new Message[elements.size()]);
		elements.clear();
		fireMessagesRemoved(messages);
	}

	/**
	 * Checks if a message is contained in the message list.
	 * 
	 * @param message The message to check.
	 * @return <code>true</code> if a specific message is in the agentlist.
	 */
	public boolean containsMessage(Message message)
	{
		return elements.contains(message);
	}

	/**
	 * @return The array of messages.
	 */
	public Message[] getMessages()
	{
		return (Message[])elements.toArray(new Message[0]);
	}

	/**
	 * @return The list of agents
	 */
	public List getList()
	{
		return elements;
	}

	/**
	 * @return The size of the message list.
	 */
	public int size()
	{
		return elements.size();
	}

	/**
	 * @return The message list iterator.
	 */
	public Iterator iterator()
	{
		return elements.iterator();
	}

	/**
	 * Register for message events.
	 * @param listener A class implementing the IMessageListListener interface.
	 */
	protected void addListener(IMessageListListener ml)
	{
		if(!listeners.contains(ml))
		{
			listeners.add(ml);
			ml.messagesAdded(getMessages());
		}
	}

	/**
	 * Notifies the listeners about the adding of messages.
	 * @param messages The added messages.
	 */
	protected void fireMessagesAdded(Message[] messages)
	{
		if(messages != null)
		{
			for(Iterator iter = listeners.iterator(); iter.hasNext();)
			{
				IMessageListListener listener = (IMessageListListener)iter.next();
				listener.messagesAdded(messages);
			}
		}
	}

	/**
	 * Notifies the listeners about the removel of messages.
	 * @param messages The removed messages.
	 */
	protected void fireMessagesRemoved(Message[] messages)
	{
		for(Iterator iter = listeners.iterator(); iter.hasNext();)
		{
			IMessageListListener listener = (IMessageListListener)iter.next();
			listener.messagesRemoved(messages);
		}

	}

	/**
	 * Notifies the listeners about messages with changed visibility.
	 * @param messages The changed messages.
	 */
	protected void fireMessagesChanged(Message[] messages)
	{
		for(Iterator iter = listeners.iterator(); iter.hasNext();)
		{
			IMessageListListener listener = (IMessageListListener)iter.next();
			listener.messagesChanged(messages);
		}

	}

}
