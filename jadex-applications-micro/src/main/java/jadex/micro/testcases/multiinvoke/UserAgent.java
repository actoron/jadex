package jadex.micro.testcases.multiinvoke;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;

/**
 * 
 */
@RequiredServices(@RequiredService(name="ms", type=IExampleService.class, multiple=true, binding=@Binding(dynamic=true)))
@Results(@Result(name="testcases", clazz=List.class))
@Agent
@ComponentTypes(@ComponentType(name="provider", filename="ProviderAgent.class"))
@Configurations(@Configuration(name="def", components=@Component(type="provider", number="5")))
public class UserAgent
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		IMExampleService ser = getMultiService("ms", IMExampleService.class);
//		ser.getItems().addResultListener(new IIntermediateResultListener<IIntermediateFuture<String>>()
//		{
//			public void intermediateResultAvailable(IIntermediateFuture<String> result)
//			{
//				System.out.println("ires: "+result);
//			}
//			public void finished()
//			{
//				System.out.println("fin");
//			}
//			public void resultAvailable(Collection<IIntermediateFuture<String>> result)
//			{
//				System.out.println("res: "+result);
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		});
		ser.getItems().addResultListener(new IIntermediateResultListener<String>()
		{
			public void intermediateResultAvailable(String result)
			{
				System.out.println("ires: "+result);
			}
			public void finished()
			{
				System.out.println("fin");
			}
			public void resultAvailable(Collection<String> result)
			{
				System.out.println("res: "+result);
			}
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
			}
		});
	}

	
	
	/**
	 * 
	 */
	public <T> T getMultiService(String reqname, Class<T> multitype)
	{
		return (T)Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{multitype}, new MultiServiceInvocationHandler(agent, reqname));
	}
}