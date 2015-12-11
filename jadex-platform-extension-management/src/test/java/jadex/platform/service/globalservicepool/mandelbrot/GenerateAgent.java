package jadex.platform.service.globalservicepool.mandelbrot;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.SwingUtilities;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that can process generate requests.
 */
@Description("Agent offering a generate service.")
@ProvidedServices(@ProvidedService(type=IGenerateService.class))
@RequiredServices({
	@RequiredService(name="displayservice", type=IDisplayService.class, binding=@Binding(create=true, dynamic=true, 
		creationinfo=@CreationInfo(type="Display"))),
	@RequiredService(name="calculateservices", type=ICalculateService.class, multiple=true, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL)),
	@RequiredService(name="cmsservice", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="generateservice", type=IGenerateService.class)
})
@Service
@Agent
public class GenerateAgent implements IGenerateService
{
	//-------- constants --------

	/** The available algorithms. */
	public static IFractalAlgorithm[]	ALGORITHMS	= new IFractalAlgorithm[] {
		new MandelbrotAlgorithm(),
		new LyapunovAlgorithm()
	};
	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The generate panel. */
	protected GeneratePanel panel;
	
	/** The calculate service. */
	protected ICalculateService cs;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	@ServiceStart
	public void start()
	{
//		System.out.println("start: "+agent.getComponentIdentifier());
		this.panel = (GeneratePanel)GeneratePanel.createGui(agent.getExternalAccess());
	}
	
	/**
	 *  Stop the service.
	 */
	@ServiceShutdown
	public IFuture<Void>	shutdown()
	{
//			System.out.println("shutdown: "+agent.getAgentName());
		final Future<Void>	ret	= new Future<Void>();
		if(panel!=null)
		{
//				System.out.println("shutdown1: "+agent.getAgentName());
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
//						System.out.println("shutdown2: "+agent.getAgentName());
					SGUI.getWindowParent(panel).dispose();
					ret.setResult(null);
//						System.out.println("shutdown3: "+agent.getAgentName());
				}
			});
		}
		else
		{
//				System.out.println("shutdown4: "+agent.getAgentName());
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
		final Set<AreaData>	areas	= new HashSet<AreaData>();	// {AreaData}
		long	task	= (long)data.getTaskSize()*data.getTaskSize()*256;
		long	pic	= (long)data.getSizeX()*data.getSizeY()*data.getMax();
		int numx = (int)Math.max(Math.round(Math.sqrt((double)pic/task)), 1);
		int numy = (int)Math.max(Math.round((double)pic/(task*numx)), 1);
		final long	time	= System.nanoTime();	
//			System.out.println("Number of tasks: "+numx+", "+numy+", max="+data.getMax()+" tasksize="+data.getTaskSize());
		
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
				
//					System.out.println("x:y: start "+x1+" "+(x1+xdiff)+" "+y1+" "+(y1+ydiff)+" "+xdiff);
				areas.add(new AreaData(xstart, xend, ystart, yend,
					data.getSizeX()-restx, data.getSizeY()-resty, sizex, sizey,
					data.getMax(), 0, 0, data.getAlgorithm(), null, null, data.getDisplayId()));
//					System.out.println("x:y: "+xi+" "+yi+" "+ad);
				restx	-= sizex;
			}
			resty	-= sizey;
		}

		// Create array for holding results.
		data.setData(new short[data.getSizeX()][data.getSizeY()]);
		
		// Assign tasks to service pool.
		final int number	= areas.size();
		
		if(cs==null)
		{
			cs = SServiceProvider.getLocalService(agent, ICalculateService.class);
			System.out.println("found calculate service: "+cs);
		}
		final IDisplayService ds = SServiceProvider.getLocalService(agent, IDisplayService.class);
			
		final int[] acnt = new int[1];
		final CounterResultListener<AreaData> lis = new CounterResultListener<AreaData>(areas.size(), new IResultListener<Void>() 
		{
			public void resultAvailable(Void result) 
			{
				double	millis	= ((System.nanoTime() - time)/100000)/10.0;
				System.out.println("took: "+millis+" millis.");
//					agent.waitForDelay(5000).get();
				ret.setResult(data);
			}
			
			public void exceptionOccurred(Exception exception) 
			{
				exception.printStackTrace();
				ret.setException(exception);
			}
		})
		{
			public void intermediateResultAvailable(AreaData ad) 
			{
//					System.out.println("rec: "+(acnt[0]++)+"/"+areas.size());
				// Save result in global data
				copyPartialData(ad, data);
			}
		};
		
		System.out.println("generate services delegates to calculate services: "+number);
		Iterator<AreaData> it = areas.iterator();
		for(int i=0; i<areas.size(); i++)// AreaData ad: areas)
		{
			AreaData ad = it.next();
			final ProgressData	pd	= new ProgressData(ad.getCalculatorId(), ad.getId(),
				new Rectangle(ad.getXOffset(), ad.getYOffset(), ad.getSizeX(), ad.getSizeY()),
				0, data.getSizeX(), data.getSizeY(), ad.getDisplayId());
			
			// todo: should know the concrete service?!
			ad.setCalculatorId(((IService)cs).getServiceIdentifier().getProviderId());
			
			final int fi = i;
			cs.calculateArea(ad).addResultListener(new IIntermediateResultListener<CalculateEvent>() 
			{
				public void intermediateResultAvailable(CalculateEvent result) 
				{
//						System.out.println("ires: "+result.getProgressData()+" "+fi+"/"+number);
					pd.setProviderId(result.getCid());
					if(result.getProgressData()!=null)
					{
						pd.setProgress(result.getProgressData());
						ds.displayProgress(pd);  // todo: listener?!
					}
					else
					{
						ds.displayPartialResult(new AreaData(data), result.getAreaData());
						lis.resultAvailable(result.getAreaData());
					}
				}
				
				public void finished() 
				{
//						System.out.println("fini: "+fi+"/"+number);
				}
				
				public void resultAvailable(Collection<CalculateEvent> result) 
				{
					for(CalculateEvent ce: result)
					{
						intermediateResultAvailable(ce);
					}
					finished();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					lis.exceptionOccurred(exception);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param from
	 * @param to
	 */
	public static void copyPartialData(AreaData from, AreaData to)
	{
		int xs = from.getXOffset();
		int ys = from.getYOffset();
		
//			System.out.println("x:y: end "+xs+" "+ys);
//				System.out.println("partial: "+SUtil.arrayToString(ad.getData()));
		for(int yi=0; yi<from.getSizeY(); yi++)
		{
			for(int xi=0; xi<from.getSizeX(); xi++)
			{
				try
				{
					to.fetchData()[xs+xi][ys+yi] = from.fetchData()[xi][yi];
				}
				catch(Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
