package jadex.micro.examples.mandelbrot;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.CounterResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.SServiceProvider;

import java.awt.Rectangle;
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
		
		SServiceProvider.getService(agent.getServiceProvider(), IDisplayService.class)
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IDisplayService ds = (IDisplayService)result;
				SServiceProvider.getServices(agent.getServiceProvider(), ICalculateService.class)
				.addResultListener(agent.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						List sers = (List)result;
						
						// Start additional components if necessary
						if(sers.size()<data.getParallel())
						{
							final int num = data.getParallel()-sers.size();
							
	//						System.out.println("Starting new calculator agents: "+num);
							
							final CollectionResultListener lis = new CollectionResultListener(num, true, agent.createResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									SServiceProvider.getServices(agent.getServiceProvider(), ICalculateService.class)
										.addResultListener(agent.createResultListener(new DelegationResultListener(ret)
									{
										public void customResultAvailable(Object source, Object result)
										{
											distributeWork(data, (List)result, ds, ret);
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
							distributeWork(data, sers, ds, ret);
						}
					}
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Distribute the work to different worker services.
	 */
	protected void distributeWork(final AreaData data, List services, final IDisplayService ds, final Future ret)
	{
		int num = Math.max(data.getSizeX()*data.getSizeY()*data.getMax()/(data.getTaskSize()*data.getTaskSize()*256), 1);
//		System.out.println("Number of tasks: "+num);
		
		final int xsize = data.getSizeX()/num;
		final int ysize = data.getSizeY()/num;
		int xrest = data.getSizeX()-num*xsize;
		int yrest = data.getSizeY()-num*ysize;
		
		final double xdiff = (data.getXEnd()-data.getXStart())/num;
		final double ydiff = (data.getYEnd()-data.getYStart())/num;
		
		double x1 = data.getXStart();
		double y1 = data.getYStart();
		
		data.setData(new int[data.getSizeX()][data.getSizeY()]);
		
		CounterResultListener lis = new CounterResultListener(num*num)
		{
			public void finalResultAvailable(Object source, Object result)
			{
				intermediateResultAvailable(source, result);
//				System.out.println("res: "+SUtil.arrayToString(data.getData()));
				ret.setResult(data);
			}
			
			public void intermediateResultAvailable(Object source, Object result)
			{
				AreaData ad = (AreaData)result;
				int xs = (int)((int[])ad.getId())[0]*xsize;
				int ys = (int)((int[])ad.getId())[1]*ysize;
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						panel.getStatusBar().setText("Finished: "+getCnt()+"("+getNumber()+")");
					}
				});
				
				if(ds!=null)
				{
					ds.displayIntermediateResult(new ProgressData(null,
						new Rectangle(xs, ys, ad.getSizeX(), ad.getSizeY()), true));
				}
				
//				System.out.println("x:y: end "+xs+" "+ys);
//				System.out.println("partial: "+SUtil.arrayToString(ad.getData()));
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
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				System.out.println("todo: handle failure");
				exception.printStackTrace();
			}
		};
		
		for(int yi=0; yi<num; yi++)
		{
			for(int xi=0; xi<num; xi++)
			{
//				System.out.println("x:y: start "+x1+" "+(x1+xdiff)+" "+y1+" "+(y1+ydiff)+" "+xdiff);
				int idx = (xi+(yi*xi))%services.size();
				ICalculateService cs = (ICalculateService)services.get(idx);
				AreaData ad = new AreaData(x1, x1+xdiff, y1, y1+ydiff, xi==num-1? xsize+xrest: xsize, yi==num-1? ysize+yrest: ysize, data.getMax(), 0, 0, new int[]{xi, yi}, null);
				cs.calculateArea(ad).addResultListener(agent.createResultListener(lis));
				if(ds!=null)
				{
					ds.displayIntermediateResult(new ProgressData(cs.getServiceIdentifier().getProviderId(),
						new Rectangle(xi*xsize, yi*ysize, ad.getSizeX(), ad.getSizeY()), false));
				}
				x1 += xdiff;
			}
			x1 = data.getXStart();
			y1 += ydiff;
		}
	}
}
