package jadex.micro.examples.mandelbrot;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.SServiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 *  Generate service implementation. 
 */
public class GenerateService extends BasicService implements IGenerateService
{
	//-------- attributes --------
	
	/** The agent. */
	protected GenerateAgent agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public GenerateService(GenerateAgent agent)
	{
		super(agent.getServiceProvider().getId(), IGenerateService.class, null);
		this.agent = agent;
	}
	
	//-------- methods --------
	
	/**
	 *  Generate a specific area using a defined x and y size.
	 */
	public IFuture generateArea(final double x1, final double y1, final double x2, final double y2, int sizex, int sizey, final int max, final int par)
	{
		final Future ret = new Future();
		
		final double stepx = (x2-x1)/sizex;
		final double stepy = (y2-y1)/sizey;
		
		final AreaData data = new AreaData(x1, x2, y1, y2, stepx, stepy, max, par, null);		
		
		
		SServiceProvider.getServices(agent.getServiceProvider(), ICalculateService.class)
			.addResultListener(agent.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				List sers = (List)result;
				
				// Start additional components if necessary
				if(sers.size()<par)
				{
					final int num = par-sers.size();
					final CollectionResultListener lis = new CollectionResultListener(num, true, agent.createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							SServiceProvider.getServices(agent.getServiceProvider(), ICalculateService.class)
								.addResultListener(agent.createResultListener(new DelegationResultListener(ret)
							{
								public void customResultAvailable(Object source, Object result)
								{
									distributeWork(data, (List)result, ret);
								}
							}));
						}
					}));
					
					SServiceProvider.getService(agent.getServiceProvider(), IComponentManagementService.class)
						.addResultListener(agent.createResultListener(agent.createResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							IComponentManagementService cms = (IComponentManagementService)result;
							
							for(int i=0; i<num; i++)
							{
								cms.createComponent(null, "jadex/micro/examples/mandelbrot/CalculateAgent.class", new CreationInfo(agent.getParent().getComponentIdentifier()), null)
									.addResultListener(agent.createResultListener(lis));
							}
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							exception.printStackTrace();
						}
					})));
				}
				else
				{
					distributeWork(data, sers, ret);
				}
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void distributeWork(AreaData data, List services, final Future ret)
	{
		// Distribute to more than one worker.
		
		ICalculateService cs = (ICalculateService)services.get(0);
		
		cs.calculateArea(data.getXStart(), data.getYStart(), data.getXEnd(), data.getYEnd(), data.getStepX(), data.getStepY(), data.getMax())
			.addResultListener(agent.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				ret.setResult(result);
			}
		}));
	}
}
