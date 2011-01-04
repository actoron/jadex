package jadex.micro.examples.mandelbrot;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.CounterResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 *  Generate service implementation. 
 */
public class GenerateService extends BasicService implements IGenerateService
{
	//-------- attributes --------
	
	/** The agent. */
	protected GenerateAgent agent;
	
	/** The generate panel. */
	protected GeneratePanel panel;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public GenerateService(GenerateAgent agent, GeneratePanel panel)
	{
		super(agent.getServiceProvider().getId(), IGenerateService.class, null);
		this.agent = agent;
		this.panel = panel;
	}
	
	//-------- methods --------
	
	/**
	 *  Generate a specific area using a defined x and y size.
	 */
	public IFuture generateArea(final AreaData data)
	{
		final Future ret = new Future();	
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				panel.updateProperties(data);
			}
		});
		
		agent.getRequiredService("displayservice")
//		SServiceProvider.getService(agent.getServiceProvider(), IDisplayService.class)
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IDisplayService ds = (IDisplayService)result;
				getCalculateServices(agent, data.getParallel()).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						distributeWork(data, (List)result, ds, ret);
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				});
				
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get (and create) the calculate services.
	 */
	protected static IFuture getCalculateServices(final GenerateAgent agent, final int par)
	{
		final Future ret = new Future();
		
//		SServiceProvider.getServices(agent.getServiceProvider(), ICalculateService.class, false, true)
		agent.getRequiredServices("calculateservices")
			.addResultListener(agent.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				List sers = (List)result;
				
				// Start additional components if necessary
				if(sers.size()<par)
				{
					final int num = par-sers.size();
					
//					System.out.println("Starting new calculator agents: "+num);
					
					final CollectionResultListener lis = new CollectionResultListener(num, true, agent.createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
//							SServiceProvider.getServices(agent.getServiceProvider(), ICalculateService.class, false, true)
							agent.getRequiredServices("calculateservices")	
								.addResultListener(agent.createResultListener(new DelegationResultListener(ret)));
						}
					}));
					
