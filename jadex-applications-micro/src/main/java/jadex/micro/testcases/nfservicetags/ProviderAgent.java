package jadex.micro.testcases.nfservicetags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.SNFPropertyProvider;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


@Agent
@Arguments(@Argument(name="tag", clazz=String.class, defaultvalue="new String[]{\"mytag1\",\"mytag2\"}"))
@ProvidedServices(@ProvidedService(type=ITestService.class))
@Service
public class ProviderAgent implements ITestService
{
	@Agent
	protected IInternalAccess agent;
	
	public IFuture<Void> method(String msg)
	{
		return IFuture.DONE;
	}
	
	@AgentBody
	public void body()
	{
		final List<String> tags = new ArrayList<String>();
		tags.add("mytag1");
		tags.add("mytag2");
//		tags.add("mytag3");
		
		ITestService ts = SServiceProvider.getTaggedService(agent, ITestService.class, RequiredServiceInfo.SCOPE_PLATFORM, "mytag1", "mytag2").get(); 
		
		System.out.println("Found: "+ts);
		
//		fut.addIntermediateResultListener(new IIntermediateResultListener<ITestService>()
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//			
//			public void resultAvailable(Collection<ITestService> result)
//			{
//				System.out.println("ra: "+result);
//			}
//			
//			public void intermediateResultAvailable(ITestService result)
//			{
//				System.out.println("found ires: "+result);
//			}
//			
//			public void finished()
//			{
//				System.out.println("fini");
//			}
//		});
	}
}
