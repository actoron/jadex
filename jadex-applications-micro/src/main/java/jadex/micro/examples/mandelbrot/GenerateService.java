package jadex.micro.examples.mandelbrot;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;

/**
 *  Generate service implementation. 
 */
@Service
public class GenerateService implements IGenerateService
{
	//-------- constants --------
	
	/** The available algorithms. */
	public static IFractalAlgorithm[]	ALGORITHMS	= new IFractalAlgorithm[] {
		new MandelbrotAlgorithm(),
		new LyapunovAlgorithm()
	};
	
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The generate panel. */
	protected GeneratePanel panel;
	
	/** The service pool manager for calculation services. */
	protected ServicePoolManager	manager;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	@ServiceStart
	public void start()
	{
//		System.out.println("start: "+agent.getAgentName());
		this.panel = (GeneratePanel)GeneratePanel.createGui(agent.getExternalAccess());
		
		this.manager = new ServicePoolManager(agent, "calculateservices", new IServicePoolHandler()
		{
			public boolean selectService(IService service)
			{
				return true;
			}
			
			public IFuture invokeService(final IService service, Object task, Object user)
			{
				final Future	ret	= new Future();
				
				final AreaData	ad	= (AreaData)task;	// single cutout of area
				final AreaData	data	= (AreaData)user;	// global area
				ad.setCalculatorId((IComponentIdentifier)service.getServiceIdentifier().getProviderId());
				
//				System.out.println("invoke: "+service);
				agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("displayservice").addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
//						System.out.println("display: "+result);
						final IDisplayService	ds	= (IDisplayService)result;
						final ProgressData	pd	= new ProgressData(ad.getCalculatorId(), ad.getId(),
							new Rectangle(ad.getXOffset(), ad.getYOffset(), ad.getSizeX(), ad.getSizeY()),
							false, data.getSizeX(), data.getSizeY(), ad.getDisplayId());
						ds.displayIntermediateResult(pd).addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
//								System.out.println("calc start: "+service);
								((ICalculateService)service).calculateArea(ad).addResultListener(
									new DelegationResultListener(ret)
								{
									public void customResultAvailable(final Object calcresult)
									{
//										System.out.println("calc end");
										pd.setFinished(true);
										ds.displayIntermediateResult(pd).addResultListener(new IResultListener()
										{
											public void resultAvailable(Object result)
											{
//												System.out.println("da");
												// Use result from calculation service instead of result from display service.
												ret.setResult(calcresult);
											}
											
											public void exceptionOccurred(Exception exception)
											{
//												System.out.println("da2");
												// Use result from calculation service instead of exception from display service.
												ret.setResult(calcresult);
											}
										});
									}
									public void exceptionOccurred(Exception exception)
									{
//										System.out.println("ex");
										super.exceptionOccurred(exception);
									}
								});
							}
							public void exceptionOccurred(Exception exception)
							{
								((ICalculateService)service).calculateArea(ad).addResultListener(new DelegationResultListener(ret));
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						((ICalculateService)service).calculateArea(ad).addResultListener(new DelegationResultListener(ret));
					}
				});
				
				return ret;
			}
			
