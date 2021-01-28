package jadex.micro.examples.mandelbrot_new;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.SwingUtilities;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.gui.SGUI;

/**
 *  Generate service implementation. 
 */
@Service
public class GenerateService implements IGenerateService
{
	//-------- constants --------
	
	/** The available algorithms. */
	public static IFractalAlgorithm[]	ALGORITHMS	= new IFractalAlgorithm[] 
	{
		new MandelbrotAlgorithm(),
		new LyapunovAlgorithm()
	};
	
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The generate panel. */
	protected GeneratePanel panel;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	//@ServiceStart
	@OnStart
	public void start()
	{
//		System.out.println("start: "+agent.getAgentName());
		this.panel = (GeneratePanel)GeneratePanel.createGui(agent.getExternalAccess());
	}
	
	/**
	 *  Stop the service.
	 */
	//@ServiceShutdown
	@OnEnd
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
	 *  Calculate and display the default image from current settings.
	 */
	public IFuture<Void> calcDefaultImage()
	{
		panel.calcDefaultImage();
		return IFuture.DONE;
	}
	
	/**
	 *  Generate a specific area using a defined x and y size.
	 */
	public IFuture<AreaData> generateArea(final AreaData data)
	{
		GenerateAgent ga = (GenerateAgent)agent.getFeature(IPojoComponentFeature.class).getPojoAgent();
		
		if(ga.getCalculateService()==null)
			return new Future<AreaData>(new RuntimeException("No calculate service available"));
		
		// Update own gui settings
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
		final Set<AreaData>	areas = new HashSet<>();	// {AreaData}
		long task = (long)data.getTaskSize()*data.getTaskSize()*256;
		long pic	= (long)data.getSizeX()*data.getSizeY()*data.getMax();
		int numx = (int)Math.max(Math.round(Math.sqrt((double)pic/task)), 1);
		int numy = (int)Math.max(Math.round((double)pic/(task*numx)), 1);
		
		//numx = 2;
	    //numy = 1;

//		final long	time	= System.nanoTime();	
		//System.out.println("Number of tasks: "+numx+", "+numy+", max="+data.getMax()+" tasksize="+data.getTaskSize());
		
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
					data.getMax(), 0, data.getAlgorithm(), null, null, data.getDisplayId(), data.getChunkCount()));
