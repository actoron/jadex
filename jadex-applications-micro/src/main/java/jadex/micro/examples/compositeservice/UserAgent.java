package jadex.micro.examples.compositeservice;

import jadex.bridge.IArgument;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.ProvidedServiceInfo;
import jadex.commons.service.RequiredServiceInfo;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.micro.examples.chat.IChatService;

/**
 *  The user agent uses services.
 */
public class UserAgent extends MicroAgent
{
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		getRequiredService("addservice").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAddService addser = (IAddService)result;
				addser.add(1, 1).addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						System.out.println("add service result: "+result+" "+getComponentIdentifier().getLocalName());
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("invocation failed: "+exception);
					}
				}));
			}
		});
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent uses an add service.", null, 
			new IArgument[]{}, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.chat.ChatPanel"}),
			new RequiredServiceInfo[]{new RequiredServiceInfo("addservice", IAddService.class)}, new ProvidedServiceInfo[]{new ProvidedServiceInfo(IChatService.class)});
	}
}
