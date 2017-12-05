package jadex.micro.testcases.semiautomatic.servicevalue;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentServiceValue;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Example for @AgentServiceValue annotation. Automatically maps
 *  a subscription of a required service to fields. 
 *  
 *  // todo: method name, parameters, intervals...
 */
@Agent
@RequiredServices(@RequiredService(name="newsser", type=INewsService.class))
@ComponentTypes(@ComponentType(name="provider", clazz=NewsProviderAgent.class))
@Configurations(@Configuration(name="def", components=@Component(type="provider")))
public class NewsConsumerAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentServiceValue(name="newsser")
	protected String headline;
	
	@AgentServiceValue(name="newsser")
	protected List<String> news = new ArrayList<String>();
	
//	@AgentServiceValue(name="newsser", repeat=true, delay=1000)
//	protected List<String> news;
	
//	@AgentServiceValue(name="newsser")
//	protected void receiveNews(String news)
//	{
//		System.out.println("received: "+news);
//	}
	
	@AgentBody
	public void body()
	{
		while(true)
		{
			System.out.println("headline is: "+headline);
			System.out.println("news are: "+news);
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000).get();
		}
	}
}
