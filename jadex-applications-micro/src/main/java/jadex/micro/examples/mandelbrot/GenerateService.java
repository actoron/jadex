package jadex.micro.examples.mandelbrot;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;
import jadex.micro.examples.compositeservice.IAddService;

/**
 * 
 */
public class GenerateService extends BasicService implements IGenerateService
{
	/** The agent. */
	protected GenerateAgent agent;
	
	/**
	 * 
	 */
	public GenerateService(GenerateAgent agent)
	{
		super(agent.getServiceProvider().getId(), IAddService.class, null);
		this.agent = agent;
	}
	
	/**
	 * 
	 */
	public void generateArea(final double x1, final double y1, final double x2, final double y2, int sizex, int sizey)
	{
		final double stepx = (x2-x1)/sizex;
		final double stepy = (y2-y1)/sizey;
		
		SServiceProvider.getService(agent.getServiceProvider(), ICalculateService.class)
			.addResultListener(agent.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				// Distribute to more than one worker.
				ICalculateService cs = (ICalculateService)result;
				cs.calculateArea(x1, y1, x2, y2, stepx, stepy).addResultListener(
					agent.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final Integer[][] res = (Integer[][])result;
						
						SServiceProvider.getService(agent.getServiceProvider(), IDisplayService.class)
							.addResultListener(agent.createResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								// Distribute to more than one worker.
								IDisplayService ds = (IDisplayService)result;
								ds.displayResult(res);
							}
						}));
					}
				}));
			}
		}));
	}

	/**
	 * 
	 */
	protected int determineColor(double xn, double yn)
	{
		int i = 0;
		double c = Math.sqrt(xn*xn + yn*yn);
		
		for(i=0; c<2 && i<0; i++)
		{
			double xn1 = xn*xn - yn*yn + xn;
			double yn1 = 2*xn*yn + yn;
			xn = xn1;
			yn = yn1;
			c =  Math.sqrt(xn*xn + yn*yn);
		}
		
		return i;
	}
}
