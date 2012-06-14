package jadex.micro.testcases;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Agent
@RequiredServices(
{
	@RequiredService(name="msgservice", type=IMessageService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cms", type=IComponentManagementService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
//@ComponentTypes(
//	@ComponentType(name="receiver", filename="jadex/micro/testcases/stream/ReceiverAgent.class")
//)
@Arguments(
{
	@Argument(name="testcnt", clazz=int.class, defaultvalue="2")
})
@Results(@Result(name="testresults", clazz=Testcase.class))
public abstract class TestAgent
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		final Testcase tc = new Testcase();
		tc.setTestCount(((Integer)agent.getArgument("testcnt")).intValue());
		
		performTests(tc).addResultListener(agent.createResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				System.out.println("tests finished");

				agent.setResultValue("testresults", tc);
				ret.setResult(null);
//				agent.killAgent();				
			}
			public void exceptionOccurred(Exception exception)
			{
				agent.setResultValue("testresults", tc);
				ret.setResult(null);
//				agent.killAgent();	
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected abstract IFuture<Void> performTests(Testcase tc);
	
	/**
	 * 
	 */
	protected IFuture<IExternalAccess> createPlatform(String[] args)
	{
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		// Start platform
		String url	= "new String[]{\"../jadex-applications-micro/target/classes\"}";	// Todo: support RID for all loaded models.
//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
//		Starter.createPlatform(new String[]{"-platformname", "testi_1", "-libpath", url,
		String[] defargs = new String[]{"-libpath", url, "-platformname", agent.getComponentIdentifier().getPlatformPrefix()+"_*",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
//				"-logging_level", "java.util.logging.Level.INFO",
//				"-gui", "false", "-usepass", "false", "-simulation", "false"
			"-gui", "false", "-simulation", "false", "-printpass", "false"};
		
		if(args!=null && args.length>0)
		{
			Map<String, String> argsmap = new HashMap<String, String>();
			for(int i=0; i<defargs.length; i++)
			{
				argsmap.put(defargs[i], defargs[++i]);
			}
			for(int i=0; i<args.length; i++)
			{
				argsmap.put(args[i], args[++i]);
			}
			defargs = new String[argsmap.size()*2];
			int i=0;
			for(String key: argsmap.keySet())
			{
				defargs[i*2]= key; 
				defargs[i*2+1] = argsmap.get(key);
				i++;
			}
		}

		System.out.println("platform args: "+SUtil.arrayToString(defargs));
		
		Starter.createPlatform(defargs).addResultListener(agent.createResultListener(
			new DelegationResultListener<IExternalAccess>(ret)));
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<IComponentIdentifier> createComponent(IServiceProvider provider, final String filename, 
		final IComponentIdentifier root, final IResultListener<Collection<Tuple2<String,Object>>> reslis)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		IFuture<IComponentManagementService> cmsfut = SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		cmsfut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
//				final Future<Collection<Tuple2<String, Object>>> resfut = new Future<Collection<Tuple2<String, Object>>>();
//				IResultListener<Collection<Tuple2<String, Object>>> reslis = new DelegationResultListener<Collection<Tuple2<String,Object>>>(resfut);
				
				// "receiver" cannot use parent due to remote case new CreationInfo(agent.getComponentIdentifier())
				IResourceIdentifier	rid	= new ResourceIdentifier(
					new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUrl()), null);
				cms.createComponent(null, filename, new CreationInfo(rid), reslis)
					.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Map<String, Object>> destroyComponent(final IComponentIdentifier cid)
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		IFuture<IComponentManagementService> cmsfut = SServiceProvider.getService(agent.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		cmsfut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				cms.destroyComponent(cid).addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
			}
		});
		
		return ret;
	}
	
}
