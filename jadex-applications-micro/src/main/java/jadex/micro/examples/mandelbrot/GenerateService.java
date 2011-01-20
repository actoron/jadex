package jadex.micro.examples.mandelbrot;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IIntermediateResultListener;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IService;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

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
	
	/** The service pool manager for calculation services. */
	protected ServicePoolManager	manager;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public GenerateService(final GenerateAgent agent, GeneratePanel panel)
	{
		super(agent.getServiceProvider().getId(), IGenerateService.class, null);
		this.agent = agent;
		this.panel = panel;
		this.manager	= new ServicePoolManager(agent, "calculateservices", new IServicePoolHandler()
		{
			public boolean selectService(IService service)
			{
				return true;
			}
			
			public IFuture invokeService(IService service, Object task)
			{
				AreaData	ad	= (AreaData)task;
				ad.setCalculatorId((IComponentIdentifier)service.getServiceIdentifier().getProviderId());
				return ((ICalculateService)service).calculateArea(ad);
			}
			
			public IFuture createService()
			{
				final Future	ret	= new Future();
				agent.getRequiredService("cmsservice").addResultListener(
					agent.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService)result;
						Object delay = agent.getArgument("delay");
						cms.createComponent(null, "jadex/micro/examples/mandelbrot/CalculateAgent.class", 
							new CreationInfo(SUtil.createHashMap(new String[]{"delay"}, new Object[]{delay}), 
							agent.getParent().getComponentIdentifier()), null)
							.addResultListener(agent.createResultListener(new DelegationResultListener(ret)
						{
							// Component created, now get the calculation service.
							public void customResultAvailable(Object result)
							{
								cms.getExternalAccess((IComponentIdentifier)result).addResultListener(
									agent.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										SServiceProvider.getService(((IExternalAccess)result).getServiceProvider(),
											ICalculateService.class, RequiredServiceInfo.SCOPE_LOCAL).addResultListener(
											agent.createResultListener(new DelegationResultListener(ret)
										{
											public void customResultAvailable(Object result)
											{
												ret.setResult(result);
											}
										}));
									}
								}));
							}
						}));
					}
				}));
				return ret;
			}
		}, -1);
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
		
		agent.getRequiredService("displayservice").addResultListener(
			agent.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IDisplayService ds = (IDisplayService)result;
				distributeWork(data, ds, ret);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				distributeWork(data, null, ret);
			}
		}));
		return ret;
	}
	
	/**
	 *  Distribute the work to available or newly created calculation services.
	 */
	protected void	distributeWork(final AreaData data, final IDisplayService ds, final Future ret)
	{
		// Split area into work units.
		final Set	areas	= new HashSet();	// {AreaData}
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
		
		double xdiff = (data.getXEnd()-data.getXStart())/xdiv;
		double ydiff = (data.getYEnd()-data.getYStart())/ydiv;
		
		if(restx>0)
			numx++;
		if(resty>0)
			numy++;
		
//		System.out.println("ad: "+data+" "+numx+" "+restx+" "+xdiff);
		
		double x1 = data.getXStart();
		double y1 = data.getYStart();
				
		for(int yi=0; yi<numy; yi++)
		{
			for(int xi=0; xi<numx; xi++)
			{
//				System.out.println("x:y: start "+x1+" "+(x1+xdiff)+" "+y1+" "+(y1+ydiff)+" "+xdiff);
				areas.add(new AreaData(x1, xi==numx-1 && restx>0 ? x1+(xdiff*restx/sizex): x1+xdiff,
					y1, yi==numy-1 && resty>0 ? y1+(ydiff*resty/sizey) : y1+ydiff,
					xi==numx-1 && restx>0 ? restx : sizex, yi==numy-1 && resty>0 ? resty : sizey,
					data.getMax(), 0, 0, new Tuple(new Integer(xi), new Integer(yi)),
					null, null));
//				System.out.println("x:y: "+xi+" "+yi+" "+ad);
				x1 += xdiff;
			}
			x1 = data.getXStart();
			y1 += ydiff;
		}

		// Create array for holding results.
		data.setData(new int[data.getSizeX()][data.getSizeY()]);
		
		// Assign tasks to service pool.
		final int number	= areas.size();
		manager.setMax(data.getParallel());
		manager.performTasks(areas, true).addResultListener(agent.createResultListener(
			new IIntermediateResultListener()
		{
			int	cnt	= 0;
			
			public void resultAvailable(Object result)
			{
				// ignored.
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				ret.setExceptionIfUndone(exception);
			}
			
			public void intermediateResultAvailable(Object result)
			{
				AreaData ad = (AreaData)result;
				int xs = (int)((Number)((Tuple)ad.getId()).getEntity(0)).intValue()*sizex;
				int ys = (int)((Number)((Tuple)ad.getId()).getEntity(1)).intValue()*sizey;
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						panel.getStatusBar().setText("Finished: "+(++cnt)+"("+number+")");
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
			
			public void finished()
			{
				ret.setResult(data);
			}
		}));
	}
}
