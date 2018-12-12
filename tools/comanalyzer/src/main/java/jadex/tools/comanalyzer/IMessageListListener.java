package jadex.tools.comanalyzer;

/**
 * Interface for tooltabs to be informed about messagelist changes.
 */
public interface IMessageListListener
{

	/**
	 * @param messages The messages to add.
	 */
	void messagesAdded(Message[] messages);

	/**
	 * @param messages The messages to removed.
	 */
	void messagesRemoved(Message[] messages);

	/**
	 * @param messages The messages that have changed.
	 */
	void messagesChanged(Message[] messages);
}