			public IFuture createService()
			{
				final Future	ret	= new Future();
				agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cmsservice").addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService)result;
						Object delay = agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("delay");
						if(delay==null)
							delay = Long.valueOf(5000);
						cms.createComponent(null, "jadex/micro/examples/mandelbrot/CalculateAgent.class", 
							new CreationInfo(SUtil.createHashMap(new String[]{"delay"}, new Object[]{delay}), 
							agent.getComponentIdentifier().getParent()), null)
							.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
						{
							// Component created, now get the calculation service.
							public void customResultAvailable(Object result)
							{
								cms.getExternalAccess((IComponentIdentifier)result).addResultListener(
									agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										SServiceProvider.getService((IExternalAccess)result,
											ICalculateService.class, RequiredServiceInfo.SCOPE_LOCAL).addResultListener(
											agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
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
				});
				return ret;
			}
		}, -1);
	}
	
	/**
	 *  Stop the service.
	 */
	@ServiceShutdown
	public IFuture<Void>	shutdown()
	{
//		System.out.println("shutdown: "+agent.getAgentName());
		final Future<Void>	ret	= new Future<Void>();
		if(panel!=null)
		{
//			System.out.println("shutdown1: "+agent.getAgentName());
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
//					System.out.println("shutdown2: "+agent.getAgentName());
					SGUI.getWindowParent(panel).dispose();
					ret.setResult(null);
//					System.out.println("shutdown3: "+agent.getAgentName());
				}
			});
		}
		else
		{
//			System.out.println("shutdown4: "+agent.getAgentName());
			ret.setResult(null);
		}
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Generate a specific area using a defined x and y size.
	 */
	public IFuture<AreaData> generateArea(final AreaData data)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				panel.updateProperties(data);
			}
		});
		
		return distributeWork(data);
	}
	
	/**
	 *  Distribute the work to available or newly created calculation services.
	 */
	protected IFuture<AreaData>	distributeWork(final AreaData data)
	{
		final Future<AreaData> ret = new Future<AreaData>();	

		// Split area into work units.
		final Set	areas	= new HashSet();	// {AreaData}
		long	task	= (long)data.getTaskSize()*data.getTaskSize()*256;
		long	pic	= (long)data.getSizeX()*data.getSizeY()*data.getMax();
		int numx = (int)Math.max(Math.round(Math.sqrt((double)pic/task)), 1);
		int numy = (int)Math.max(Math.round((double)pic/(task*numx)), 1);
//		final long	time	= System.nanoTime();	
//		System.out.println("Number of tasks: "+numx+", "+numy+", max="+data.getMax()+" tasksize="+data.getTaskSize());
		
		double	areawidth	= data.getXEnd() - data.getXStart();
		double	areaheight	= data.getYEnd() - data.getYStart();
		int	numx0	=numx;
		
		int	resty	= data.getSizeY();
		for(; numy>0; numy--)
		{
			int	sizey	= (int)Math.round((double)resty/numy);
			double	ystart	= data.getYStart() + areaheight*(((double)data.getSizeY()-resty)/data.getSizeY());
			double	yend	= data.getYStart() + areaheight*(((double)data.getSizeY()-(resty-sizey))/data.getSizeY()); 

			int	restx	= data.getSizeX();
			for(numx=numx0; numx>0; numx--)
			{
				int	sizex	= (int)Math.round((double)restx/numx);
				double	xstart	= data.getXStart() + areawidth*(((double)data.getSizeX()-restx)/data.getSizeX()); 
				double	xend	= data.getXStart() + areawidth*(((double)data.getSizeX()-(restx-sizex))/data.getSizeX()); 
				
//				System.out.println("x:y: start "+x1+" "+(x1+xdiff)+" "+y1+" "+(y1+ydiff)+" "+xdiff);
				areas.add(new AreaData(xstart, xend, ystart, yend,
					data.getSizeX()-restx, data.getSizeY()-resty, sizex, sizey,
					data.getMax(), 0, 0, data.getAlgorithm(), null, null, data.getDisplayId()));
//				System.out.println("x:y: "+xi+" "+yi+" "+ad);
				restx	-= sizex;
			}
			resty	-= sizey;
		}

		// Create array for holding results.
		data.setData(new short[data.getSizeX()][data.getSizeY()]);
		
		// Assign tasks to service pool.
		final int number	= areas.size();
		manager.setMax(data.getParallel());
		manager.performTasks(areas, true, data).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
			new IIntermediateResultListener<Object>()
		{
			int	cnt	= 0;
			
			public void resultAvailable(Collection result)
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
				int xs = ad.getXOffset();
				int ys = ad.getYOffset();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						panel.getStatusBar().setText("Finished: "+(++cnt)+"("+number+")");
					}
				});
				
	//			System.out.println("x:y: end "+xs+" "+ys);
	//			System.out.println("partial: "+SUtil.arrayToString(ad.getData()));
				for(int yi=0; yi<ad.getSizeY(); yi++)
				{
					for(int xi=0; xi<ad.getSizeX(); xi++)
					{
						try
						{
							data.fetchData()[xs+xi][ys+yi] = ad.fetchData()[xi][yi];
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
//				double	millis	= ((System.nanoTime() - time)/100000)/10.0;
//				System.out.println("took: "+millis+" millis.");
				ret.setResult(data);
			}
		}));
		
		return ret;
	}
}
