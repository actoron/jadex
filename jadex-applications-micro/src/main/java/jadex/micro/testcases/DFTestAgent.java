package jadex.micro.testcases;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFComponentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.IResultListener;
import jadex.microkernel.MicroAgent;

import java.util.HashMap;
import java.util.Map;

/**
 *  Test DF usage from micro agent.
 *  @author Dirk
 */
public class DFTestAgent extends MicroAgent
{
	/**
	 *  At startup register the agent at the DF.
	 */
	public void executeBody()
	{
		registerDF();
	}
	
	/**
	 *  Register the agent at the DF.
	 */
	protected void registerDF()
	{
		IDF df = (IDF)getServiceContainer().getService(IDF.class);
		IDFServiceDescription sd = df.createDFServiceDescription(null, "testType", null);
		IDFComponentDescription ad = df.createDFComponentDescription(getComponentIdentifier(), sd);

		df.register(ad, createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				System.out.println("DF registration successful. Starting search...");
				searchDF();
			}
			
			public void exceptionOccurred(Object source, Exception e)
			{
				e.printStackTrace();
			}
		}));
	}
	
	/**
	 *  Search for the agent at the DF.
	 */
	protected  void searchDF()
	{
		// Create a service description to search for.
		IDF df = (IDF)getServiceContainer().getService(IDF.class);
		IDFServiceDescription sd = df.createDFServiceDescription(null, "testType", null);
		IDFComponentDescription ad = df.createDFComponentDescription(null, sd);
		ISearchConstraints	cons = df.createSearchConstraints(-1, 0);
		
		df.search(ad, cons, createResultListener(new IResultListener() {
			
			public void resultAvailable(Object sourcem, Object result)
			{
				IDFComponentDescription[] agentDesc = (IDFComponentDescription[])result;
				if(agentDesc.length != 0)
				{
					System.out.println("DF search successful. Sending message...");
					IComponentIdentifier receiver = agentDesc[0].getName();
					sendMessageToMyself(receiver);
				}
			}
			
			public void exceptionOccurred(Object source, Exception e)
			{
				e.printStackTrace();
			}
		}));
	}
	
	private void sendMessageToMyself(IComponentIdentifier myself)
	{
		Map hlefMessage = new HashMap();
		hlefMessage.put(SFipa.PERFORMATIVE, SFipa.INFORM);
		hlefMessage.put(SFipa.LANGUAGE, SFipa.NUGGETS_XML);
		hlefMessage.put(SFipa.SENDER, getComponentIdentifier());
		hlefMessage.put(SFipa.RECEIVERS, myself);
		hlefMessage.put(SFipa.CONTENT, "");
		
		sendMessage(hlefMessage, SFipa.FIPA_MESSAGE_TYPE);
	}
	
	public void messageArrived(Map msg, MessageType mt)
	{
		System.out.println("Message received. Test case succeeded!");
	}
}
