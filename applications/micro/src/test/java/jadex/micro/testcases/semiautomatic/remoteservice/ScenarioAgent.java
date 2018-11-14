package jadex.micro.testcases.semiautomatic.remoteservice;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Just for starting the scenario.
 */
@Agent
public class ScenarioAgent
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Execute the body.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
//		ILibraryService ls = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM));
//		System.out.println("ls:"+ls);
		
		IFuture<ILibraryService> fut = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM));
//		IFuture<ILibraryService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(ILibraryService.class);
		fut.addResultListener(new DefaultResultListener<ILibraryService>()
		{
			public void resultAvailable(ILibraryService libservice)
			{
//				libservice.getURLStrings().addResultListener(createResultListener(new DefaultResultListener()
				libservice.getAllResourceIdentifiers().addResultListener(agent.getFeature(IExecutionFeature.class)
					.createResultListener(new DefaultResultListener<List<IResourceIdentifier>>()
				{
					public void resultAvailable(List<IResourceIdentifier> result)
					{
						List<IResourceIdentifier> libs = (List<IResourceIdentifier>)result;
						List<String> libpaths = new ArrayList<String>();
						for(IResourceIdentifier rid: libs)
						{
							String lib	= rid.getLocalIdentifier().getUri().toString();
							if(lib.startsWith("file:"))	// Hack!!! excludes systemcprid.
							{
								libpaths.add(lib);
							}
						}
						StartScenario.startScenario(libpaths.toArray(new String[libpaths.size()])).addResultListener(
							agent.getFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener<IExternalAccess[]>()
						{
							public void resultAvailable(IExternalAccess[] platforms)
							{
								System.out.println("Killing platforms");
								for(int i=0; i<platforms.length; i++)
									platforms[i].killComponent();
							}
						}));
					}
				}));
			}
			
//			public void exceptionOccurred(Exception exception)
//			{
//				super.exceptionOccurred(exception);
//			}
		});
		
		return new Future<Void>(); // never kill?!
	}
}
