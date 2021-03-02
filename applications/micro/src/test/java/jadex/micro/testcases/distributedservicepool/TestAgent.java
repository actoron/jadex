package jadex.micro.testcases.distributedservicepool;

import java.util.Collection;

import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Imports;


@Imports(
{
	"jadex.platform.service.distributedservicepool.*",
	"jadex.bridge.service.*",
	"jadex.bridge.service.search.*"
})
@ComponentTypes(
{
	@ComponentType(name="Worker", clazz=WorkerAgent.class),
	@ComponentType(name="DistributedPool", filename = "jadex/platform/service/distributedservicepool/DistributedServicePoolAgent.class")
})
@Configurations(
{
	@Configuration(name="pool", components={
		@Component(type="Worker"),
		@Component(type="DistributedPool", arguments = 
		{
			@NameValue(name="serviceinfo",
				value="new ServiceQuery(ITestService.class).setScope(ServiceScope.GLOBAL)")
		}),
	})
})
@Agent
public class TestAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@OnStart
	public void start()
	{
		Collection<ITestService> sers = agent.searchLocalServices(new ServiceQuery<ITestService>(ITestService.class));
		
		// todo: how to identify pool or worker (tagging workers or tagging pools)
		ITestService ser = null;
		for(ITestService s: sers)
		{
			if(((IService)s).getServiceId().toString().indexOf("Pool")!=-1)
			{
				ser = s;
				break;
			}
		}
		
		ser.ok().get();
		
		for(int i=0; i<3; i++)
		{
			try
			{
				ser.ex().get();
			}
			catch(Exception e)
			{
				System.out.println("got exception: "+e);
			}
		}
		
		while(true)
		{
			try
			{
				ser.ok().get();
			}
			catch(Exception e)
			{
				System.out.println("ok call got exception: "+e);
			}
			agent.waitForDelay(10000).get();
		}
	}
	
	
	public static void main(String[] args)
	{
		//new RequiredServiceInfo(ITestService.class).setDefaultBinding(new RequiredServiceBinding(ServiceScope.APPLICATION_GLOBAL));
	
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefault()).get();
		CreationInfo ci = new CreationInfo().setFilenameClass(TestAgent.class);
		platform.createComponent(ci).get();
	}
	
	@OnEnd
	public void end(Exception e)
	{
		e.printStackTrace();
		System.out.println("end");
	}
}
