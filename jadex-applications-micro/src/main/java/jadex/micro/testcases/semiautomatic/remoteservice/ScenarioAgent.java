package jadex.micro.testcases.semiautomatic.remoteservice;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

import java.util.List;

/**
 *  Just for starting the scenario.
 */
public class ScenarioAgent extends MicroAgent
{
	/**
	 *  Execute the body.
	 */
	public IFuture<Void> executeBody()
	{
		getServiceContainer().searchService(ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ILibraryService libservice = (ILibraryService)result;
//				libservice.getURLStrings().addResultListener(createResultListener(new DefaultResultListener()
				libservice.getAllResourceIdentifiers().addResultListener(createResultListener(new DefaultResultListener()
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
							createResultListener(new DefaultResultListener()
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