//				System.out.println("x:y: "+xi+" "+yi+" "+ad);
				restx	-= sizex;
			}
			resty	-= sizey;
		}

		// Create array for holding results.
		data.setData(new short[data.getSizeX()][data.getSizeY()]);
		
		// Assign tasks to service pool.
		final int number = areas.size();
		
		//System.out.println("tasks: "+areas);
		
		//manager.setMax(data.getParallel());
		performTasks(areas, true, data).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(
			new IntermediateEmptyResultListener<AreaData>()
		{
			int	cnt	= 0;
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex in perform tasks: "+exception);
				ret.setExceptionIfUndone(exception);
			}
			
			public void intermediateResultAvailable(AreaData ad)
			{
				if(ad.fetchData()==null)
					return;
				
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
	
	/**
	 *  Perform the given tasks using available or newly created services.
	 *  @param tasks	The set of tasks to be performed.
	 *  @param retry	True, when failed tasks should be retried.
	 *  @param user	User data that is provided for service selection, creation, invocation (if any).
	 *  @return	A future with intermediate and final results. 
	 */
	public IIntermediateFuture<AreaData> performTasks(Set<AreaData> tasks, boolean retry, Object user)
	{
//		System.out.println("Peforming "+tasks.size()+" tasks");
//		System.out.println("Performing tasks: busy="+busy.size()+", free="+free.size());
		
		// Allocation data binds tasks together to a single result future.
		AllocationData ad = new AllocationData(tasks.size(), retry, user);
		
		for(Iterator<AreaData> it=tasks.iterator(); it.hasNext(); )
		{
			performTask(it.next(), ad);
		}
		
		// finished parts are added via ad.taskFinished(part);
		return ad.getResult();
	}
	
	public Future<AreaData> performTask(AreaData task, AllocationData alda)
	{
		//System.out.println("perform task start: "+task);
		Future<AreaData> ret = new Future<>();
		
		//final Object task	=	this.tasks.keySet().iterator().next();
		//final AllocationData	ad	= (AllocationData)this.tasks.remove(task);
//		System.out.println("started service: "+service.getId()+", "+task);
		final AreaData complete = (AreaData)alda.getUserData();	// complete area
		final AreaData part = (AreaData)task;					// part
		
//		System.out.println("invoke: "+service);
		IFuture<IDisplayService> futd = agent.getFeature(IRequiredServicesFeature.class).getService("displayservice");
		futd.then(ds ->
		{
//			System.out.println("display: "+result);
			//final ProgressData pd = new ProgressData(part.getCalculatorId(), part.getId(),
			//	new Rectangle(part.getXOffset(), part.getYOffset(), part.getSizeX(), part.getSizeY()),
			//	0, complete.getSizeX(), complete.getSizeY(), part.getDisplayId());
			
			// Inform display service that the following area/size is calculated
			ds.displayResult(complete).then(vo ->
			{
				// inform display service about the partitioning is calculated
				//ds.displayIntermediateResult(pd).then(v ->
				//{
					//System.out.println("calc start: "+Thread.currentThread());
					
					IFuture<ICalculateService> futc = agent.getFeature(IRequiredServicesFeature.class).getService("calculateservice");
					
					futc.then(cs -> 
					{
						cs.calculateArea(part).addResultListener(new IIntermediateResultListener<PartDataChunk>() 
						{
							public void resultAvailable(Collection<PartDataChunk> results)
							{
							}
							
							public void intermediateResultAvailable(PartDataChunk chunk)
							{
								System.out.println("generate got chunk (calls display): "+chunk);
								
								chunk.setDisplayId(part.getDisplayId());
								chunk.setArea(new Rectangle(part.getXOffset(), part.getYOffset(), part.getSizeX(), part.getSizeY()));
								chunk.setImageWidth(complete.getSizeX());
								chunk.setImageHeight(complete.getSizeY());
								
								part.addChunk(chunk);
								
								// Inform display service that a chunk is finsihed
								ds.displayIntermediateResult(chunk).then(v2 -> {}
									//System.out.println("da")
									// Use result from calculation service instead of result from display service.
									//ret.setResult(calcresult)
								).catchErr(ex -> {}
									//System.out.println("da2: "+ex)
									// Use result from calculation service instead of exception from display service.
									//ret.setResult(calcresult)
								);
							}
							
						    public void finished()
						    {
						    	// Add result of task execution.
								alda.taskFinished(part);
								//System.out.println("perform task ended: "+task);
								ret.setResult(part);
						    }
						    
							public void maxResultCountAvailable(int max)
							{
							}
							
							public void exceptionOccurred(Exception e)
							{
								System.out.println("ex");
								System.out.println("exception during task execution: "+e);
								performTask(task, alda).delegate(ret);
							}
						});
						
						/*cs.calculateArea(part).next(chunk ->
						{
							System.out.println("generate got chunk (calls display): "+chunk);
							
							chunk.setDisplayId(part.getDisplayId());
							chunk.setArea(new Rectangle(part.getXOffset(), part.getYOffset(), part.getSizeX(), part.getSizeY()));
							chunk.setImageWidth(complete.getSizeX());
							chunk.setImageHeight(complete.getSizeY());
							
							part.addChunk(chunk);
							
							// Inform display service that a chunk is finsihed
							ds.displayIntermediateResult(chunk).then(v2 -> {}
								//System.out.println("da")
								// Use result from calculation service instead of result from display service.
								//ret.setResult(calcresult)
							).catchErr(ex -> {}
								//System.out.println("da2: "+ex)
								// Use result from calculation service instead of exception from display service.
								//ret.setResult(calcresult)
							);
						})
						.finished(Void ->
						{
							// Add result of task execution.
							alda.taskFinished(part);
							//System.out.println("perform task ended: "+task);
							ret.setResult(part);
						})
						.catchErr(ex -> 
						{
	//						System.out.println("ex");
							System.out.println("exception during task execution: "+ex);
							performTask(task, alda).delegate(ret);
						});*/
					}).catchErr(ex ->
					{
						System.out.println("ex: "+ex);
						ret.setException(ex);
					}
					);
//				}).catchErr(ex -> 
//				{	
//					System.out.println("ex: "+ex);
//					ret.setException(ex);
//				});
			}).catchErr(ex -> 
			{	
				System.out.println("ex: "+ex);
				ret.setException(ex);
			});
		}).catchErr(ex -> 
		{	
			System.out.println("ex: "+ex); 
			ret.setException(ex);
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	/*public Future<AreaData> performTask(AreaData task, AllocationData alda)
	{
		Future<AreaData> ret = new Future<>();
		
		//final Object task	=	this.tasks.keySet().iterator().next();
		//final AllocationData	ad	= (AllocationData)this.tasks.remove(task);
//		System.out.println("started service: "+service.getId()+", "+task);
		final AreaData global = (AreaData)alda.getUserData();	// global area
		final AreaData part = (AreaData)task;					// part
		
//		System.out.println("invoke: "+service);
		IFuture<IDisplayService> futd = agent.getFeature(IRequiredServicesFeature.class).getService("displayservice");
		futd.then(ds ->
		{
//			System.out.println("display: "+result);
			final ProgressData	pd	= new ProgressData(part.getCalculatorId(), part.getId(),
				new Rectangle(part.getXOffset(), part.getYOffset(), part.getSizeX(), part.getSizeY()),
				false, global.getSizeX(), global.getSizeY(), part.getDisplayId());
			
			// Inform display service that a part is calculated
			ds.displayIntermediateResult(pd).then(v ->
			{
				//System.out.println("calc start: "+Thread.currentThread());
				IFuture<ICalculateService> futc = agent.getFeature(IRequiredServicesFeature.class).getService("calculateservice");
				futc.then(cs -> 
				{
					cs.calculateArea(part).then(calcresult ->
					{
						// Add result of task execution.
						alda.taskFinished(calcresult);
						System.out.println("calc end: "+part);
						pd.setFinished(true);
						// Inform display service that a part is finsihed
						ds.displayIntermediateResult(pd).then(v2 ->
//							System.out.println("da");
							// Use result from calculation service instead of result from display service.
							ret.setResult(calcresult)
						).catchErr(ex ->
//							System.out.println("da2");
							// Use result from calculation service instead of exception from display service.
							ret.setResult(calcresult)
						);
					}).catchErr(ex -> 
					{
//						System.out.println("ex");
						System.out.println("exception during task execution: "+ex);
						performTask(task, alda).delegate(ret);
					});
				}).catchErr(ex ->
				{
					System.out.println("ex: "+ex);
					ret.setException(ex);
				}
				);
			}).catchErr(ex -> 
			{	
				System.out.println("ex: "+ex);
				ret.setException(ex);
			});
		}).catchErr(ex -> 
		{	
			System.out.println("ex: "+ex); 
			ret.setException(ex);
		});
		
		return ret;
	}*/
	
	/**
	 *  Handler for a single task allocation.
	 */
	public static class	AllocationData
	{
		//-------- attributes --------
		
		/** The counter for open tasks, i.e. number of assigned and unassigned tasks that are not yet finished. */
		protected int open;
		
		/** The retry flag, i.e. if failed tasks should be assigned again. */
		protected boolean retry;
		
		/** The user data (if any). */
		protected Object user;
		
		/** The result future. */
		protected IntermediateFuture result;
		
		//-------- constructors --------
		
		/**
		 *  Create a new allocation data.
		 */
		public AllocationData(int open, boolean retry, Object user)
		{
			this.open	= open;
			this.retry	= retry;
			this.user	= user;
			this.result	= new IntermediateFuture();
		}
		
		//-------- methods --------
		
		/**
		 *  Get the user data.
		 *  @return The user data (if any).
		 */
		public Object	getUserData()
		{
			return user;
		}
		
		/**
		 *  Get the result.
		 *  @return The intermediate results future.
		 */
		public IntermediateFuture	getResult()
		{
			return result;
		}
				
		/**
		 *  Add an intermediate result.
		 */
		public void	taskFinished(Object result)
		{
			this.result.addIntermediateResult(result);
			open--;
			
			if(open==0)
			{
				this.result.setFinished();
			}
		}
		
		/**
		 *  A task has failed and is not retried.
		 */
		public void	taskFailed(Exception e)
		{
			// Todo: post exception in result?
			e.printStackTrace();
			
			open--;
			if(open==0)
			{
				this.result.setFinished();
			}
		}
		
		/**
		 *  Test if the retry flag is set.
		 *  @return True, if the retry flag is set.
		 */
		public boolean	isRetry()
		{
			return retry;
		}
	}
}
