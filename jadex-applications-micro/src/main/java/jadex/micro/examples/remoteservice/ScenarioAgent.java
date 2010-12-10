package jadex.micro.examples.remoteservice;

import jadex.bridge.IExternalAccess;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;
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
	public void executeBody()
	{
		SServiceProvider.getService(getServiceProvider(), ILibraryService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				ILibraryService libservice = (ILibraryService)result;
				libservice.getURLStrings().addResultListener(createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						String[] libpaths = (String[])((List)result).toArray(new String[0]);
						StartScenario.startScenario(libpaths).addResultListener(createResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
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
		}));
	}
}
