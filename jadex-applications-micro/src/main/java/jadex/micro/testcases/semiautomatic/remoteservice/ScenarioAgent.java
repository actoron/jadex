package jadex.micro.testcases.semiautomatic.remoteservice;

import java.util.List;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
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
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ILibraryService libservice = (ILibraryService)result;
//				libservice.getURLStrings().addResultListener(createResultListener(new DefaultResultListener()
				libservice.getAllResourceIdentifiers().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						List<IResourceIdentifier> libs = (List<IResourceIdentifier>)result;
						String[] libpaths = new String[libs.size()];
						for(int i=0; i<libpaths.length; i++)
						{
							libpaths[i] = libs.get(i).getLocalIdentifier().getUri().toString();
						}
//						String[] libpaths = (String[])((List)result).toArray(new String[0]);
						StartScenario.startScenario(libpaths).addResultListener(
							agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								System.out.println("Killing platforms");
								IExternalAccess[] platforms = (IExternalAccess[])result;
								for(int i=0; i<platforms.length; i++)
									platforms[i].killComponent();
							}
						}));
					}
				}));
			}
		});
		
		return new Future<Void>(); // never kill?!
	}
}
