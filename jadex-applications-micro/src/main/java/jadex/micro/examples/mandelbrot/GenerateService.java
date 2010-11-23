package jadex.micro.examples.mandelbrot;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.SServiceProvider;

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
	public IFuture generateArea(final double x1, final double y1, final double x2, final double y2, int sizex, int sizey, final int max)
	{
		final Future ret = new Future();
		
		final double stepx = (x2-x1)/sizex;
		final double stepy = (y2-y1)/sizey;
		
		SServiceProvider.getService(agent.getServiceProvider(), ICalculateService.class)
			.addResultListener(agent.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				// Distribute to more than one worker.
				ICalculateService cs = (ICalculateService)result;
				
				cs.calculateArea(x1, y1, x2, y2, stepx, stepy, max).addResultListener(
					agent.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						ret.setResult(result);
					}
				}));
			}
		}));
		
		return ret;
	}
}