//					SServiceProvider.getService(agent.getServiceProvider(), IComponentManagementService.class, false, true)
					agent.getRequiredService("cmsservice")
						.addResultListener(agent.createResultListener(agent.createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							IComponentManagementService cms = (IComponentManagementService)result;
							
//							System.out.println("test: "+agent.getArgument("delay"));
							Object delay = agent.getArgument("delay");
							if(delay==null)
								delay = new Long(5000);
							for(int i=0; i<num; i++)
							{
								cms.createComponent(null, "jadex/micro/examples/mandelbrot/CalculateAgent.class", 
									new CreationInfo(SUtil.createHashMap(new String[]{"delay"}, new Object[]{delay}), 
									agent.getParent().getComponentIdentifier()), null)
									.addResultListener(agent.createResultListener(lis));
							}
						}
					})));
				}
				else
				{
					ret.setResult(sers);
				}
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Distribute the work to different worker services.
	 */
	protected void distributeWork(final AreaData data, Collection sers, final IDisplayService ds, final Future ret)
	{
		if(sers==null || sers.size()==0)
			throw new IllegalArgumentException("Calculate services must not be null");
		List services = new ArrayList(sers);
		
		int numx = Math.max((int)Math.sqrt((double)data.getSizeX()*data.getSizeY()*data.getMax()/(data.getTaskSize()*data.getTaskSize()*256)), 1);
		int numy = numx;
//		System.out.println("Number of tasks: "+numx+", "+numy+", max="+data.getMax()+" tasksize="+data.getTaskSize());
		
		final int sizex = data.getSizeX()/numx;
		final int sizey = data.getSizeY()/numy;
		int restx = data.getSizeX()-numx*sizex;
		int resty = data.getSizeY()-numy*sizey;
		
		// If rest if too large add more chunks
		numx += restx/sizex;
		numy += resty/sizey;
		restx = restx%sizex;
		resty = resty%sizey;
		
		double xdiv = restx==0? numx: ((double)restx)/sizex+numx;
		double ydiv = resty==0? numy: ((double)resty)/sizey+numy;
		
		final double xdiff = (data.getXEnd()-data.getXStart())/xdiv;
		final double ydiff = (data.getYEnd()-data.getYStart())/ydiv;
		
		if(restx>0)
			numx++;
		if(resty>0)
			numy++;
		
//		System.out.println("ad: "+data+" "+numx+" "+restx+" "+xdiff);
		
		double x1 = data.getXStart();
		double y1 = data.getYStart();
		
		data.setData(new int[data.getSizeX()][data.getSizeY()]);
		
		final CounterResultListener lis = new CounterResultListener(numx*numy, new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				ret.setResult(data);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
			}
		})
		{
			public void intermediateResultAvailable(Object result)
			{
				AreaData ad = (AreaData)result;
				int xs = (int)((Number)((Tuple)ad.getId()).getEntity(0)).intValue()*sizex;
				int ys = (int)((Number)((Tuple)ad.getId()).getEntity(1)).intValue()*sizey;
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						panel.getStatusBar().setText("Finished: "+getCnt()+"("+getNumber()+")");
					}
				});
				
				if(ds!=null)
				{
					ds.displayIntermediateResult(new ProgressData(ad.getCalculatorId(), ad.getId(),
						new Rectangle(xs, ys, ad.getSizeX(), ad.getSizeY()), true, data.getSizeX(), data.getSizeY()));
				}
				
	//			System.out.println("x:y: end "+xs+" "+ys);
	//			System.out.println("partial: "+SUtil.arrayToString(ad.getData()));
				for(int yi=0; yi<ad.getSizeY(); yi++)
				{
					for(int xi=0; xi<ad.getSizeX(); xi++)
					{
						try
						{
							data.getData()[xs+xi][ys+yi] = ad.getData()[xi][yi];
						}
						catch(Exception e) 
						{
							e.printStackTrace();
						}
					}
				}
			}
		};
		
		for(int yi=0; yi<numy; yi++)
		{
			for(int xi=0; xi<numx; xi++)
			{
//				System.out.println("x:y: start "+x1+" "+(x1+xdiff)+" "+y1+" "+(y1+ydiff)+" "+xdiff);
				int idx = (xi+(yi*numx))%services.size();
				ICalculateService cs = (ICalculateService)services.get(idx);
				AreaData ad = new AreaData(x1, xi==numx-1 && restx>0 ? x1+(xdiff*restx/sizex): x1+xdiff,
					y1, yi==numy-1 && resty>0 ? y1+(ydiff*resty/sizey) : y1+ydiff,
					xi==numx-1 && restx>0 ? restx : sizex, yi==numy-1 && resty>0 ? resty : sizey,
					data.getMax(), 0, 0, new Tuple(new Integer(xi), new Integer(yi)),
					(IComponentIdentifier)cs.getServiceIdentifier().getProviderId(), null);
//				System.out.println("x:y: "+xi+" "+yi+" "+ad);
//				System.out.println("calculateArea: "+cs.getServiceIdentifier().getProviderId());
				cs.calculateArea(ad).addResultListener(agent.createResultListener(new CalculateListener(agent, lis, ad)));
				if(ds!=null)
				{
					ds.displayIntermediateResult(new ProgressData((IComponentIdentifier)cs.getServiceIdentifier().getProviderId(), ad.getId(),
						new Rectangle(xi*sizex, yi*sizey, ad.getSizeX(), ad.getSizeY()), false, data.getSizeX(), data.getSizeY()));
				}
				x1 += xdiff;
			}
			x1 = data.getXStart();
			y1 += ydiff;
		}
	}
}

/**
 *  Listener for calculate service that can reassign a task.
 */
class CalculateListener implements IResultListener
{
	/** The agent. */
	protected GenerateAgent agent;
	
	/** The listener. */
	protected IResultListener listener;
	
	/** The data. */
	protected AreaData data;
	
	/**
	 *  Create a new listener.
	 */
	public CalculateListener(GenerateAgent agent, IResultListener listener, AreaData data)
	{
		this.agent = agent;
		this.listener = listener;
		this.data = data;
	}
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public void resultAvailable(Object result)
	{
		// Hack!!! Provide provider id.
		listener.resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
		exception.printStackTrace();
		
//		System.out.println("Recal start: "+data.getId());
		GenerateService.getCalculateServices(agent, 1).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
//				System.out.println("Recal end: "+data.getId());
				ICalculateService cs = (ICalculateService)((Collection)result).iterator().next();
//				System.out.println("(re)calculateArea: "+cs.getServiceIdentifier().getProviderId());
				cs.calculateArea(data).addResultListener(agent.createResultListener(CalculateListener.this));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("Recal no success: "+data.getId());
				exception.printStackTrace();
				listener.exceptionOccurred(exception);
			}
		});
	}
}
