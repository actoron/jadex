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
				SServiceProvider.getServices(agent.getServiceProvider(), ICalculateService.class, false, true)
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
									SServiceProvider.getServices(agent.getServiceProvider(), ICalculateService.class, false, true)
										.addResultListener(agent.createResultListener(new DelegationResultListener(ret)
									{
										public void customResultAvailable(Object source, Object result)
										{
											distributeWork(data, (List)result, ds, ret);
										}
									}));
								}
							}));
							
							SServiceProvider.getService(agent.getServiceProvider(), IComponentManagementService.class, false, true)
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
		
		CounterResultListener lis = new CounterResultListener(numx*numy)
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
				int xs = (int)((int[])ad.getId())[0]*sizex;
				int ys = (int)((int[])ad.getId())[1]*sizey;
				
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
						new Rectangle(xs, ys, ad.getSizeX(), ad.getSizeY()), true, data.getSizeX(), data.getSizeY()));
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
		
		for(int yi=0; yi<numy; yi++)
		{
			for(int xi=0; xi<numx; xi++)
			{
//				System.out.println("x:y: start "+x1+" "+(x1+xdiff)+" "+y1+" "+(y1+ydiff)+" "+xdiff);
				int idx = (xi+(yi*xi))%services.size();
				ICalculateService cs = (ICalculateService)services.get(idx);
				AreaData ad = new AreaData(x1, x1+xdiff, y1, y1+ydiff, xi==numx-1 && restx>0? restx: 
					sizex, yi==numy-1 && resty>0? resty: sizey, data.getMax(), 0, 0, new int[]{xi, yi}, null);
//				System.out.println("x:y: "+xi+" "+yi+" "+ad);
				cs.calculateArea(ad).addResultListener(agent.createResultListener(lis));
				if(ds!=null)
				{
					ds.displayIntermediateResult(new ProgressData(cs.getServiceIdentifier().getProviderId(),
						new Rectangle(xi*sizex, yi*sizey, ad.getSizeX(), ad.getSizeY()), false, data.getSizeX(), data.getSizeY()));
				}
				x1 += xdiff;
			}
			x1 = data.getXStart();
			y1 += ydiff;
		}
	}
}
