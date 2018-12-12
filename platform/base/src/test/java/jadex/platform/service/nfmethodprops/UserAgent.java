package jadex.platform.service.nfmethodprops;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.sensor.service.ExecutionTimeProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.DefaultPoolStrategy;
import jadex.commons.MethodInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.platform.service.servicepool.IServicePoolService;

/**
 *
 */
@Agent
@Service
@RequiredServices(
{
	@RequiredService(name="poolser", type=IServicePoolService.class),
	@RequiredService(name="testser", type=ITestService.class)
})
@ComponentTypes(@ComponentType(name="spa", filename="jadex.platform.service.servicepool.ServicePoolAgent.class"))
@Configurations(@Configuration(name="default", components=@Component(type="spa")))
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
public class UserAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
//	/**
//	 *  The agent body. 
//	 */
//	@AgentBody
//	public void body()
//	{
//		registerServices().get();
//		
//		ITestService ser = (ITestService)agent.getComponentFeature(IRequiredServicesFeature.class).getService("testser").get();
//		
//		for(int i=0; i<100; i++)
//		{
//			ser.methodA(500).get();
//			
//			ser.methodB(1000).get();
//		}
//	}
	
	/**
	 *  The agent body. 
	 */
	@AgentBody
	public void body()
	{
		registerServices().get();
		
		ITestService ser = (ITestService)agent.getFeature(IRequiredServicesFeature.class).getService("testser").get();
		
		final List<TestReport> results = new ArrayList<TestReport>();
		final long wa = 500;
		final long wb = 1000;
		
		for(int i=0; i<5; i++)
		{
			ser.methodA(wa).get();
			ser.methodB(wb).get();
		}
		
		try
		{
			TestReport tr1 = new TestReport("#1", "Test if wait time of method a is ok");
			results.add(tr1);
			Method ma = ser.getClass().getMethod("methodA", new Class[]{long.class});
			
//			INFMixedPropertyProvider prov = ((INFMixedPropertyProvider)((IService)ser).getExternalComponentFeature(INFPropertyComponentFeature.class));
			double w = ((Long)agent.getMethodNFPropertyValue(((IService)ser).getServiceId(), new MethodInfo(ma), ExecutionTimeProperty.NAME).get()).doubleValue();
//			double w = ((Long)((IService)ser).getMethodNFPropertyValue(new MethodInfo(ma), ExecutionTimeProperty.NAME).get()).doubleValue();
			double d = Math.abs(w-wa)/wa;
			if(d<0.15)
			{
				tr1.setSucceeded(true);
			}
			else
			{
				tr1.setReason("Value differs more than 15 percent: "+d+" "+w+" "+wa);
			}
			
			TestReport tr2 = new TestReport("#2", "Test if wait time of method b is ok");
			results.add(tr2);
			Method mb = ser.getClass().getMethod("methodB", new Class[]{long.class});
//			prov = ((INFMixedPropertyProvider)((IService)ser).getExternalComponentFeature(INFPropertyComponentFeature.class));
			w = ((Long)agent.getMethodNFPropertyValue(((IService)ser).getServiceId(), new MethodInfo(mb), ExecutionTimeProperty.NAME).get()).doubleValue();
//			w = ((Long)((IService)ser).getMethodNFPropertyValue(new MethodInfo(mb), ExecutionTimeProperty.NAME).get()).doubleValue();
			d = Math.abs(w-wb)/wb;
			if(d<0.15)
			{
				tr2.setSucceeded(true);
			}
			else
			{
				tr2.setReason("Value differs more than 15 percent: "+d+" "+w+" "+wb);
			}
			
			TestReport tr3 = new TestReport("#3", "Test if wait time of service is ok");
			results.add(tr3);
//			prov = ((INFMixedPropertyProvider)((IService)ser).getExternalComponentFeature(INFPropertyComponentFeature.class));
			w = ((Long)agent.getNFPropertyValue(((IService)ser).getServiceId(), ExecutionTimeProperty.NAME).get()).doubleValue();
//			w = ((Long)((IService)ser).getNFPropertyValue(ExecutionTimeProperty.NAME).get()).doubleValue();
			long wab = (wa+wb)/2;
			d = Math.abs(w-wab)/wab;
			if(d<0.15)
			{
				tr3.setSucceeded(true);
			}
			else
			{
				tr3.setReason("Value differs more than 15 percent: "+d+" "+w+" "+wab);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), 
			(TestReport[])results.toArray(new TestReport[results.size()])));
		agent.killComponent();
	}
	
	/**
	 *  Register services at the service pool service.
	 */
	public IFuture<Void> registerServices()
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IServicePoolService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("poolser");
		fut.addResultListener(new ExceptionDelegationResultListener<IServicePoolService, Void>(ret)
		{
			public void customResultAvailable(final IServicePoolService sps)
			{
				sps.addServiceType(ITestService.class, new DefaultPoolStrategy(5, 35000, 10), ProviderAgent.class.getName()+".class")
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
			
		return ret;
	}
}

