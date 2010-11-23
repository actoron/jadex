package jadex.micro.examples.mandelbrot;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.SServiceProvider;

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
		super(agent.getServiceProvider().getId(), IGenerateService.class, null);
		this.agent = agent;
	}
	
	/**
	 * 
	 */
	public IFuture generateArea(final double x1, final double y1, final double x2, final double y2, int sizex, int sizey)
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
				cs.calculateArea(x1, y1, x2, y2, stepx, stepy).addResultListener(
					agent.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						final int[][] res = (int[][])result;
						
						ret.setResult(res);
					}
				}));
			}
		}));
		
		return ret;
	}
}
