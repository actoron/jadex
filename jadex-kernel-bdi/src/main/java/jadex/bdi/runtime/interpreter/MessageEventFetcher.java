package jadex.bdi.runtime.interpreter;

import jadex.bridge.IMessageAdapter;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

/**
 *  Fetcher for raw messages.
 */
public class MessageEventFetcher extends OAVBDIFetcher
{
	//-------- attributes --------
	
	/** The raw message. */
	protected IMessageAdapter rawmsg;
	
	//-------- constructors --------
	
	/**
	 *  Create a new fetcher.
	 */
	public MessageEventFetcher(IOAVState state, Object rcapa, IMessageAdapter rawmsg)
	{
		super(state, rcapa);
		this.rawmsg = rawmsg;
	}
	
	//-------- IValueFetcher methods --------
	
	/**
	 *  Fetch a value via its name.
	 *  @param name The name.
	 *  @return The value.
	 */
	public Object fetchValue(String name)
	{
		Object ret = null;
		
		if(name==null)
			throw new RuntimeException("Name must not be null.");
		
		String shortname = name;
		if(name.startsWith("$"))
			shortname = name.substring(1);
		
		if(isMessageParameter(rawmsg.getMessageType(), shortname))
			ret = MessageEventRules.getValue(rawmsg, shortname, rcapa);
		else if(name.equals("$message"))
			ret = rawmsg;
		else
			ret = super.fetchValue(name);
		
		return ret;
	}
	
	//-------- additional methods --------
	
	/**
	 *  Test if a name is a message parameter.
	 *  @param mt The message template.
	 *  @param name The parameter name.
	 */
	protected boolean isMessageParameter(MessageType mt, String name)
	{
		// todo: index for speed?!
		String[] pnames = mt.getParameterNames();
		String[] psnames = mt.getParameterSetNames();
		return SUtil.arrayContains(pnames, name) || SUtil.arrayContains(psnames, name);
	}
}
