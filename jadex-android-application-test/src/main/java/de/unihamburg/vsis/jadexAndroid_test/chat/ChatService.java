package de.unihamburg.vsis.jadexAndroid_test.chat;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.List;

/**
 *  Chat service implementation.
 */
@Service
public class ChatService implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The listeners. */
	protected List listeners;

	private String identification;
	
	/** The chat gui. */
	
	//-------- methods --------
	
	/**
	 *  Called on startup.
	 */
	@ServiceStart
	public void start()
	{
		this.listeners = new ArrayList();
		this.identification = agent.getComponentIdentifier().getName();
		MeasureActivity.chatAgent = this.agent.getExternalAccess();
	}
	
	/**
	 *  Hear something.
	 *  @param name The name.
	 *  @param text The text.
	 */
	public void hear(String name, String text)
	{
		for(int i=0; i<listeners.size(); i++)
		{
//			System.out.println("listeners: "+listeners);
			final IRemoteChangeListener lis = (IRemoteChangeListener)listeners.get(i);
			lis.changeOccurred(new ChangeEvent(name, null, text))
				.addResultListener(agent.createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					exception.printStackTrace();
					listeners.remove(lis);
				}
			}));
		}
	}
	
	/**
	 *  Add a local listener.
	 */
	public void addChangeListener(IRemoteChangeListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 *  Remove a local listener.
	 */
	public void removeChangeListener(IRemoteChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	@Override
	public String getIdentification() {
		return identification;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ChatService, "+agent.getComponentIdentifier();
	}
}
